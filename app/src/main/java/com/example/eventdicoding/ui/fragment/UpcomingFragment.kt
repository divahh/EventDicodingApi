package com.example.eventdicoding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.R
import com.example.eventdicoding.data.Injection
import com.example.eventdicoding.data.api.ApiConfig
import com.example.eventdicoding.data.database.SettingPreferences
import com.example.eventdicoding.data.database.dataStore
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentUpcomingBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.viewmodel.EventViewModel
import com.example.eventdicoding.viewmodel.EventViewModelFactory
import com.example.eventdicoding.viewmodel.FavouriteViewModel
import com.example.eventdicoding.viewmodel.SettingViewModel
import com.example.eventdicoding.viewmodel.SettingViewModelFactory

@Suppress("DEPRECATION")
class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { EventRepository(ApiConfig.getServiceApi()) }
    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(repository)
    }

    private val favouriteViewModel: FavouriteViewModel by viewModels {
        Injection.provideFavoriteViewModelFactory(requireContext())
    }
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val modeSett = SettingPreferences.getInstance(requireContext().dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(modeSett))[SettingViewModel::class.java]
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Initialize adapter and RecyclerView
        adapter = EventAdapter { event -> navigateToDetail(event) }
        binding.rvUpcomingEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@UpcomingFragment.adapter
        }

        // Observe upcoming events data from ViewModel
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        adapter.onFavoriteClick = { event, isFavorite -> toggleFavorite(event, isFavorite) }

        favouriteViewModel.favouriteEvents.observe(viewLifecycleOwner) { favouriteEvents ->
            val updatedActiveEvents = adapter.currentList.map { event ->
                event.copy(isFavourite = favouriteEvents.any { it.id == event.id })
            }
            adapter.submitList(updatedActiveEvents)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), "Halaman Upcoming Event tidak dapat dimuat", Toast.LENGTH_SHORT).show()
            }
        }

        // Load upcoming events
        viewModel.loadUpcomingEvents()
    }

    private fun toggleFavorite(event: EventItem, isFavorite: Boolean) {
        if (isFavorite) {
            favouriteViewModel.addFavourite(event.toFavouriteEvent())
            Toast.makeText(context, "Ditambahkan ke Favourite: ${event.nameEvent}", Toast.LENGTH_SHORT).show()
        } else {
            favouriteViewModel.removeFavourite(event.toFavouriteEvent())
            Toast.makeText(context, "Dihapus dari Favourite: ${event.nameEvent}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDetail(event: EventItem) {
        event.id.let { id ->
            val detailFragment = EventDetailFragment().apply {
                arguments = Bundle().apply { putInt("eventId", id) }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::adapter.isInitialized) {
            outState.putParcelableArrayList("events", ArrayList(adapter.currentList))
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getParcelableArrayList<EventItem>("events")?.let {
            adapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        val appCompatActivity = activity as? AppCompatActivity
        appCompatActivity?.supportActionBar?.apply {
            title = "Event Dicoding"
            setDisplayHomeAsUpEnabled(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

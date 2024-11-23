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
import com.example.eventdicoding.data.database.SettingPreferences
import com.example.eventdicoding.data.database.dataStore
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.databinding.FragmentFavouriteBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.viewmodel.FavouriteViewModel
import com.example.eventdicoding.viewmodel.SettingViewModel
import com.example.eventdicoding.viewmodel.SettingViewModelFactory

@Suppress("DEPRECATION")
class FavouriteFragment : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteViewModel by viewModels {
        Injection.provideFavoriteViewModelFactory(requireContext())
    }
    private lateinit var adapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
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

        // Initialize adapter with toggle favorite functionality
        adapter = EventAdapter { event -> navigateToDetail(event) }.apply {
            onFavoriteClick = { event, isFavorite -> toggleFavorite(event, isFavorite) }
        }

        binding.rvFavouriteEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavouriteFragment.adapter
        }

        // Observe favorite events from ViewModel
        viewModel.favouriteEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            viewModel.setLoadingState(false)
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.setLoadingState(true)
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleFavorite(event: EventItem, isFavorite: Boolean) {
        if (isFavorite) {
            viewModel.addFavourite(event.toFavouriteEvent())
            Toast.makeText(context, "Ditambahkan ke Favourite: ${event.nameEvent}", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.removeFavourite(event.toFavouriteEvent())
            Toast.makeText(context, "Dihapus dari Favourite: ${event.nameEvent}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToDetail(event: EventItem) {
        val detailFragment = EventDetailFragment().apply {
            arguments = Bundle().apply { putInt("eventId", event.id) }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, detailFragment)
            .addToBackStack(null)
            .commit()
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

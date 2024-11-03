package com.example.eventdicoding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.R
import com.example.eventdicoding.data.api.ApiConfig
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentFinishedBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.viewmodel.EventViewModel
import com.example.eventdicoding.viewmodel.EventViewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { EventRepository(ApiConfig.getServiceApi()) }
    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(repository)
    }
    private lateinit var adapter: EventAdapter
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventAdapter { event -> navigateToDetail(event) }
        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FinishedFragment.adapter
        }

        // Set up swipe to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadFinishedEvents()
        }

        // Observe finished events data
        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Observe search results data
        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            adapter.submitList(searchResults)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // Observe loading state for SwipeRefreshLayout
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefreshLayout.isRefreshing = isLoading
        }

        // Observe error messages and show them as toast
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), "Halaman Finished Event tidak dapat dimuat", Toast.LENGTH_SHORT).show()
            }
        }

        // Initial load of finished events
        viewModel.loadFinishedEvents()

        setupSearch()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()

                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    newText?.let {
                        delay(300)
                        performSearch(it)
                    }
                }
                return true
            }
        })

        viewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isEmpty()) {
                Toast.makeText(requireContext(), "Tidak ada event berdasarkan kata kunci!", Toast.LENGTH_SHORT).show()
            }
            adapter.submitList(searchResults)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun performSearch(query: String) {
        viewModel.searchFinishedEvents(query)
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

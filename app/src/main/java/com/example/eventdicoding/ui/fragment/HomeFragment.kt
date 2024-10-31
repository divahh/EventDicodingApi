package com.example.eventdicoding.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.R
import com.example.eventdicoding.data.api.ApiConfig
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentHomeBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.viewmodel.EventViewModel
import com.example.eventdicoding.viewmodel.EventViewModelFactory

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(repository)
    }

    private val repository by lazy { EventRepository(ApiConfig.getServiceApi()) }
    private lateinit var activeAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up adapters
        activeAdapter = EventAdapter { event -> navigateToDetail(event) }
        finishedAdapter = EventAdapter { event -> navigateToDetail(event) }

        binding.rvActiveEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = activeAdapter
        }

        binding.rvFinishedEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = finishedAdapter
        }

        // Observe data from ViewModel
        viewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            activeAdapter.submitList(events.take(5))
        }

        viewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            finishedAdapter.submitList(events.take(5))
        }

        // Load events
        viewModel.loadUpcomingEvents()
        viewModel.loadFinishedEvents()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
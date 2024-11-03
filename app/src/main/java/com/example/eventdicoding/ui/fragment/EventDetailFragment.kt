package com.example.eventdicoding.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.eventdicoding.data.api.ApiConfig
import com.example.eventdicoding.data.repository.EventRepository
import com.example.eventdicoding.databinding.FragmentEventDetailBinding
import com.example.eventdicoding.viewmodel.EventViewModel
import com.example.eventdicoding.viewmodel.EventViewModelFactory

@Suppress("DEPRECATION")
class EventDetailFragment : Fragment() {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val repository by lazy { EventRepository(ApiConfig.getServiceApi()) }
    private val viewModel: EventViewModel by viewModels {
        EventViewModelFactory(repository)
    }
    private var eventId: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()

        eventId = savedInstanceState?.getInt("eventId") ?: arguments?.getInt("eventId")

        if (eventId != null) {
            viewModel.loadEventDetail(eventId!!)
        } else {
            Toast.makeText(requireContext(), "Event ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), "Halaman Detail tidak dapat dimuat", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.eventDetail.observe(viewLifecycleOwner) { eventDetail ->
            eventDetail?.let {
                binding.eventName.text = it.nameEvent
                binding.eventOwner.text = it.ownerEvent
                binding.eventTime.text = it.beginTime
                binding.eventQuota.text = it.quota.toString()
                val remaining = it.quota - it.registrant
                binding.eventRegistrant.text = remaining.toString()
                val parsedDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(it.description, Html.FROM_HTML_MODE_LEGACY)
                } else {
                    Html.fromHtml(it.description)
                }
                binding.eventDescription.text = parsedDescription
                Glide.with(this)
                    .load(it.imgEvent)
                    .into(binding.eventImage)
            } ?: run {
                Toast.makeText(context, "Event details tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        binding.openLinkButton.setOnClickListener {
            viewModel.eventDetail.value?.link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } ?: Toast.makeText(context, "Link tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        eventId?.let { outState.putInt("eventId", it) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.getInt("eventId")?.let { restoredEventId ->
            if (eventId == null) {
                eventId = restoredEventId
                viewModel.loadEventDetail(eventId!!)
            }
        }
    }

    private fun setupActionBar() {
        val appCompatActivity = activity as? AppCompatActivity
        appCompatActivity?.supportActionBar?.apply {
            title = "Event Detail"
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

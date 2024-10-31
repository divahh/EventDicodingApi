package com.example.eventdicoding.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt("eventId") ?: return
        viewModel.loadEventDetail(eventId)

        viewModel.eventDetail.observe(viewLifecycleOwner) { eventDetail ->
            eventDetail?.let {
                // Update UI dengan detail event
                binding.eventName.text = it.nameEvent
                binding.eventOwner.text = it.ownerEvent
                binding.eventTime.text = it.beginTime
                binding.eventQuota.text = it.quota.toString()
                binding.eventRegistrant.text = it.registrant.toString()
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
                // Tampilkan pesan error jika data null
                Toast.makeText(context, "Event details not available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.openLinkButton.setOnClickListener {
            viewModel.eventDetail.value?.link?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            } ?: Toast.makeText(context, "Link not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

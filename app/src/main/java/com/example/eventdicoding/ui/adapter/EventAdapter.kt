package com.example.eventdicoding.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventdicoding.R
import com.example.eventdicoding.data.model.EventItem
import com.example.eventdicoding.databinding.ItemEventBinding

class EventAdapter(private val onEventClick: (EventItem) -> Unit) : ListAdapter<EventItem, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    var onFavoriteClick: ((EventItem, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem) {
            with(binding) {
                tvEventName.text = event.nameEvent
                Glide.with(ivEventImage.context)
                    .load(event.imgEvent)
                    .into(ivEventImage)

                btnFav.setImageResource(
                    if (event.isFavourite) R.drawable.ic_favourite_full_events else R.drawable.ic_favourite_border_events
                )

                btnFav.setOnClickListener {
                    val newIsFavorite = !event.isFavourite
                    onFavoriteClick?.invoke(event, newIsFavorite)

                    // Temporarily update UI state until change is reflected in ViewModel list
                    btnFav.setImageResource(
                        if (newIsFavorite) R.drawable.ic_favourite_full_events else R.drawable.ic_favourite_border_events
                    )
                }

                root.setOnClickListener {
                    onEventClick(event)
                }
            }
        }
    }

    class EventDiffCallback : DiffUtil.ItemCallback<EventItem>() {
        override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
            return oldItem == newItem
        }
    }
}

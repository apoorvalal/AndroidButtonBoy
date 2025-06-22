package com.example.buttonboy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.EventViewHolder>() {

    private var events = emptyList<Event>()

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Update the ViewHolder to find the new TextViews
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val eventTimestampTextView: TextView = itemView.findViewById(R.id.eventTimestampTextView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = events[position]
        // Set text for both
        holder.eventNameTextView.text = currentEvent.eventName
        holder.eventTimestampTextView.text = currentEvent.timestamp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }


    override fun getItemCount() = events.size

    fun getEventAt(position: Int): Event {
        return events[position]
    }

    fun setData(events: List<Event>) {
        this.events = events
        notifyDataSetChanged()
    }
}

package com.example.myflightloud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SubscribedFlightAdapter(
    private val flights: MutableList<SubscribedFlight>
) : RecyclerView.Adapter<SubscribedFlightAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val flightKeyTextView: TextView = itemView.findViewById(R.id.item_title)
        val bidTextView: TextView = itemView.findViewById(R.id.user_bid)
        val priceTextView: TextView = itemView.findViewById(R.id.item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subscribed_flight, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val flight = flights[position]
        holder.flightKeyTextView.text = flight.flightKey
        holder.bidTextView.text = flight.bid.toString()
        holder.priceTextView.text = flight.flightPrice.toString()
    }

    override fun getItemCount(): Int = flights.size

    fun updateData(newFlights: List<SubscribedFlight>) {
        flights.clear()
        flights.addAll(newFlights)
        notifyDataSetChanged()
    }
}

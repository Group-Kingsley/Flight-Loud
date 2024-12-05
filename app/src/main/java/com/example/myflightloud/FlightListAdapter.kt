package com.example.myflightloud

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import android.view.inputmethod.EditorInfo

class FlightListAdapter(
    private val flightDeals: List<FlightDeal>,
    private val onBidChanged: (FlightDeal, Double) -> Unit,
    private val onCheckboxChanged: (FlightDeal, Boolean) -> Unit
) : RecyclerView.Adapter<FlightListAdapter.FlightDealViewHolder>() {

    class FlightDealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detailsTextView: TextView = view.findViewById(R.id.item_title)
        val priceTextView: TextView = view.findViewById(R.id.item_price)
        val userBidEditText: EditText = view.findViewById(R.id.user_bid)
        val checkBox: CheckBox = view.findViewById(R.id.item_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightDealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fliigh_list_recyclerview, parent, false)
        return FlightDealViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightDealViewHolder, position: Int) {
        val deal = flightDeals[position]

        // Populate the details
        holder.detailsTextView.text = deal.key
        holder.priceTextView.text = "${deal.price.units} ${deal.price.currencyCode}"

        // Handle checkbox changes
        holder.checkBox.setOnCheckedChangeListener(null) // Reset listener to avoid false triggers
        holder.checkBox.isChecked = false // Default unchecked
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckboxChanged(deal, isChecked)
        }

        // Handle bid input
        holder.userBidEditText.setText("") // Reset the EditText to avoid incorrect data
        holder.userBidEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val bidText = holder.userBidEditText.text.toString()
                val bidValue = bidText.toDoubleOrNull()
                if (bidValue != null) {
                    onBidChanged(deal, bidValue)
                } else {
                    holder.userBidEditText.error = "Invalid bid value"
                }
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount() = flightDeals.size
}

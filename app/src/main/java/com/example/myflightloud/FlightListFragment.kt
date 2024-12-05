package com.example.myflightloud

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FlightListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlightListAdapter
    private val subscribeFlights = mutableListOf<FlightDeal>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_flight_list, container, false)
        recyclerView = rootView.findViewById(R.id.flightlist_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // get the deals from the flghtdata
        val flightData = arguments?.getParcelable<FlightData>("flight_data")
        val flightDeals = flightData?.data?.flightDeals ?: emptyList()
        Log.d("FlightListFragment", "Received flight deals: ${flightDeals.size}")

        // Set up the adapter with the flight deals list
        adapter = FlightListAdapter(
            flightDeals,
            onBidChanged = { flightDeal, bid ->
                // TODO stoee the new bid locally
                val currentBid = bid
                showToast("Bid $${currentBid}! for ${flightDeal.key} flight")
            },
            onCheckboxChanged = { flightDeal, isChecked ->
                if(isChecked) {
                    subscribeFlights.add(flightDeal)
                    showToast("Subscribed to ${flightDeal.key} flight")
                    Log.d("FlightListFragment", subscribeFlights.toString())

                }else {
                    subscribeFlights.remove(flightDeal)
                    showToast("Unsubscribed from ${flightDeal.key} flight")
                    Log.d("FlightListFragment", subscribeFlights.toString())
                }
            })
        recyclerView.adapter = adapter

        return rootView
    }

    // Helper function to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newInstance(flightResponse: FlightData): FlightListFragment {
            val fragment = FlightListFragment()
            val args = Bundle().apply {
                putParcelable("flight_data", flightResponse)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
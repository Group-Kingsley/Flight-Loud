package com.example.myflightloud

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FlightListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FlightListAdapter
    private val subscribeFlights = mutableListOf<FlightDeal>()
    private var currentBid = 0.0

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
                saveFlight(flightDeal, bid)
                showToast("Bid $${bid}! for ${flightDeal.key} flight")
            },
            onCheckboxChanged = { flightDeal, isChecked ->
                if(isChecked) {
                    saveFlight(flightDeal, null)
                    showToast("Subscribed to ${flightDeal.key} flight")
                    Log.d("FlightListFragment", subscribeFlights.toString())

                }else {
                    val db = AppDatabase.getInstance(requireContext())
                    val dao = db.subscribedFlightDAO()
                    lifecycleScope.launch {
                        dao.deleteAll()
                    }
                    showToast("Unsubscribed from ${flightDeal.key} flight")
                    Log.d("FlightListFragment", subscribeFlights.toString())
                }
            })
        recyclerView.adapter = adapter

        return rootView
    }
    private fun saveFlight(flightDeal: FlightDeal, bid: Double?) {
        val flight = SubscribedFlight(flightKey = flightDeal.key, bid = bid, flightPrice = flightDeal.price.units.toDouble())
        val db = AppDatabase.getInstance(requireContext())
        val dao = db.subscribedFlightDAO()
        lifecycleScope.launch(Dispatchers.IO) {
            dao.insertAll(flight)
        }
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
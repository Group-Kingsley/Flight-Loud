package com.example.myflightloud



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubscribedFlightAdapter
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = rootView.findViewById(R.id.fragment_home_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SubscribedFlightAdapter(mutableListOf())
        recyclerView.adapter = adapter

        loadSubscribedFlights()

        return rootView
    }

    private fun loadSubscribedFlights() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val subscribedFlight = database.subscribedFlightDAO().getAll()
                launch(Dispatchers.Main) {
                    adapter.updateData(subscribedFlight)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}

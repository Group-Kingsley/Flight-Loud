package com.example.myflightloud

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
private val TAG = "SearchFragment"

class SearchFragment : Fragment() {
    private  lateinit var FromTextView: EditText

    private lateinit var ToTextView: EditText
    private lateinit var WhenTextView: EditText
    private lateinit var searchBtn: Button

    interface OnSearchCompletedListener {
        fun onSearchCompleted()
    }
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttpClient())
        .build()

    val apiService = retrofit.create(ApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        FromTextView = rootView.findViewById(R.id.from_textView)
        ToTextView = rootView.findViewById(R.id.To_textView)
        WhenTextView = rootView.findViewById(R.id.when_textView)
        searchBtn = rootView.findViewById(R.id.search_btn)

        //when search btton is pressed set the values
        searchBtn.setOnClickListener{
            val fromAirportValue = FromTextView.text.toString()
            val toAirportValue = ToTextView.text.toString()
            val departureDateValue = WhenTextView.text.toString()

            //validates required inputs
            if(fromAirportValue.isEmpty() || toAirportValue.isEmpty() || departureDateValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }else {
                fetchFlightData(fromAirportValue, toAirportValue, departureDateValue)
            }
        }
        return  rootView

    }
    private fun fetchFlightData(fromAirport: String, toAirport: String, departureDate: String) {
        // Log the API request to check if it is being made
        Log.d(TAG, "API call started for: from $fromAirport to $toAirport on $departureDate")

        apiService.getFlightData(
            fromId = fromAirport,
            toId = toAirport,
            departDate = departureDate,
            pageNo = 1,
            adults = 1,
            cabinClass = "ECONOMY"
        ).enqueue(object : Callback<FlightData> {
            override fun onResponse(call: Call<FlightData>, response: Response<FlightData>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "API Response: Success")
                    val flightResponse = response.body()
                    if (flightResponse != null) {
                        (activity as? OnSearchCompletedListener)?.onSearchCompleted()

                        // Process and display the flight data
                        Log.d(TAG, "Flight Data: ${flightResponse.data}")
                        //passin the data to flightDeals fragment
                        val flightListFragment = FlightListFragment.newInstance(flightResponse)
                        replaceFragment(flightListFragment)
                    } else {
                        Log.d(TAG, "No flight data found")
                        showToast("No flight data found")
                    }
                } else {
                    Log.e(TAG, "API Response Error: ${response.message()}")
                    showToast("Failed to load data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<FlightData>, t: Throwable) {
                Log.e(TAG, "API Call Failed: ${t.message}")
                showToast("Error: ${t.message}")
            }
        })
    }

    // Method to show a toast message
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Helper function to replace current fragment with the new one
    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)  // Optional, adds this transaction to the back stack so the user can navigate back
        transaction.commit()
    }

    companion object {
        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}

private fun getOkHttpClient(): OkHttpClient {
    val apiKey = BuildConfig.API_KEY
    val interceptor = Interceptor { chain ->
        // Add API Key and Host to the request header
        val request = chain.request().newBuilder()
            .addHeader("X-RapidAPI-Key", apiKey) // Add your API Key here
            .addHeader("X-RapidAPI-Host", "booking-com15.p.rapidapi.com")
            .build()

        chain.proceed(request) // Proceed with the request
    }

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY


    return OkHttpClient.Builder()
        .addInterceptor(interceptor)
        //.addInterceptor(loggingInterceptor) // Add the interceptor FOR http logging.. reallyy neat stuff
        .build()
}

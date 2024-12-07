package com.example.myflightloud

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val TAG = "SearchFragment"
private const val LOCATION_PERMISSION_REQUEST_CODE = 1

class SearchFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var fetchLocationBtn: Button
    private lateinit var FromTextView: EditText
    private lateinit var ToTextView: AutoCompleteTextView
    private lateinit var WhenTextView: EditText
    private lateinit var searchBtn: Button
    private lateinit var loadingSpinner: ProgressBar

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://booking-com15.p.rapidapi.com/api/v1/flights/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttpClient())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        FromTextView = rootView.findViewById(R.id.from_textView)
        ToTextView = rootView.findViewById(R.id.To_textView)
        WhenTextView = rootView.findViewById(R.id.when_textView)
        searchBtn = rootView.findViewById(R.id.search_btn)
        fetchLocationBtn = rootView.findViewById(R.id.fetch_location_btn)
        loadingSpinner = rootView.findViewById(R.id.loading_spinner)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        fetchLocationBtn.setOnClickListener {
            getCurrentLocation()
        }

        setupDestinationAutocomplete()

        searchBtn.setOnClickListener {
            val fromAirportValue = FromTextView.text.toString()
            val toAirportValue = ToTextView.text.toString()
            val departureDateValue = WhenTextView.text.toString()

            if (fromAirportValue.isEmpty() || toAirportValue.isEmpty() || departureDateValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                fetchFlightData(fromAirportValue, toAirportValue, departureDateValue)
            }
        }

        return rootView
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Fetch the user's last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val city = getCityName(location.latitude, location.longitude)
                FromTextView.setText(city)
                Log.d(TAG, "Your current city: $city")
                Toast.makeText(requireContext(), "Detected city: $city", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun getCityName(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (addresses != null) {
            return addresses[0]?.locality ?: "Unknown City"
            } else {
                return "Unknown City"
            }
    }

    private fun setupDestinationAutocomplete() {
        ToTextView.threshold = 4 // Show suggestions after 4 character

        ToTextView.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    fetchDestinationSuggestions(s.toString())
                }
            }
        })
    }

    private fun fetchDestinationSuggestions(query: String) {
        apiService.getDestinationSuggestions(query).enqueue(object : Callback<List<DestinationSuggestion>> {
            override fun onResponse(
                call: Call<List<DestinationSuggestion>>,
                response: Response<List<DestinationSuggestion>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val suggestions = response.body()!!.map { it.name }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        suggestions
                    )
                    ToTextView.setAdapter(adapter)
                } else {
                    Log.e(TAG, "Failed to fetch destination suggestions: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<DestinationSuggestion>>, t: Throwable) {
                Log.e(TAG, "Error fetching destination suggestions: ${t.message}")
            }
        })
    }


    private fun fetchFlightData(fromAirport: String, toAirport: String, departureDate: String) {
        // Implement flight data fetching logic as described earlier
    }

    private fun getOkHttpClient(): OkHttpClient {
        val apiKey = BuildConfig.API_KEY
        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "booking-com15.p.rapidapi.com")
                .build()
            chain.proceed(request)
        }
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch location
                getCurrentLocation()
            } else {
                // Permission denied
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Unable to access location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}

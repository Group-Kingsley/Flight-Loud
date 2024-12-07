package com.example.myflightloud



import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SubscribedFlightAdapter
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(requireContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get the FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM Token", "Token: $token") // You can send this token to your server
            } else {
                Log.w("FCM Token", "Fetching FCM token failed", task.exception)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "flight_price_drop_channel"
            val channelName = "Flight Price Drop"
            val descriptionText = "Notifies you when a flight price drops."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val btnLogout = view.findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        setupSwipeToDelete()
    }

    private fun loadSubscribedFlights() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val subscribedFlights = database.subscribedFlightDAO().getAll()
                val priceDropFlight = subscribedFlights.find { it.flightPrice < it.bid }
                if (priceDropFlight != null) {
                    Log.d("HomeFragment", "Price drop detected: ${priceDropFlight.flightKey} - Price: ${priceDropFlight.flightPrice} vs Bid: ${priceDropFlight.bid}")
                    launch(Dispatchers.Main) {
                        sendPriceDropNotification(priceDropFlight.flightKey, priceDropFlight.flightPrice)
                        sendPriceDropNotification(flightName="test Flight", newPrice = 1.2)
                    }
                }
                if (priceDropFlight != null) {
                    launch(Dispatchers.Main) {
                        sendPriceDropNotification(priceDropFlight.flightKey, priceDropFlight.flightPrice)
                    }
                }
                launch(Dispatchers.Main) {
                    adapter.updateData(subscribedFlights)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun setupSwipeToDelete() {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Drag and drop not needed
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                // Remove item from the adapter
                val removedFlight = adapter.removeItem(position)

                // Remove item from the database
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    database.subscribedFlightDAO().delete(removedFlight)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun sendPriceDropNotification(flightName: String, newPrice: Double) {
        val channelId = "flight_price_drop_channel"

        // Log the values to ensure they are correct
        Log.d("Notification", "Sending notification: Flight: $flightName, New Price: $newPrice")

        val notificationBuilder = android.app.Notification.Builder(requireContext(), channelId)
            .setSmallIcon(android.R.drawable.ic_notification_overlay) // Use a valid icon
            .setContentTitle("Price Drop Alert!")
            .setContentText("$flightName price dropped to $newPrice!") // Make sure these values are not null or empty
            .setPriority(android.app.Notification.PRIORITY_HIGH)
            .setAutoCancel(true) // Automatically dismiss the notification when tapped

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build()) // Unique notification ID
    }



    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}

package com.example.myflightloud
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

//how to pass data to other fraegment easily in andriod
@Parcelize
data class FlightData(
    val data: FlightDeals
) : Parcelable

@Parcelize
data class FlightDeals(
    val flightDeals: List<FlightDeal> // This is the array of flight deals
) : Parcelable

@Parcelize
data class FlightDeal(
    val key: String,
    val price: Price // `price` is an object with currencyCode and unit
) : Parcelable

@Parcelize
data class Price(
    val currencyCode: String,
    val units: Int // This represents the price in the specified currency
) : Parcelable


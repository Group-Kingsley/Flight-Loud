package com.example.myflightloud

data class FlightData(
    val data: FlightDeals
)

data class FlightDeals(
    val flightDeals: List<FlightDeal> // This is the array of flight deals
)

data class FlightDeal(
    val key: String,
    val price: Price // `price` is an object with currencyCode and unit
)

data class Price(
    val currencyCode: String,
    val unit: Int // This represents the price in the specified currency
)


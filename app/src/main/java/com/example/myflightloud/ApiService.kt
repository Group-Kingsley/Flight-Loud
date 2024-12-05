package com.example.myflightloud


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Define the API service interface
interface ApiService {

    @GET("searchFlights")
    fun getFlightData(
        @Query("fromId") fromId: String,
        @Query("toId") toId: String,
        @Query("departDate") departDate: String,
        @Query("pageNo") pageNo: Int?=1,
        @Query("adults") adults: Int?=1,
        @Query("children") children: String?=null,
        @Query("sort") sort: String?="CHEAPEST",
        @Query("cabinClass") cabinClass: String?,
        @Query("currency_code") currencyCode: String?="USD"
    ): Call<FlightData>
}
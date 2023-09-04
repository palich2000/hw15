package com.palich.web_client

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object WeatherUndergroundApi {
    private val apiService5DaysForecast: WeatherUndergroundApiService5DaysForecast
    private val apiServiceSearchLocation: WeatherUndergroundApiServiceSearchLocation
    private val apiServiceCurrent: WeatherUndergroundApiServiceCurrent

    private const val weatherUndergroundApiKey = "ebb513f131474ba6b513f13147aba691"
    private const val weatherUndergroundApiUrl = "https://api.weather.com/"
    private const val TAG = "WeatherUndergroundApi"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(weatherUndergroundApiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        apiService5DaysForecast =
            retrofit.create(WeatherUndergroundApiService5DaysForecast::class.java)
        apiServiceSearchLocation =
            retrofit.create(WeatherUndergroundApiServiceSearchLocation::class.java)
        apiServiceCurrent = retrofit.create(WeatherUndergroundApiServiceCurrent::class.java)
    }

    fun getDataSearchLocation(
        geoCode: String,
        callBack: (WeatherUndergroundSearchLocation?) -> Unit
    ) {

        val call = apiServiceSearchLocation.getData(geoCode, weatherUndergroundApiKey, "pws", "json")
        call.enqueue(object : Callback<WeatherUndergroundSearchLocation> {

            override fun onResponse(
                call: Call<WeatherUndergroundSearchLocation>,
                response: Response<WeatherUndergroundSearchLocation>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d(TAG, "Data: $data")
                    callBack(data)
                } else {
                    Log.e(TAG, "getDataSearchLocation Error: ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherUndergroundSearchLocation>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}")
            }
        })
    }

    fun getDataCurrent(
        stationId: String,
        callBack: (WeatherUndergroundCurrent?) -> Unit
    ) {

        val call = apiServiceCurrent.getData(stationId, weatherUndergroundApiKey, "m", "json")
        call.enqueue(object : Callback<WeatherUndergroundCurrent> {

            override fun onResponse(
                call: Call<WeatherUndergroundCurrent>,
                response: Response<WeatherUndergroundCurrent>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d(TAG, "Data: $data")
                    callBack(data)
                } else {
                    Log.e(TAG, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherUndergroundCurrent>, t: Throwable) {
                Log.e(TAG, "Error: ${t.message}")
            }
        })
    }

    fun getData5DaysForecast(
        geoCode: String,
        callBack: (WeatherUnderground5DaysForecast?) -> Unit
    ) {
        Log.i(TAG, "getData5DaysForecast: $geoCode")
        val call =
            apiService5DaysForecast.getData(geoCode, weatherUndergroundApiKey, "m", "en-US", "json")
        call.enqueue(object : Callback<WeatherUnderground5DaysForecast> {

            override fun onResponse(
                call: Call<WeatherUnderground5DaysForecast>,
                response: Response<WeatherUnderground5DaysForecast>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d(TAG, "Data: $data")
                    callBack(data)
                } else {
                    Log.e(TAG, "getData5DaysForecast Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<WeatherUnderground5DaysForecast>, t: Throwable) {
                Log.e(TAG, "getData5DaysForecast Error: ${t.message}")
            }
        })
    }
}

data class WeatherUnderground5DaysForecast(
    @SerializedName("calendarDayTemperatureMax") var calendarDayTemperatureMax: ArrayList<Int> = arrayListOf(),
    @SerializedName("calendarDayTemperatureMin") var calendarDayTemperatureMin: ArrayList<Int> = arrayListOf(),
    @SerializedName("dayOfWeek") var dayOfWeek: ArrayList<String> = arrayListOf(),
    @SerializedName("expirationTimeUtc") var expirationTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("moonPhase") var moonPhase: ArrayList<String> = arrayListOf(),
    @SerializedName("moonPhaseCode") var moonPhaseCode: ArrayList<String> = arrayListOf(),
    @SerializedName("moonPhaseDay") var moonPhaseDay: ArrayList<Int> = arrayListOf(),
    @SerializedName("moonriseTimeLocal") var moonriseTimeLocal: ArrayList<String> = arrayListOf(),
    @SerializedName("moonriseTimeUtc") var moonriseTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("moonsetTimeLocal") var moonsetTimeLocal: ArrayList<String> = arrayListOf(),
    @SerializedName("moonsetTimeUtc") var moonsetTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("narrative") var narrative: ArrayList<String> = arrayListOf(),
    @SerializedName("qpf") var qpf: ArrayList<Double> = arrayListOf(),
    @SerializedName("qpfSnow") var qpfSnow: ArrayList<Int> = arrayListOf(),
    @SerializedName("sunriseTimeLocal") var sunriseTimeLocal: ArrayList<String> = arrayListOf(),
    @SerializedName("sunriseTimeUtc") var sunriseTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("sunsetTimeLocal") var sunsetTimeLocal: ArrayList<String> = arrayListOf(),
    @SerializedName("sunsetTimeUtc") var sunsetTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("temperatureMax") var temperatureMax: ArrayList<Int> = arrayListOf(),
    @SerializedName("temperatureMin") var temperatureMin: ArrayList<Int> = arrayListOf(),
    @SerializedName("validTimeLocal") var validTimeLocal: ArrayList<String> = arrayListOf(),
    @SerializedName("validTimeUtc") var validTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("daypart") var daypart: ArrayList<WeatherUndergroundDayPart> = arrayListOf()
)

data class WeatherUndergroundDayPart(
    @SerializedName("cloudCover") var cloudCover: ArrayList<Int?> = arrayListOf(),
    @SerializedName("dayOrNight") var dayOrNight: ArrayList<String?> = arrayListOf(),
    @SerializedName("daypartName") var daypartName: ArrayList<String?> = arrayListOf(),
    @SerializedName("iconCode") var iconCode: ArrayList<Int?> = arrayListOf(),
    @SerializedName("iconCodeExtend") var iconCodeExtend: ArrayList<Int> = arrayListOf(),
    @SerializedName("narrative") var narrative: ArrayList<String> = arrayListOf(),
    @SerializedName("precipChance") var precipChance: ArrayList<Int> = arrayListOf(),
    @SerializedName("precipType") var precipType: ArrayList<String> = arrayListOf(),
    @SerializedName("qpf") var qpf: ArrayList<Double> = arrayListOf(),
    @SerializedName("qpfSnow") var qpfSnow: ArrayList<Int> = arrayListOf(),
    @SerializedName("qualifierCode") var qualifierCode: ArrayList<String> = arrayListOf(),
    @SerializedName("qualifierPhrase") var qualifierPhrase: ArrayList<String> = arrayListOf(),
    @SerializedName("relativeHumidity") var relativeHumidity: ArrayList<Int> = arrayListOf(),
    @SerializedName("snowRange") var snowRange: ArrayList<String> = arrayListOf(),
    @SerializedName("temperature") var temperature: ArrayList<Int> = arrayListOf(),
    @SerializedName("temperatureHeatIndex") var temperatureHeatIndex: ArrayList<Int> = arrayListOf(),
    @SerializedName("temperatureWindChill") var temperatureWindChill: ArrayList<Int> = arrayListOf(),
    @SerializedName("thunderCategory") var thunderCategory: ArrayList<String> = arrayListOf(),
    @SerializedName("thunderIndex") var thunderIndex: ArrayList<Int> = arrayListOf(),
    @SerializedName("uvDescription") var uvDescription: ArrayList<String> = arrayListOf(),
    @SerializedName("uvIndex") var uvIndex: ArrayList<Int> = arrayListOf(),
    @SerializedName("windDirection") var windDirection: ArrayList<Int> = arrayListOf(),
    @SerializedName("windDirectionCardinal") var windDirectionCardinal: ArrayList<String> = arrayListOf(),
    @SerializedName("windPhrase") var windPhrase: ArrayList<String> = arrayListOf(),
    @SerializedName("windSpeed") var windSpeed: ArrayList<Int> = arrayListOf(),
    @SerializedName("wxPhraseLong") var wxPhraseLong: ArrayList<String> = arrayListOf(),
    @SerializedName("wxPhraseShort") var wxPhraseShort: ArrayList<String> = arrayListOf()

)

interface WeatherUndergroundApiService5DaysForecast {
    @GET("v3/wx/forecast/daily/5day")
    fun getData(
        @Query("geocode") paramGeoCode: String,
        @Query("apiKey") paramApiKey: String,
        @Query("units") paramUnits: String,
        @Query("language") paramLanguage: String,
        @Query("format") paramFormat: String
    ): Call<WeatherUnderground5DaysForecast>
}


data class WeatherUndergroundSearchLocation(

    @SerializedName("location") var location: WeatherUndergroundLocation? = WeatherUndergroundLocation()

)

data class WeatherUndergroundLocation(

    @SerializedName("stationName") var stationName: ArrayList<String> = arrayListOf(),
    @SerializedName("stationId") var stationId: ArrayList<String> = arrayListOf(),
    @SerializedName("qcStatus") var qcStatus: ArrayList<Int> = arrayListOf(),
    @SerializedName("updateTimeUtc") var updateTimeUtc: ArrayList<Int> = arrayListOf(),
    @SerializedName("partnerId") var partnerId: ArrayList<String> = arrayListOf(),
    @SerializedName("latitude") var latitude: ArrayList<Double> = arrayListOf(),
    @SerializedName("longitude") var longitude: ArrayList<Double> = arrayListOf(),
    @SerializedName("distanceKm") var distanceKm: ArrayList<Double> = arrayListOf(),
    @SerializedName("distanceMi") var distanceMi: ArrayList<Double> = arrayListOf()

)

interface WeatherUndergroundApiServiceSearchLocation {
    @GET("v3/location/near")
    fun getData(
        @Query("geocode") paramGeoCode: String,
        @Query("apiKey") paramApiKey: String,
        @Query("product") paramProduct: String,
        @Query("format") paramFormat: String
    ): Call<WeatherUndergroundSearchLocation>
}

data class WeatherUndergroundCurrent(

    @SerializedName("observations") var observations: ArrayList<WeatherUndergroundObservations> = arrayListOf()

)

data class WeatherUndergroundCurrentMetric(

    @SerializedName("temp") var temp: Int? = null,
    @SerializedName("heatIndex") var heatIndex: Int? = null,
    @SerializedName("dewpt") var dewpt: Int? = null,
    @SerializedName("windChill") var windChill: String? = null,
    @SerializedName("windSpeed") var windSpeed: String? = null,
    @SerializedName("windGust") var windGust: String? = null,
    @SerializedName("pressure") var pressure: Double? = null,
    @SerializedName("precipRate") var precipRate: String? = null,
    @SerializedName("precipTotal") var precipTotal: String? = null,
    @SerializedName("elev") var elev: Int? = null

)

data class WeatherUndergroundObservations(

    @SerializedName("stationID") var stationID: String? = null,
    @SerializedName("obsTimeUtc") var obsTimeUtc: String? = null,
    @SerializedName("obsTimeLocal") var obsTimeLocal: String? = null,
    @SerializedName("neighborhood") var neighborhood: String? = null,
    @SerializedName("softwareType") var softwareType: String? = null,
    @SerializedName("country") var country: String? = null,
    @SerializedName("solarRadiation") var solarRadiation: String? = null,
    @SerializedName("lon") var lon: Double? = null,
    @SerializedName("realtimeFrequency") var realtimeFrequency: String? = null,
    @SerializedName("epoch") var epoch: Int? = null,
    @SerializedName("lat") var lat: Double? = null,
    @SerializedName("uv") var uv: String? = null,
    @SerializedName("winddir") var winddir: String? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("qcStatus") var qcStatus: Int? = null,
    @SerializedName("metric") var metric: WeatherUndergroundCurrentMetric? = WeatherUndergroundCurrentMetric()

)

interface WeatherUndergroundApiServiceCurrent {
    @GET("/v2/pws/observations/current")
    fun getData(
        @Query("stationId") paramStationId: String,
        @Query("apiKey") paramApiKey: String,
        @Query("units") paramUnits: String,
        @Query("format") paramFormat: String
    ): Call<WeatherUndergroundCurrent>
}

package com.palich.web_client

import android.content.pm.PackageManager
import android.graphics.drawable.PictureDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream
import com.caverock.androidsvg.SVG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_PERMISSION_CODE = 42
        private const val TAG = "MainActivity"
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        requestPermissions()
        setupLocation()

//        val button = findViewById<Button>(R.id.button)
//        button.setOnClickListener {
//            Thread {
//                WeatherUndergroundApi.getData5DaysForecast("55.75396,37.620393", ::displayForecast)
//            }.start()
//        }

    }

    private fun setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                location = locationResult.lastLocation
                if (location != null) {
                    Log.i(TAG, "Location: ${location?.latitude} ${location?.longitude}")
                    WeatherUndergroundApi.getDataSearchLocation(
                        "${location?.latitude},${location?.longitude}",
                        ::searchNearestPWS
                    )
                }
            }
        }

        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {

        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 * 60 * 30 // 30 minutes
        }

        val permission = android.Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun searchNearestPWS(weatherUndergroundSearchLocation: WeatherUndergroundSearchLocation?): Unit {
        if (weatherUndergroundSearchLocation == null) {
            Log.e(TAG, "Error: location is null")
            return
        }
        val weatherUndergroundLocation = weatherUndergroundSearchLocation.location
        if (weatherUndergroundLocation == null) {
            Log.e(TAG, "Error: location is null")
            return
        }

        val now: Long = System.currentTimeMillis() / 1000
        var minDistance: Double? = null
        var minIndex: Int? = null;
        for (i in weatherUndergroundLocation.stationName.indices) {
            val stationName = weatherUndergroundLocation.stationName[i]
            val stationId = weatherUndergroundLocation.stationId[i]
            val status = weatherUndergroundLocation.qcStatus[i]
            val lastOnline = weatherUndergroundLocation.updateTimeUtc[i]
            val distance = weatherUndergroundLocation.distanceKm[i]
            if (status > 0 && (minDistance == null || minDistance > distance)) {
                minDistance = distance
                minIndex = i
            }
            Log.i(TAG, "PWS: $stationName $stationId $status $distance ${now - lastOnline}")
        }

        Log.i(
            TAG,
            "Best PWS: ${weatherUndergroundLocation.stationName[minIndex!!]} ${weatherUndergroundLocation.stationId[minIndex]}"
        )

        WeatherUndergroundApi.getDataCurrent(
            weatherUndergroundLocation.stationId[minIndex],
            ::displayCurrent
        )

    }

    private fun displayCurrent(current: WeatherUndergroundCurrent?): Unit {
        if (current == null) {
            Log.e(TAG, "Error: current is null")
            return
        }
        if (location == null) {
            Log.e(TAG, "Error: location is null")
            return
        }
        WeatherUndergroundApi.getData5DaysForecast(
            "${location?.latitude},${location?.longitude}",
            ::displayForecast
        )
    }

    private fun loadIcon(imageView: ImageView, iconCode: Int?) {
        val svgUrl =
            "https://www.wunderground.com/static/i/c/v4/${iconCode?:44}.svg"

        val uri = Uri.parse(svgUrl)

        val okHttpClient = OkHttpClient()

        val request = Request.Builder().url(uri.toString()).build()

        GlobalScope.launch(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            val inputStream: InputStream? = response.body?.byteStream()

            if (inputStream != null) {
                val svg: SVG = SVG.getFromInputStream(inputStream)
                val drawable: PictureDrawable = PictureDrawable(svg.renderToPicture())
                runOnUiThread {
                    imageView.setImageDrawable(drawable)
                }
            }
        }

    }

    private fun displayForecast(forecast: WeatherUnderground5DaysForecast?): Unit {
        if (forecast == null) {
            Log.e(TAG, "Error: forecast is null")
            return
        }
        Log.i(TAG, "Forecast: ${forecast.daypart[0].daypartName}")
        runOnUiThread {
            Log.i(TAG, " Icon codes: ${forecast.daypart[0].iconCode}")

            var i = 0
            if (forecast.daypart[0].iconCode[i] == null) {
                i++
            }

            loadIcon(
                findViewById<ImageView>(R.id.imageView1),
                forecast.daypart[0].iconCode[i]
            )
            loadIcon(
                findViewById<ImageView>(R.id.imageView2),
                forecast.daypart[0].iconCode[i+1]
            )
            loadIcon(
                findViewById<ImageView>(R.id.imageView3),
                forecast.daypart[0].iconCode[i+2]
            )

            try {
                findViewById<TextView>(R.id.textView1).text =
                    forecast.daypart[0].daypartName[i] + " " +
                            forecast.daypart[0].temperature[i].toString() + "°C" + " " +
                            forecast.daypart[0].precipChance[i].toString() + "% \n" +
                            forecast.daypart[0].narrative[i]
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
            try {
                findViewById<TextView>(R.id.textView2).text =
                    forecast.daypart[0].daypartName[i+1] + " " +
                            forecast.daypart[0].temperature[i+1].toString() + "°C" + " " +
                            forecast.daypart[0].precipChance[i+1].toString() + "% \n" +
                            forecast.daypart[0].narrative[i+1]
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
            try {

                findViewById<TextView>(R.id.textView3).text =
                    forecast.daypart[0].daypartName[i+2] + " " +
                            forecast.daypart[0].temperature[i+2].toString() + "°C" + " " +
                            forecast.daypart[0].precipChance[i+2].toString() + "% \n" +
                            forecast.daypart[0].narrative[i+2]
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
            }
        }
    }

//    private fun downloadIMageGlide() {
//        Glide.with(this)
//            .load("https://pub-static.fotor.com/assets/bg/78f0d7f1-e8e5-4f87-9af7-17495858c8ec.jpg")
//            .into(findViewById(R.id.imageView))
//    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissions(permissions, REQUEST_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {

            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(
                        TAG,
                        "Permission: ${permissions[i]} - granted grantResult: ${grantResults[i]}"
                    )
                } else {
                    Log.e(
                        TAG,
                        "Permission: ${permissions[i]} - declined grantResult: ${grantResults[i]}"
                    )
                }
            }
        }
    }
}


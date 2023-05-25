package com.example.meteo

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meteo.databinding.ActivityProgressBarBinding
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ProgressBarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBarBinding
    private val weatherDataList = mutableListOf<WeatherData>()
    private lateinit var adapter: WeatherDataAdapter
    private lateinit var timer: CountDownTimer
    private val handler = Handler()
    private var lastMessageUpdateTime: Long = 0L
    private var lastMessageIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = WeatherDataAdapter(weatherDataList)
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.recyclerView.adapter = adapter

        // Initialisation de la jauge de progression
        binding.progressBar.progress = 0
        binding.progressBar.max = 100


        // Appel de l'API météo toutes les 10 secondes
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = (Long.MAX_VALUE - millisUntilFinished)/ 1000
                val progress = (timeLeft + 1) * 100 / 60
                binding.progressBar.progress = progress.toInt()

                if (timeLeft - lastMessageUpdateTime >= 6L) {
                    lastMessageUpdateTime = timeLeft
                    lastMessageIndex = (lastMessageIndex + 1) % 3
                    when (lastMessageIndex) {
                        0 -> {
                            binding.messageText.text = getString(R.string.data_download)
                        }
                        1 -> {
                            binding.messageText.text = getString(R.string.almost_finish)
                        }
                        2 -> {
                            binding.messageText.text = getString(R.string.few_seconds)
                        }
                    }
                }
                if (timeLeft % 10 == 0L) {
                    val cityIndex = (timeLeft / 10).toInt() % cities.size
                    handler.postDelayed({
                        callWeatherApi(cities[cityIndex])
                    }, 0)
                    val resultList = ArrayList<String>()
                    resultList.add(cities.size.toString())
                }


            }
            override fun onFinish() {
                binding.progressBar.progress = 100
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                handler.removeCallbacksAndMessages(null)
            }
        }
        timer.start()

    }
    private fun callWeatherApi(city: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=$city&appid=ac6c4c4ac9012aa130205b97d7f4d947&units=metric")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Gestion de l'erreur
                Log.e("MY-TAG", "API call failed for city: $city", e)
            }

            override fun onResponse(call: Call, response: Response) {
                // Traitement de la réponse
                val responseData = response.body?.string()
                Log.d("MY-TAG", "API response for city: $city\n$responseData")
                val weatherData = parseWeatherData(responseData)
                weatherData?.let {
                    weatherDataList.add(it)
                    runOnUiThread {
                        adapter.notifyDataSetChanged()
                        binding.recyclerView.visibility = View.VISIBLE
                    }
                }
            }
        })
    }


    companion object {
        val cities = arrayOf("Rennes", "Paris", "Nantes", "Bordeaux", "Lyon")
    }

    private fun parseWeatherData(responseData: String?): WeatherData? {
        responseData?.let {
            try {
                val jsonObject = JSONObject(it)
                val cityName = jsonObject.getString("name")
                val mainObject = jsonObject.getJSONObject("main")
                val temperature = mainObject.getDouble("temp")
                val cloudsObject = jsonObject.getJSONObject("clouds")
                val cloudiness = cloudsObject.getDouble("all")

                return WeatherData(cityName, temperature, cloudiness)
            } catch (e: JSONException) {
                Log.e("MY-TAG", "Error parsing JSON response", e)
            }
        }
        return null
    }



    data class WeatherData(
        val cityName: String,
        val temperature: Double,
        val cloudiness: Double
    )

}
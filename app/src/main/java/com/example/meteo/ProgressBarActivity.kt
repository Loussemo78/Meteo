package com.example.meteo

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.meteo.databinding.ActivityProgressBarBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ProgressBarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBarBinding
    private lateinit var timer: CountDownTimer
    private val handler = Handler()
    private var lastMessageUpdateTime: Long = 0L
    private var lastMessageIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                }


            }
            override fun onFinish() {
                binding.progressBar.progress = 100
                binding.progressBar.visibility = View.GONE
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
            }

            override fun onResponse(call: Call, response: Response) {
                // Traitement de la réponse
            }
        })
    }


    companion object {
        val cities = arrayOf("Rennes", "Paris", "Nantes", "Bordeaux", "Lyon")
    }


    data class WeatherData(
        val cityName: String,
        val temperature: Double,
        val cloudiness: Double
    )

}
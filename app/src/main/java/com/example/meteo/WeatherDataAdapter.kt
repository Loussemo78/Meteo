package com.example.meteo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeatherDataAdapter(private val weatherDataList: List<ProgressBarActivity.WeatherData>) :
    RecyclerView.Adapter<WeatherDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherData = weatherDataList[position]
        holder.bind(weatherData)
    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityNameTextView: TextView = itemView.findViewById(R.id.cityNameTextView)
        private val temperatureTextView: TextView = itemView.findViewById(R.id.temperatureTextView)
        private val cloudinessTextView: TextView = itemView.findViewById(R.id.cloudinessTextView)

        fun bind(weatherData: ProgressBarActivity.WeatherData) {
            cityNameTextView.text = weatherData.cityName
            temperatureTextView.text = weatherData.temperature.toString()
            cloudinessTextView.text = weatherData.cloudiness.toString()
        }
    }
}

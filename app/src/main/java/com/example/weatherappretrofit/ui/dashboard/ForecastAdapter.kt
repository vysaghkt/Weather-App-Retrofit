package com.example.weatherappretrofit.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherappretrofit.R
import com.squareup.picasso.Picasso

class ForecastAdapter(private val items: List<DailyModel>, private val context: Context) :
    RecyclerView.Adapter<ForecastAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.forecastDay)
        val dayTemp: TextView = view.findViewById(R.id.forecastTempDay)
        val nightTemp: TextView = view.findViewById(R.id.forecastTempNight)
        val climateIcon: ImageView = view.findViewById(R.id.forecastIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_recyclerview_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        holder.dayTextView.text = item.dayOfWeek
        holder.dayTemp.text = item.dayTemp
        holder.nightTemp.text = item.nightTemp
        Picasso.get().load("http://openweathermap.org/img/wn/${item.iconCode}@4x.png")
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.climateIcon)
    }

    override fun getItemCount(): Int {
        return items.size
    }

}
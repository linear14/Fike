package com.dongldh.fike.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.fike.R
import com.dongldh.fike.data.Station
import kotlinx.android.synthetic.main.item_result.view.*

class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val star = itemView.star_image
    val name = itemView.station_name
    val detail = itemView.station_detail
}

class ResultAdapter(val context: Context, val list: MutableList<Pair<Station, Double>>) : RecyclerView.Adapter<ResultViewHolder>() {

    val string = context.getString(R.string.station_result_text)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        return ResultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val station = list[position]

        holder.name.text = station.first.stationName

        val newString = string.replace("x", station.second.toInt().toString()).replace("y", station.first.parkingBikeTotCnt.toString())
        Log.d("resultString1", newString)

        val spannable = SpannableString(newString)
        val slash = string.indexOf("/")
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#444444")), slash, slash, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        Log.d("resultString2", spannable.toString())
        holder.detail.text = spannable
    }
}
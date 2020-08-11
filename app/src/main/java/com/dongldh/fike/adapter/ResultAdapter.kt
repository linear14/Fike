package com.dongldh.fike.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.fike.data.Station
import com.dongldh.fike.databinding.ItemResultBinding

class ResultAdapter: ListAdapter<Station, RecyclerView.ViewHolder>(StationDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ResultViewHolder(ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val station = getItem(position)
        (holder as ResultViewHolder).bind(station)
    }

    class ResultViewHolder(private val binding: ItemResultBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Station) {
            binding.apply {
                station = item
                executePendingBindings()
            }
        }
    }

}

private class StationDiffCallback: DiffUtil.ItemCallback<Station>() {
    override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.stationId == newItem.stationId
    }

    override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
    }

}

/*
class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val star = itemView.star_image
    val name = itemView.station_name
    val detail = itemView.station_detail
}

class ResultAdapter(
    val context: Context,
    val list: MutableList<Pair<Station, Double>>,
    val map: MapView,
    val bottomSheetBehavior: BottomSheetBehavior<View>,
    val recycler: RecyclerView
) : RecyclerView.Adapter<ResultViewHolder>() {

    val string = context.getString(R.string.station_result_text)
    var selectedPosition = -1

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
        val slash = newString.indexOf("/")
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#dddddd")), slash, slash+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        Log.d("resultString2", spannable.toString())
        holder.detail.text = spannable

        if(position == selectedPosition) {
            holder.itemView.setBackgroundColor((Color.parseColor("#F5F6F8")))
        } else {
            holder.itemView.setBackgroundColor((Color.parseColor("#FFFFFF")))
        }

        // 검색 결과 view를 클릭하면 화면에 marker가 선택되도록 구현 + 부가기능
        holder.itemView.setOnClickListener {
            val items = map.poiItems
            for(item in items) {
                if(item.itemName == station.first.stationName) {
                    map.selectPOIItem(item!!, true)
                    map.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(item.mapPoint, map.zoomLevelFloat)))
                    if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

                    // recyclerview 에서 선택한 아이템을 recyclerview 의 최상단에 두기
                    val smoothScroller: RecyclerView.SmoothScroller by lazy {
                        object : LinearSmoothScroller(context) {
                            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
                        }
                    }
                    smoothScroller.targetPosition = position
                    recycler.layoutManager?.startSmoothScroll(smoothScroller)
                    break
                }
            }
            updateNotifyItemChanged(position)
        }
    }

    fun updateNotifyItemChanged(position: Int) {
        notifyItemChanged(position)
        notifyItemChanged(selectedPosition)
        selectedPosition = position
    }
}*/

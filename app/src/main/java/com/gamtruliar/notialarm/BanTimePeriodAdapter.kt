package com.gamtruliar.notialarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gamtruliar.notialarm.Data.FilterData
import android.content.pm.PackageManager

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.*
import com.gamtruliar.notialarm.Data.BanTimePeriod
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BanTimePeriodAdapter(private val dataSet: ArrayList<BanTimePeriod>) :    RecyclerView.Adapter<BanTimePeriodAdapter.ViewHolder>() {
    var onRowClick:Event<BanTimePeriod> = Event<BanTimePeriod>()
    var onRowDeleteClick:Event<BanTimePeriod> = Event<BanTimePeriod>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fromToTime: TextView
        val weekDays: TextView
        var context:Context
        var wholeRow:LinearLayout
        var deleteBtn:FloatingActionButton

        init {
            // Define click listener for the ViewHolder's View.
            fromToTime = view.findViewById(R.id.fromToTime)
            weekDays = view.findViewById(R.id.weekDays)
            wholeRow=view.findViewById(R.id.wholeRow)
            deleteBtn=view.findViewById(R.id.deleteBtn)
            context=view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_one_time_period, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var data=dataSet[position]
        viewHolder.fromToTime.text = AppCommon.minInt2Str(data.fromTime)+" -> "+AppCommon.minInt2Str(data.toTime)
        var weekText=""
        for(v in data.weekDay){
            weekText+=""+viewHolder.context.getString(AppCommon.WD2SStr[v]!!)+"|"
        }
        viewHolder.weekDays.text=weekText
        viewHolder.wholeRow.setOnClickListener{view->onRowClick.invoke(data)}
        viewHolder.deleteBtn.setOnClickListener{view->onRowDeleteClick.invoke(data)}
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
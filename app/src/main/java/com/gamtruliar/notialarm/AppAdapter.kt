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
import com.gamtruliar.notialarm.Data.AppData
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AppAdapter(private val dataSet: ArrayList<AppData>, val canDelete:Boolean) :    RecyclerView.Adapter<AppAdapter.ViewHolder>() {
    var onRowClick:Event<AppData> = Event<AppData>()
    var onRowDeleteClick:Event<AppData> = Event<AppData>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        var context:Context
        var imageView: ImageView
        var wholeRow:LinearLayout
        var deleteBtn:FloatingActionButton

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.Name)
            imageView= view.findViewById(R.id.iconImg)
            wholeRow=view.findViewById(R.id.wholeRow)
            deleteBtn=view.findViewById(R.id.deleteBtn)
            context=view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_app, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var data=dataSet[position]
        viewHolder.textView.text = data.packageName
        if(!canDelete){
            viewHolder.deleteBtn.visibility=View.GONE
        }
        if(data.app_name!=""){
            viewHolder.textView.text = data.app_name

        }
        if(data.icon!=null) {
            viewHolder.imageView.setImageDrawable(data.icon)
        }
//        AppCommon.setAppInfo(viewHolder.context,data,viewHolder.imageView,viewHolder.textView)
        viewHolder.wholeRow.setOnClickListener{view->onRowClick.invoke(data)}
        viewHolder.deleteBtn.setOnClickListener{view->onRowDeleteClick.invoke(data)}
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
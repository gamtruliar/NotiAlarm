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
import com.gamtruliar.notialarm.Data.SubFilterData
import com.google.android.material.floatingactionbutton.FloatingActionButton


class SubFilterDataAdapter(private val dataSet: ArrayList<SubFilterData>) :    RecyclerView.Adapter<SubFilterDataAdapter.ViewHolder>() {
    var onRowClick:Event<SubFilterData> = Event<SubFilterData>()
    var onRowDeleteClick:Event<SubFilterData> = Event<SubFilterData>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleKeywordText: TextView
        val textKeywordText: TextView
        var context:Context
        var deleteBtn:FloatingActionButton
        var wholeRow:LinearLayout

        init {
            // Define click listener for the ViewHolder's View.
            wholeRow= view.findViewById(R.id.wholeRow)
            titleKeywordText = view.findViewById(R.id.titleKeyword)
            textKeywordText= view.findViewById(R.id.textKeyword)
            deleteBtn=view.findViewById(R.id.deleteBtn)
            context=view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_sub_filter, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var data=dataSet[position]
        var txt=viewHolder.context.getString(R.string.Keyword_LText_Title).format(data.titleKeyWords,data.titleKeyWordsNot)
        if(txt.endsWith(", !:")){
            txt=txt.substring(0,txt.length-4)
        }
        viewHolder.titleKeywordText.text =txt
        txt=viewHolder.context.getString(R.string.Keyword_LText_Text).format(data.textKeyWords,data.textKeyWordsNot)
        if(txt.endsWith(", !:")){
            txt=txt.substring(0,txt.length-4)
        }
        viewHolder.textKeywordText.text = txt
        viewHolder.deleteBtn.setOnClickListener{view->onRowDeleteClick.invoke(data)}
        viewHolder.wholeRow.setOnClickListener{view->onRowClick.invoke(data)}


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
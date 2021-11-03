package com.gamtruliar.notialarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gamtruliar.notialarm.Data.FilterData
import android.content.pm.PackageManager

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch


class FilterAdapter(private val dataSet: ArrayList<FilterData>) :    RecyclerView.Adapter<FilterAdapter.ViewHolder>() {
    var onRowClick:Event<FilterData> = Event<FilterData>()
    var onRowSwitchChange:Event<FilterData> = Event<FilterData>()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val AppsCond: TextView
        val Conditions: TextView
        var context:Context
        var imageView: ImageView
        var switch:Switch
        var wholeRow:LinearLayout

        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.Name)
            AppsCond = view.findViewById(R.id.AppsCond)
            Conditions = view.findViewById(R.id.Conditions)
            imageView= view.findViewById(R.id.iconImg)
            switch=view.findViewById(R.id.switch1)
            wholeRow=view.findViewById(R.id.wholeRow)
            context=view.context
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_filter, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        var data=dataSet[position]
        viewHolder.textView.text = data.filterName
        AppCommon.setAppInfo(viewHolder.context,data.packageNames[0],viewHolder.imageView,viewHolder.AppsCond)
        var pnames=""
        for(pname in data.packageNames){
            if(pnames!="")pnames+="|"
            pnames+=AppCommon.getAppName(viewHolder.context,pname)
        }
        viewHolder.AppsCond.text=pnames
        var cnds=""
        for(sfData in data.subFilterData){
            if(cnds!="")cnds+=","
            cnds+="["+AppCommon.charLimitStr("\""+sfData.value.titleKeyWords+"\"",10)
            if(sfData.value.titleKeyWordsNot!="") cnds+="["+AppCommon.charLimitStr(",!\""+sfData.value.titleKeyWordsNot+"\"",10)
            cnds+="|"+AppCommon.charLimitStr("\""+sfData.value.textKeyWords+"\"",10)+"]"
            if(sfData.value.textKeyWordsNot!="") cnds+="["+AppCommon.charLimitStr(",!\""+sfData.value.textKeyWordsNot+"\"",10)
        }
        cnds=AppCommon.charLimitStr(cnds,50)
        viewHolder.Conditions.text=cnds
        viewHolder.switch.isChecked=data.enable
        viewHolder.switch.setOnCheckedChangeListener{v,c->
            data.enable=c
            onRowSwitchChange.invoke(data)
        }
        viewHolder.wholeRow.setOnClickListener{view->onRowClick.invoke(data)}
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}
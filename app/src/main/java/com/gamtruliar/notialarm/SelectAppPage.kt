package com.gamtruliar.notialarm

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamtruliar.notialarm.Base.NoMenuFragment
import com.gamtruliar.notialarm.Data.AppData
import com.gamtruliar.notialarm.Data.FilterData
import com.gamtruliar.notialarm.databinding.FragmentSecondBinding
import com.gamtruliar.notialarm.databinding.FragmentSelectAppBinding

class SelectAppPage  : NoMenuFragment() {

    private var _binding: FragmentSelectAppBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var curFilter=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSelectAppBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun updateList(){

        var banSet=HashSet<String>( AppCommon.curFilterData?.packageNames!!)
        var pm=view?.context?.packageManager!!
        var allApp=pm.getInstalledApplications(PackageManager.GET_META_DATA)
        var pkName2Label=HashMap<String,String>()
        for(v in allApp){
            pkName2Label[v.packageName]=""+pm.getApplicationLabel(v)
        }
        allApp.sortBy { x->""+pkName2Label[x.packageName]}
        var data=ArrayList<AppData>()
        for(appInfo in allApp){
            if(!banSet.contains(appInfo.packageName)) {
                if(curFilter=="" || appInfo.packageName.lowercase().contains(curFilter) || pkName2Label[appInfo.packageName]?.lowercase()?.contains(curFilter)!!) {
                    var nAppData=AppData()
                    nAppData.packageName=appInfo.packageName
                    nAppData.app_name=pkName2Label[appInfo.packageName]!!
                    val icon: Drawable = pm.getApplicationIcon(appInfo.packageName)
                    nAppData.icon=icon
                    data.add(nAppData)
                }
            }

        }

        val adapter = AppAdapter(data,false)
        binding.infscr.adapter = adapter
        adapter.onRowClick.plusAssign{x->onAppSelected(x)}
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pName=view.context.getPackageName()+"_preferences";
        var  prefs =view.context.getSharedPreferences( pName, Context.MODE_PRIVATE);
        val layoutManager = LinearLayoutManager(context)
        binding.infscr.layoutManager = layoutManager
        binding.AppNameSearch.editText?.doAfterTextChanged { txt->
            curFilter=txt.toString()
            updateList()
        }

    }

    fun onAppSelected(fdata:AppData){
//        Log.i("NARM",""+AppCommon.curFilterData)
        AppCommon.curFilterData?.packageNames?.add(fdata.packageName)
        val navController =  findNavController()
        navController.navigateUp()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
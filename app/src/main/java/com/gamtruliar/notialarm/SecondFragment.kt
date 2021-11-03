package com.gamtruliar.notialarm

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamtruliar.notialarm.Data.BanTimePeriod
import com.gamtruliar.notialarm.Data.FilterData
import com.gamtruliar.notialarm.Data.SubFilterData
import com.gamtruliar.notialarm.databinding.FragmentSecondBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import androidx.core.app.ActivityCompat.startActivityForResult

import android.media.RingtoneManager

import android.content.Intent
import android.net.Uri


import android.app.Activity.RESULT_OK
import android.graphics.drawable.Drawable
import android.widget.AdapterView
import android.widget.Toast
import com.gamtruliar.notialarm.Base.NoMenuFragment
import com.gamtruliar.notialarm.Data.AppData
import kotlinx.serialization.encodeToString


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : NoMenuFragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentSecondBinding? = null
    var prefs: SharedPreferences?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun loadFromData(filterData:FilterData){
        var data=ArrayList<AppData>()
        for(appInfo in filterData.packageNames){
            var nAppData=AppData()
            nAppData.packageName=appInfo
            var pm=context?.getPackageManager()!!
            try {
                val app_name= pm.getApplicationLabel(  pm.getApplicationInfo(appInfo,    PackageManager.GET_META_DATA ))
                val icon: Drawable = pm.getApplicationIcon(appInfo)
                nAppData.icon=icon
                nAppData.app_name=app_name.toString()
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e("NotiAlarm","NameNotFoundException:"+appInfo)
            }
            data.add(nAppData)
//            Log.i("NARM",appInfo)
        }
        val adapter = AppAdapter(data,true)
        binding.infscr.adapter = adapter
        adapter.onRowDeleteClick.plusAssign{x->onAppDelete(x)}

        binding.filterName.setText(filterData.filterName)

        var data2=ArrayList<SubFilterData>()
        for(appInfo in filterData.subFilterData){
            data2.add(appInfo.value)
//            Log.i("NARM",appInfo)
        }
        val adapter2 = SubFilterDataAdapter(data2)
        binding.infscrCond.adapter = adapter2
        adapter2.onRowClick.plusAssign{x->onSubConEdit(x)}
        adapter2.onRowDeleteClick.plusAssign{x->onSubConDelete(x)}

        binding.switchVibrate.isChecked= AppCommon.curFilterData?.needVibrate!!
        binding.switchSound.isChecked= AppCommon.curFilterData?.needSound!!

        binding.switchOSPBan.isChecked= AppCommon.curFilterData?.overrideBanTime!!
        if(binding.switchOSPBan.isChecked){
            binding.SPBPart1.visibility=View.VISIBLE
            binding.SPBPart2.visibility=View.VISIBLE
        }else{
            binding.SPBPart1.visibility=View.GONE
            binding.SPBPart2.visibility=View.GONE
        }

        var data3=ArrayList<BanTimePeriod>()
        for(appInfo in filterData.banTimes){
            data3.add(appInfo.value)
//            Log.i("NARM",appInfo)
        }
        val adapter3 = BanTimePeriodAdapter(data3)
        binding.infscrSPBan.adapter = adapter3
        adapter3.onRowClick.plusAssign{x->onSPBanEdit(x)}
        adapter3.onRowDeleteClick.plusAssign{x->onSPBanDelete(x)}
        if(AppCommon.curFilterData?.specSound==""){
            binding.SpSound.text = view?.context?.getString(R.string.Def)
        }else {
            var mtch=AppCommon.soundRegex.find( AppCommon.curFilterData?.specSound!!)
            if(mtch!=null){
                val (name) = mtch.destructured
                binding.SpSound.text = name
            }else {
                binding.SpSound.text = AppCommon.curFilterData?.specSound
            }
        }

        binding.spinner.setSelection(AppCommon.curFilterData!!.ringDuration)
    }
    fun onSubConEdit(data:SubFilterData){
        AppCommon.curSubFilter=data
        val navController =  findNavController()
        navController.navigate(R.id.action_SecondFragment_to_SubFilter)
    }
    fun onSubConDelete(data:SubFilterData){
        var filterData=AppCommon.curFilterData!!
        filterData.subFilterData.remove(data.uid)
        loadFromData(filterData)
    }
    fun onSPBanEdit(data:BanTimePeriod){
        AppCommon.curBanPeriod=data
        val navController =  findNavController()
        navController.navigate(R.id.action_SecondFragment_to_SelectOneTimePeriod)
    }
    fun onSPBanDelete(data:BanTimePeriod){
        var filterData=AppCommon.curFilterData!!
        filterData.banTimes.remove(data.uid)
        loadFromData(filterData)
    }
    fun onAppDelete(packageName:AppData){
        var filterData=AppCommon.curFilterData!!
        filterData.packageNames.remove(packageName.packageName)
        loadFromData(filterData)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        //var item= parent.getItemAtPosition(pos)
        AppCommon.curFilterData!!.ringDuration=pos

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs =AppCommon.getSharedPref(view.context)
        val navController =  findNavController()
        binding.fabApp.setOnClickListener { view ->
            navController.navigate(R.id.action_SecondFragment_to_SelectAppFragment)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.infscr.layoutManager = layoutManager
        val layoutManager2 = LinearLayoutManager(context)
        binding.infscrCond.layoutManager = layoutManager2
        binding.fabCond.setOnClickListener { view ->
            var nuid= AppCommon.curFilterData!!.subFilterDataUUID++
            var curEditData= SubFilterData()
            curEditData.uid=nuid
            AppCommon.curFilterData!!.subFilterData[nuid]= curEditData
            AppCommon.curSubFilter=curEditData
            navController.navigate(R.id.action_SecondFragment_to_SubFilter)
        }

        val layoutManager3 = LinearLayoutManager(context)
        binding.infscrSPBan.layoutManager = layoutManager3
        binding.fabSPBan.setOnClickListener { view ->
            var nBanTimePeriod=BanTimePeriod()
            nBanTimePeriod.uid=AppCommon.curFilterData!!.banTimesUUID++
            AppCommon.curFilterData!!.banTimes[nBanTimePeriod.uid]=nBanTimePeriod
            AppCommon.curBanPeriod=nBanTimePeriod
            navController.navigate(R.id.action_SecondFragment_to_SelectOneTimePeriod)
        }

        binding.filterName.addTextChangedListener { view->
            AppCommon.curFilterData?.filterName= binding.filterName.text.toString()
        }
        binding.switchVibrate.setOnCheckedChangeListener{view,checked->
            AppCommon.curFilterData?.needVibrate=checked
        }
        binding.switchSound.setOnCheckedChangeListener{view,checked->
            AppCommon.curFilterData?.needSound=checked
        }
        binding.switchOSPBan.setOnCheckedChangeListener{view,checked->
            AppCommon.curFilterData?.overrideBanTime=checked
            loadFromData( AppCommon.curFilterData!!)
        }

        binding.EditSPSound.setOnClickListener { view ->
            var currentTone: Uri = RingtoneManager.getActualDefaultRingtoneUri(
                this.context,
                RingtoneManager.TYPE_ALARM
            )
            if(AppCommon.curFilterData?.specSound!=""){
                currentTone=Uri.parse(AppCommon.curFilterData?.specSound!!)
            }
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
            startActivityForResult(intent, 999)
        }
        binding.spinner.onItemSelectedListener=this


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 999) {
            if (resultCode === RESULT_OK) {
                Log.i("NARM", "onActivityResult:" + requestCode + "|" + resultCode)
                if (data != null) {
                    if (data.extras != null) {
                        AppCommon.curFilterData?.specSound= data.extras!!["android.intent.extra.ringtone.PICKED_URI"].toString()
                        loadFromData(AppCommon.curFilterData!!)
//                        for (v in data.extras!!.keySet()) {
//                            Log.i("NARM", "DRES " + v + ":" + data.extras!![v])
//                        }
                    }
                }
            }
//                when (requestCode) {
//                    1 -> AppCommon.curFilterData?.specSound = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
//                    else -> {
//                    }
//                }
//            }
        }
    }

    override fun onPause() {
        super.onPause()

    }
    override fun onResume() {
        super.onResume()
        Log.i("NARM","FUID:"+AppCommon.curFilterData!!.filerUID)
        if(AppCommon.curBanPeriod!=null){
            if(AppCommon.curBanPeriod!!.fromTime== AppCommon.curBanPeriod!!.toTime && AppCommon.curBanPeriod!!.fromTime==0){
                AppCommon.curFilterData?.banTimes?.remove(AppCommon.curBanPeriod!!.uid)
            }
            AppCommon.curBanPeriod=null
        }
        if(AppCommon.curSubFilter!=null){
            if(AppCommon.curSubFilter!!.titleKeyWords== "" && AppCommon.curSubFilter!!.titleKeyWordsNot== "" && AppCommon.curSubFilter!!.textKeyWords== "" && AppCommon.curSubFilter!!.textKeyWordsNot== "" ){
                AppCommon.curFilterData?.subFilterData?.remove(AppCommon.curSubFilter!!.uid)
            }
            AppCommon.curSubFilter=null
        }
        loadFromData(AppCommon.curFilterData!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
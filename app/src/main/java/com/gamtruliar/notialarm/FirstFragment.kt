package com.gamtruliar.notialarm

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gamtruliar.notialarm.Data.FilterData
import com.gamtruliar.notialarm.databinding.FragmentFirstBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import com.google.android.gms.ads.AdRequest


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    var prefs: SharedPreferences?=null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    fun updateList(){
        var context=view?.context
        var allFilterUID=AppCommon.getAllFilterUIDS(prefs!!)
        var data=ArrayList<FilterData>()
        for(pname in allFilterUID){
            var filterData=AppCommon.getFilterDataByUID(prefs!!,pname)
            data.add(filterData)
        }
        val layoutManager = LinearLayoutManager(context)
        binding.infscr.layoutManager = layoutManager
        val adapter = FilterAdapter(data)
        binding.infscr.adapter = adapter
        val navController =  findNavController()
        adapter.onRowClick.plusAssign { d->
            AppCommon.curFilterData=d
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        adapter.onRowSwitchChange.plusAssign { d->
            AppCommon.saveFilter(d,prefs!!)
        }
        binding.AllSwitch.isChecked= prefs?.getBoolean(AppCommon.SETTING_KEY_ALLFUNCTION,true)!!
        binding.SoundSwitch.isChecked= prefs?.getBoolean(AppCommon.SETTING_KEY_ALLSOUND,true)!!
        binding.VibrateSwitch.isChecked= prefs?.getBoolean(AppCommon.SETTING_KEY_ALLVIBRATE,true)!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs =AppCommon.getSharedPref(view.context)

        val navController =  findNavController()

        binding.fab.setOnClickListener { view ->
            AppCommon.curFilterData=AppCommon.newFilter(prefs!!,this.context)
            navController.navigate(R.id.action_FirstFragment_to_SecondFragment)

        }
        binding.AllSwitch.setOnCheckedChangeListener{view,checked->
            var edit=prefs!!.edit()
            edit.putBoolean(AppCommon.SETTING_KEY_ALLFUNCTION,checked)
            edit.apply()
        }
        binding.SoundSwitch.setOnCheckedChangeListener{view,checked->
            var edit=prefs!!.edit()
            edit.putBoolean(AppCommon.SETTING_KEY_ALLSOUND,checked)
            edit.apply()
        }
        binding.VibrateSwitch.setOnCheckedChangeListener{view,checked->
            var edit=prefs!!.edit()
            edit.putBoolean(AppCommon.SETTING_KEY_ALLVIBRATE,checked)
            edit.apply()
        }
        var mAdView =binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
    override fun onResume() {
        super.onResume()
        if(AppCommon.curFilterData!=null){
            if(AppCommon.curFilterData!!.packageNames.count()>0) {
                AppCommon.saveFilter(AppCommon.curFilterData!!,prefs!!)
            }else{
                var allFilterUID = AppCommon.getAllFilterUIDS(prefs!!)
                var nuid = AppCommon.curFilterData!!.filerUID
                if(allFilterUID.contains(nuid)){
                    AppCommon.killFilter(nuid,prefs!!)
                }
            }
            AppCommon.curFilterData=null;
        }
        updateList()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
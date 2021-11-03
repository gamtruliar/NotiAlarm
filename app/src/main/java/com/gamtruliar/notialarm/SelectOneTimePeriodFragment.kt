package com.gamtruliar.notialarm

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.gamtruliar.notialarm.Base.NoMenuFragment
import com.gamtruliar.notialarm.Data.BanTimePeriod
import com.gamtruliar.notialarm.databinding.FragmentSelectOneTimePeriodBinding
import java.util.*


class SelectOneTimePeriodFragment : NoMenuFragment()  {

    var  curEditData:BanTimePeriod?=null;
    private var _binding: FragmentSelectOneTimePeriodBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

       _binding = FragmentSelectOneTimePeriodBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weekday0.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.SUNDAY)
            else curEditData?.weekDay?.remove(Calendar.SUNDAY)
        }
        binding.weekday1.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.MONDAY)
            else curEditData?.weekDay?.remove(Calendar.MONDAY)
        }
        binding.weekday2.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.TUESDAY)
            else curEditData?.weekDay?.remove(Calendar.TUESDAY)
        }
        binding.weekday3.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.WEDNESDAY)
            else curEditData?.weekDay?.remove(Calendar.WEDNESDAY)
        }
        binding.weekday4.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.THURSDAY)
            else curEditData?.weekDay?.remove(Calendar.THURSDAY)
        }
        binding.weekday5.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.FRIDAY)
            else curEditData?.weekDay?.remove(Calendar.FRIDAY)
        }
        binding.weekday6.setOnCheckedChangeListener{view,checked->
            if(checked)curEditData?.weekDay?.add(Calendar.SATURDAY)
            else curEditData?.weekDay?.remove(Calendar.SATURDAY)
        }
        binding.EditStartTime.setOnClickListener { view ->
            Log.i("NARM","TimePickerDialog")
            TimePickerDialog(activity,
                { v,h,m->
                    curEditData?.fromTime=h*60+m
                    binding.StartTime.setText(AppCommon.minInt2Str(curEditData?.fromTime!!))
                }, curEditData?.fromTime!!/60, curEditData?.fromTime!!%60, DateFormat.is24HourFormat(activity)).show()
        }
        binding.EditEndTime.setOnClickListener { view ->
            Log.i("NARM","TimePickerDialog2")
            TimePickerDialog(activity, { v,h,m->
                curEditData?.toTime=h*60+m
                binding.EndTime.setText(AppCommon.minInt2Str(curEditData?.toTime!!))
            }, curEditData?.toTime!!/60, curEditData?.toTime!!%60, DateFormat.is24HourFormat(activity)).show()
        }

    }

    fun loadAsData(){
        binding.StartTime.setText(AppCommon.minInt2Str(curEditData?.fromTime!!))
        binding.EndTime.setText(AppCommon.minInt2Str(curEditData?.toTime!!))
        binding.weekday0.isChecked=curEditData?.weekDay?.contains(Calendar.SUNDAY)!!
        binding.weekday1.isChecked=curEditData?.weekDay?.contains(Calendar.MONDAY)!!
        binding.weekday2.isChecked=curEditData?.weekDay?.contains(Calendar.TUESDAY)!!
        binding.weekday3.isChecked=curEditData?.weekDay?.contains(Calendar.WEDNESDAY)!!
        binding.weekday4.isChecked=curEditData?.weekDay?.contains(Calendar.THURSDAY)!!
        binding.weekday5.isChecked=curEditData?.weekDay?.contains(Calendar.FRIDAY)!!
        binding.weekday6.isChecked=curEditData?.weekDay?.contains(Calendar.SATURDAY)!!

    }

    override fun onResume() {
        super.onResume()
        curEditData=AppCommon.curBanPeriod
        loadAsData()
    }

}
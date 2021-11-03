package com.gamtruliar.notialarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.gamtruliar.notialarm.Base.NoMenuFragment
import com.gamtruliar.notialarm.Data.SubFilterData
import com.gamtruliar.notialarm.databinding.FragmentSecondBinding
import com.gamtruliar.notialarm.databinding.FragmentSubFilterBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "UID"

/**
 * A simple [Fragment] subclass.
 * Use the [SubFilterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubFilterFragment : NoMenuFragment() {

    var  curEditData:SubFilterData?=null;
    private var _binding: FragmentSubFilterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubFilterBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.titleKeyword.addTextChangedListener { view->
            curEditData?.titleKeyWords= binding.titleKeyword.text.toString()
        }
        binding.textKeyWord.addTextChangedListener { view->
            curEditData?.textKeyWords= binding.textKeyWord.text.toString()
        }
        binding.TitleRegSwitch.setOnCheckedChangeListener{view,checked->
            curEditData?.titleUseRegex=checked
        }
        binding.TextRegSwitch.setOnCheckedChangeListener{view,checked->
            curEditData?.textUseRegex=checked
        }

        binding.titleKeywordNot.addTextChangedListener { view->
            curEditData?.titleKeyWordsNot= binding.titleKeywordNot.text.toString()
        }
        binding.textKeyWordNot.addTextChangedListener { view->
            curEditData?.textKeyWordsNot= binding.textKeyWordNot.text.toString()
        }
        binding.TitleRegSwitchNot.setOnCheckedChangeListener{view,checked->
            curEditData?.titleUseRegexNot=checked
        }
        binding.TextRegSwitchNot.setOnCheckedChangeListener{view,checked->
            curEditData?.textUseRegexNot=checked
        }
    }

    fun loadAsData(){
        binding.titleKeyword.setText(curEditData?.titleKeyWords!!)
        binding.textKeyWord.setText(curEditData?.textKeyWords!!)
        binding.TitleRegSwitch.isChecked=curEditData?.titleUseRegex!!
        binding.TextRegSwitch.isChecked=curEditData?.textUseRegex!!
        binding.titleKeywordNot.setText(curEditData?.titleKeyWordsNot!!)
        binding.textKeyWordNot.setText(curEditData?.textKeyWordsNot!!)
        binding.TitleRegSwitchNot.isChecked=curEditData?.titleUseRegexNot!!
        binding.TextRegSwitchNot.isChecked=curEditData?.textUseRegexNot!!
    }

    override fun onResume() {
        super.onResume()

        curEditData=AppCommon.curSubFilter

        loadAsData()
    }

}
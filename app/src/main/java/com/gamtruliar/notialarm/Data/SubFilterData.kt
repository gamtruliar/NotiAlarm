package com.gamtruliar.notialarm.Data

import kotlinx.serialization.Serializable

@Serializable
data class SubFilterData (
    var uid:Int=0,
    var titleKeyWords:String="",
    var textKeyWords:String="",
    var titleUseRegex:Boolean=false,
    var textUseRegex:Boolean=false,
    var titleKeyWordsNot:String="",
    var textKeyWordsNot:String="",
    var titleUseRegexNot:Boolean=false,
    var textUseRegexNot:Boolean=false
)
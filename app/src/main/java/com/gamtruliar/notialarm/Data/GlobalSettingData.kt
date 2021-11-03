package com.gamtruliar.notialarm.Data

import kotlinx.serialization.Serializable

@Serializable
data class GlobalSettingData (
    var allFun_banTimes: HashMap<Int,BanTimePeriod> = HashMap<Int,BanTimePeriod>(),
    var allSound_banTimes: HashMap<Int,BanTimePeriod> = HashMap<Int,BanTimePeriod>(),
    var allVibrate_banTimes: HashMap<Int,BanTimePeriod> = HashMap<Int,BanTimePeriod>(),
    var all_banTimesUUID:Int=0,
    var gsversion:Int=-1
)
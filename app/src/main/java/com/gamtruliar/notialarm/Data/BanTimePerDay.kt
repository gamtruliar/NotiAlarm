package com.gamtruliar.notialarm.Data

import kotlinx.serialization.Serializable

@Serializable
data class BanTimePeriod (
    var uid:Int=0,
    var weekDay:HashSet<Int> = hashSetOf(1,2,3,4,5,6,7),
    var fromTime:Int=0,
    var toTime:Int=0
)
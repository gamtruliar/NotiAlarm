package com.gamtruliar.notialarm

import android.content.*
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.service.notification.NotificationListenerService.RankingMap
import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import android.preference.PreferenceManager

import android.net.Uri
import android.os.*
import android.widget.Toast
import com.gamtruliar.notialarm.CData.RingingData
import com.gamtruliar.notialarm.Data.BanTimePeriod
import com.gamtruliar.notialarm.Data.FilterData
import com.gamtruliar.notialarm.Enums.RingDurationType
import java.lang.Exception
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class NotificationCollectorService : NotificationListenerService() {
    var prefs:SharedPreferences?=null
    var alarming=HashSet<Int>()
    var uid2RingingData=HashMap<Int, RingingData>()
    var m_Recv:ActionBReceiver?=null
    var m_Recv_lang:ActionBReceiver?=null



    fun regRecv(){


        var mRecv=ActionBReceiver { c, intv ->
//            Log.i("NARM","recv")
            onNotiRecv(c, intv)
        }
        m_Recv=mRecv
        var ifilter= IntentFilter()
        ifilter.addAction(AppCommon.ACTION_NARM_NAction)
        registerReceiver(mRecv,ifilter)

        mRecv=ActionBReceiver { c, intv ->
            onNLangRecv(c, intv)
        }
        m_Recv_lang=mRecv
        ifilter= IntentFilter()
        ifilter.addAction(AppCommon.ACTION_NARM_NAction_LANG)
        registerReceiver(mRecv,ifilter)

        mRecv=ActionBReceiver { c, intv ->
            onTestRecv(c, intv)
        }
        m_Recv_lang=mRecv
        ifilter= IntentFilter()
        ifilter.addAction(AppCommon.ACTION_NARM_NAction_Test)
        registerReceiver(mRecv,ifilter)


    }
    fun relocal(){
        Log.i("NARM","relocal")
        AppCommon.checkStartLang(this)
    }
    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        relocal()
        regRecv()
    }

    override fun onBind(intent: Intent?): IBinder? {
        AppCommon.createNotificationChannel(this)
        relocal()
        regRecv()
        return super.onBind(intent)

    }

    override fun onUnbind(intent: Intent?): Boolean {
        if(m_Recv!=null)unregisterReceiver(m_Recv)
        m_Recv=null
        if(m_Recv_lang!=null)unregisterReceiver(m_Recv_lang)
        m_Recv_lang=null
        return super.onUnbind(intent)
    }
    fun shotRingWithNoti(notiUID:Int,filterName:String,soundUri:Uri,isSound:Boolean,isVibrate:Boolean,durationType:Int){
        if(alarming.contains(notiUID))return

        AppCommon.sendNoti(this,getString(R.string.alarmTitle).format(filterName),getString(R.string.alarmText),Bundle(),123+notiUID,{it->

            it.putExtra("FilterUID",notiUID)
        })
        var nRingData=RingingData()
        uid2RingingData[notiUID]=nRingData
        var played:Boolean=false
        if(isSound && canSound()) {
            try {
                nRingData.mediaPlayer = AppCommon.playAlermSnd(this, soundUri, durationType)
            }catch ( ex: Exception){
                Log.e("NARM",""+ex.message)
            }
            played=true
        }
        if(isVibrate && canVibrate()){
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
            var execer= Executors.newSingleThreadExecutor();
            var vibrationActive=true

            execer.execute(Runnable {
                while(vibrationActive){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE))
                    }else{
                        v.vibrate(1000)
                    }
                    Thread.sleep(1000);
                    if(durationType== RingDurationType.Once.ordinal){
                        vibrationActive=false
                    }
                }
            })
            nRingData.vibStoper={->
                vibrationActive=false
            }
            played=true
        }
        if(played){
            if(durationType== RingDurationType.Forever.ordinal){

            }else{
                var execer= Executors.newSingleThreadExecutor();
                var dur=AppCommon.RingDurationType2MS[durationType]!!
                if(dur>0) {
                    var durActive = true
                    var endTime = System.currentTimeMillis() + dur;
                    execer.execute(Runnable {
                        while (durActive && System.currentTimeMillis()<endTime) {
                            Thread.sleep(1000 * 60)
                        }
                        nRingData.mediaPlayer?.stop()
                        nRingData.vibStoper?.invoke()
                    })
                    nRingData.durStoper={->
                        durActive=false
                    }
                }
            }
        }
        alarming.add(notiUID)
    }
    fun onNLangRecv(context: Context?,intent: Intent?) {
        relocal()
    }
    fun onNotiRecv(context: Context?,intent: Intent?){
        if(intent==null)return
        var fuid: Int=0;
        if(intent.hasExtra("FilterUID")) {
            fuid = intent.getIntExtra("FilterUID", 0);
        }
        Log.i("NARM","onNotiRecv:"+fuid)
//        Toast.makeText(context,"wawaw:"+intent.getIntExtra("FilterUID",0), Toast.LENGTH_LONG).show()
        if(intent.hasExtra("FilterUID")){

            var rUID=fuid
            alarming.remove(rUID)
            stopSoundByUID(rUID)
        }
    }
    fun onTestRecv(context: Context?,intent: Intent?) {
        if(intent==null)return
        if(prefs==null){
            var pName=getPackageName()+"_preferences";
            //Log.i("wawa", "pName:" + pName)
            prefs = getSharedPreferences( pName, Context.MODE_PRIVATE);
        }
        var fuid: Int=0;
        if(intent.hasExtra("FilterUID")) {
            fuid = intent.getIntExtra("FilterUID", 0);
        }
        Log.i("NARM","onTestRecv:"+fuid)
        if(intent.hasExtra("FilterUID")){
            var uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            var filterData=AppCommon.getFilterDataByUID(prefs!!,fuid)
            if(filterData.specSound!=""){
                uri= Uri.parse(filterData.specSound)
            }
            shotRingWithNoti(filterData.filerUID,filterData.filterName,uri,filterData.needSound,filterData.needVibrate,filterData.ringDuration)
        }
    }

    fun stopSoundByUID(uid:Int){
        if(uid2RingingData.containsKey(uid)){
            var rdada=uid2RingingData[uid]
            rdada?.mediaPlayer?.stop()
            rdada?.vibStoper?.invoke()
            rdada?.durStoper?.invoke()
            uid2RingingData.remove(uid)
        }

    }
    fun checkWithInTime(periods:HashMap<Int, BanTimePeriod>):Boolean{
        val calendar: Calendar = Calendar.getInstance()
        val nDOW: Int = calendar.get(Calendar.DAY_OF_WEEK)
        var nHM=calendar.get(Calendar.HOUR_OF_DAY)*60+calendar.get(Calendar.MINUTE)
        var stTimes=ArrayList<Int>()
        var edTimes=ArrayList<Int>()
        var endOfDay=60*24
        for(v in periods){
            if(v.value.weekDay.contains(nDOW)){
                var sttime=v.value.fromTime
                var edtime=v.value.toTime
                if(edtime<sttime)edtime=endOfDay
                stTimes.add(sttime)
                edTimes.add(edtime)
            }
            if(v.value.weekDay.contains(((nDOW-2)%7)+1)){
                if(v.value.toTime<v.value.fromTime){
                    var sttime=0
                    var edtime=v.value.toTime
                    stTimes.add(sttime)
                    edTimes.add(edtime)
                }
            }
        }
        stTimes.sort()
        edTimes.sort()
        var lastEdTimeIdx=0
        var sti=0
        while (sti < stTimes.size){
            var stt=stTimes[sti]
            for(i in lastEdTimeIdx until edTimes.size){
                var edt=edTimes[i]
                if(edt<stt)continue
                while(sti+1<stTimes.size && edt>stTimes[sti+1])sti++ //skip to found next start time
                if((sti+1>=stTimes.size || edt>stTimes[sti+1]) && i<edTimes.size-1)continue // skip to found last end time before next start time
               // println("chk:"+stt+"->"+edt)
                if(stt<=nHM && nHM<edt){
                    //println("tttt:"+stt+"->"+edt)
                    return true
                }
                lastEdTimeIdx=i+1
                break
            }
            sti++
        }
        return false
    }
    fun canFun(filterData: FilterData):Boolean{
        if(filterData.overrideBanTime){
            return checkWithInTime(filterData.banTimes)==false
        }
        var gsSetting=AppCommon.getGSData(prefs!!)
        return checkWithInTime(gsSetting.allFun_banTimes)==false
    }
    fun canSound():Boolean{
        if(prefs!!.getBoolean(AppCommon.SETTING_KEY_ALLSOUND,true)==false)return false
        var gsSetting=AppCommon.getGSData(prefs!!)
        return checkWithInTime(gsSetting.allSound_banTimes)==false
    }
    fun canVibrate():Boolean{
        if(prefs!!.getBoolean(AppCommon.SETTING_KEY_ALLVIBRATE,true)==false)return false
        var gsSetting=AppCommon.getGSData(prefs!!)
        return checkWithInTime(gsSetting.allVibrate_banTimes)==false
    }
    override fun onNotificationPosted(sbn: StatusBarNotification, rankingMap: RankingMap) {
        if(prefs==null){
            var pName=getPackageName()+"_preferences";
            //Log.i("wawa", "pName:" + pName)
            prefs = getSharedPreferences( pName, Context.MODE_PRIVATE);
        }
        if(!prefs!!.getBoolean(AppCommon.SETTING_KEY_ALLFUNCTION,true))return




        var includedPackageName= prefs?.getStringSet(AppCommon.SETTING_KEY_sbnIncPName,HashSet<String>());

//        Log.i("wawa", "open" + "-----" + sbn.packageName)
//
//        Log.i("wawa", "open" + "------" + sbn.notification.tickerText)
//        Log.i("wawa", "open" + "-----" + sbn.notification.extras["android.title"])
//        Log.i("wawa", "open" + "-----" + sbn.notification.extras["android.text"])
////        Log.i("wawa", "open" + "-----" + Json.encodeToString(sbn.notification.extras.keySet()))
//        for (v in sbn.notification.extras.keySet()){
//            Log.i("wawa", "open" + v+":" + sbn.notification.extras[v])
//        }
//        Log.i("wawa", "open" + "-inc:" + Json.encodeToString(includedPackageName))
        if(includedPackageName?.contains( sbn.packageName) == true) {
            var uidMap=AppCommon.getPackName2UIDMap(prefs!!)
            if(uidMap.containsKey(sbn.packageName)){
                for(uid in uidMap[sbn.packageName]!!){
                    //--to do: cache
                    oneFilterHandle(AppCommon.getFilterDataByUID(prefs!!,uid),""+sbn.notification.extras["android.title"],""+sbn.notification.extras["android.text"])
                }
            }

        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
//        Log.i("wawa", "kill" + "-----" + sbn.packageName)
//
//        Log.i("wawa", "kill" + "------" + sbn.notification.tickerText)
//        Log.i("wawa", "kill" + "-----" + sbn.notification.extras["android.title"])
//        Log.i("wawa", "kill" + "-----" + sbn.notification.extras["android.text"])
////        Log.i("wawa", "open" + "-----" + Json.encodeToString(sbn.notification.extras.keySet()))
//        for (v in sbn.notification.extras.keySet()){
//            Log.i("wawa", "kill" + v+":" + sbn.notification.extras[v])
//        }

    }

    fun subkeywordCheck(KeyWords:String,useRegex:Boolean,targetwords:String):Boolean{
        if(KeyWords!=""){

            if(useRegex){
                if(Regex(KeyWords).matches(targetwords)){
                    return true
                }
            }else{
                var keywords=KeyWords.split(' ')
                for(v in keywords){
                    if(v.trim()=="")continue
                    if(targetwords.contains(v)==false)return false
                }
                return true

            }
        }else{
           return true
        }
        return false
    }
    fun keywordPassCheck(filterData: FilterData,title:String,text:String):Boolean{
        var anychk=false

        for (sdata in filterData.subFilterData){
            anychk=true
            var anyBanPassed=false
            var title_passed=false
            var text_passed=false
            title_passed=subkeywordCheck(sdata.value.titleKeyWords.trim(),sdata.value.titleUseRegex,title)
            text_passed=subkeywordCheck(sdata.value.textKeyWords.trim(),sdata.value.textUseRegex,text)
            if(sdata.value.titleKeyWordsNot.trim()!="") {
                anyBanPassed = subkeywordCheck(
                    sdata.value.titleKeyWordsNot,
                    sdata.value.titleUseRegexNot,
                    title
                )
            }
            if(sdata.value.textKeyWordsNot.trim()!="") {
                anyBanPassed = anyBanPassed || subkeywordCheck(
                    sdata.value.textKeyWordsNot,
                    sdata.value.textUseRegexNot,
                    text
                )
            }
//            Log.i("NARM","title_passed:"+title_passed)
//            Log.i("NARM","text_passed:"+text_passed)
//            Log.i("NARM","anyBanPassed:"+anyBanPassed)
            if((title_passed && text_passed) && !anyBanPassed)return true
        }
        return !anychk
    }

    fun oneFilterHandle(filterData: FilterData,title:String,text:String){
        if(filterData.enable==false)return;
        if(canFun(filterData)==false)return;
        if(keywordPassCheck(filterData,title,text)){
            var uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            if(filterData.specSound!=""){
                uri= Uri.parse(filterData.specSound)
            }
            shotRingWithNoti(filterData.filerUID,filterData.filterName,uri,filterData.needSound,filterData.needVibrate,filterData.ringDuration)

        }

    }
}
package com.gamtruliar.notialarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast


open class ActionBReceiver(var cb:(context:Context?,intent: Intent?)->Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.i("NARM","ActionBReceiver...recvvvvvvvv")

        cb(context,intent)
    }
}
package com.gamtruliar.notialarm

import android.Manifest
import android.app.AlertDialog
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.gamtruliar.notialarm.databinding.ActivityMainBinding
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.os.*
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.gamtruliar.notialarm.Enums.LangType
import com.google.android.gms.ads.initialization.InitializationStatus

import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

import com.google.android.gms.ads.MobileAds
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    fun gotoSettingPage(){
        var string: String = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        if (!string.contains(NotificationCollectorService::class.java.name)) {
            AlertDialog.Builder(this@MainActivity)
                .setMessage(getString(R.string.permissionText1))
                .setPositiveButton("OK") { _, _ ->
                    startActivity(
                        Intent(
                            "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"
                        )
                    )
                }
                .setNegativeButton("No") { _, _ -> finish() }
                .show()

        }
        string = Settings.Secure.getString(contentResolver,        "enabled_notification_listeners")
        if (!string.contains(NotificationCollectorService::class.java.name)) {
            Toast.makeText(this,getString(R.string.permissionText1),Toast.LENGTH_LONG)
        }
    }
    fun reqPermission(permissions: Array<String>)
    {
        if (
            permissions.any() { x->ContextCompat.checkSelfPermission(this@MainActivity,x) != PackageManager.PERMISSION_GRANTED }
        ) {
            if (
                permissions.any() { x-> ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, x)}
            ) {
                AlertDialog.Builder(this@MainActivity)
                    .setMessage(getString(R.string.permissionText2))
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            permissions,
                            1562
                        )
                    }
                    .setNegativeButton("No") { _, _ -> finish() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permissions
                    ,
                    1562
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Log.i("wawa", "onCreate");

//        Log.i("wawa", "requestPermissions");
        gotoSettingPage();
        reqPermission(arrayOf(Manifest.permission.QUERY_ALL_PACKAGES,Manifest.permission.VIBRATE,Manifest.permission.INTERNET));
        MobileAds.initialize(
            this
        ) { }
//        AppCommon.playAlermSnd(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
//        Log.i("wawa", "play alarm");
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        AppCommon.checkStartLang(this)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }
    override fun onRequestPermissionsResult(requestCode: Int,  permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("wawa",""+grantResults)
                } else {
                    finish()
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
//        menuInflater.inflate(R.menu.action_about, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return when (item.itemId) {
            R.id.action_settings -> {

                navController.navigate(R.id.action_FirstFragment_to_Setting)
                true
            }
            R.id.action_about -> {

                navController.navigate(R.id.action_FirstFragment_to_About)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}
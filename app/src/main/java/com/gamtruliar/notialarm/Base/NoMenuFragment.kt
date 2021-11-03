package com.gamtruliar.notialarm.Base

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.gamtruliar.notialarm.R

open class NoMenuFragment  : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        var item: MenuItem = menu.findItem(R.id.action_settings)
        if (item != null) item.setVisible(false)
        item = menu.findItem(R.id.action_about)
        if (item != null) item.setVisible(false)
    }
}
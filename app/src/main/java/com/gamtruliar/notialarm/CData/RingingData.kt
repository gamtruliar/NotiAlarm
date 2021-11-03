package com.gamtruliar.notialarm.CData

import android.media.MediaPlayer

class RingingData {
    var mediaPlayer: MediaPlayer?=null
    var vibStoper: (() -> Unit)? = null
    var durStoper: (() -> Unit)? = null
}
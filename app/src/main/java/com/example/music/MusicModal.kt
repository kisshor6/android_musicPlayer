package com.example.music

import java.util.concurrent.TimeUnit

class MusicModal(
    val icon : Int,
    val title : String,
    val album : String,
    val path : String,
    val duration : Long,

)
fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val second = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)

    return String.format("%02d:%02d", minutes, second)
}

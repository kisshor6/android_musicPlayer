package com.example.music

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.system.exitProcess

class MusicItem : AppCompatActivity(), MediaPlayer.OnCompletionListener {
    companion object{
        lateinit var musicListPA : ArrayList<MusicModal>
        var songPosition : Int = 0
        private var mediaPlayer: MediaPlayer? = null
        var min_15 : Boolean = false
        var min_25 : Boolean = false
        var min_30 : Boolean = false

    }

    private lateinit var runnable: Runnable
    private lateinit var songDuration: TextView
    private lateinit var songCurrDuration: TextView
    private lateinit var seekbar: SeekBar
    private lateinit var songName: TextView
    private lateinit var imgIcon: ImageView
    private lateinit var playBtn: Button
    private  var repeatSong : Boolean = false
    private lateinit var timerBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_item)

        // Defining id's

        val backPress = findViewById<ImageView>(R.id.backPress)
        backPress.setOnClickListener {
            finish()
        }
        playBtn = findViewById(R.id.playBtn)
        songName = findViewById(R.id.songName)
        songDuration = findViewById(R.id.songStop)
        songCurrDuration = findViewById(R.id.songStart)
        imgIcon = findViewById(R.id.music_icon)
        val previousBtn = findViewById<TextView>(R.id.previousBtn)
        val nextBtn = findViewById<TextView>(R.id.nextBtn)
        val repeat = findViewById<Button>(R.id.repeat)
        timerBtn = findViewById(R.id.timer)
        seekbar = findViewById(R.id.seekbar)

        songPosition = intent.getIntExtra("index", 0)
        musicListPA = ArrayList()
        musicListPA.addAll(MainActivity.musicListMA)

        playMusic()


        playBtn.setOnClickListener{
            if (mediaPlayer!!.isPlaying){
                playBtn.setBackgroundResource(R.drawable.play_arrow_24)
                mediaPlayer?.pause()
                imgIcon.clearAnimation()
            }else{
                playBtn.setBackgroundResource(R.drawable.pause_24)
                mediaPlayer?.start()
                imgIcon.startAnimation(rotateIcon())
            }
        }

        nextBtn.setOnClickListener {
            if (songPosition == musicListPA.size - 1) songPosition = 0
            else ++songPosition
            playMusic()
        }

        previousBtn.setOnClickListener {
            if (songPosition == 0) songPosition = musicListPA.size - 1
            else --songPosition
            playMusic()

        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        repeat.setOnClickListener {
            if(!repeatSong){
                repeatSong = true
                repeat.setBackgroundResource(R.drawable.repeat_one)
                Toast.makeText(this, "single play mode", Toast.LENGTH_SHORT).show()
            }else{
                repeatSong = false
                repeat.setBackgroundResource(R.drawable.repeat)
                Toast.makeText(this, "random play mode", Toast.LENGTH_SHORT).show()
            }

        }
        timerBtn.setOnClickListener {
            val timer = min_15 || min_25 || min_30
            if (!timer) showTimerDialog()
            else{
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Stop Timer")
                    .setMessage("Do you want to stop timer?")
                    .setPositiveButton("Yes") { dialog, which ->
                        min_30 = false
                        min_25 = false
                        min_15 = false

                        timerBtn.setBackgroundResource(R.drawable.access_time)
                        // Do something when the user clicks the Delete button
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                        // Do something when the user clicks the Cancel button
                    }

                val dialog = builder.create()
                dialog.show()
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.WHITE)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)
            }

        }
    }

    private fun showTimerDialog() {
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.timer)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {

            Toast.makeText(this, "Music will stop in 15 minutes", Toast.LENGTH_SHORT).show()
            timerBtn.setBackgroundResource(R.drawable.timer)
            min_15 = true
            stopMusic(15)
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_25)?.setOnClickListener {
            Toast.makeText(this, "Music will stop in 25 minutes", Toast.LENGTH_SHORT).show()
            timerBtn.setBackgroundResource(R.drawable.timer)
            min_25 = true
            stopMusic(25)
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(this, "Music will stop in 30 minutes", Toast.LENGTH_SHORT).show()
            timerBtn.setBackgroundResource(R.drawable.timer)
            min_30 = true
            stopMusic(30)
            dialog.dismiss()
        }
    }

    private fun stopMusic(i: Int) {
        Thread{Thread.sleep(((i * 6000).toLong()))
            if (min_30 && min_25 && min_15) {
                mediaPlayer?.pause()
            }
                imgIcon.clearAnimation()
                playBtn.setBackgroundResource(R.drawable.play_arrow_24)
        }.start()
    }

    private fun playMusic() {
        if (mediaPlayer == null) mediaPlayer = MediaPlayer()
        mediaPlayer?.stop()
        mediaPlayer?.reset()
        mediaPlayer?.setDataSource(musicListPA[songPosition].path)
        mediaPlayer?.prepare()
        mediaPlayer?.start()
        songName.text = musicListPA[songPosition].title
        imgIcon.animation = rotateIcon()
        songDuration.text = formatDuration(musicListPA[songPosition].duration)
        songCurrDuration.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
        seekbar.progress = 0
        seekbar.max = mediaPlayer!!.duration
        mediaPlayer?.setOnCompletionListener(this)
        initializeSeekBar()
        if (repeatSong){
            mediaPlayer?.isLooping = true
        }
    }

    private fun rotateIcon(): Animation? {
        val rotateAnim = RotateAnimation(0F,
            360F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.interpolator = LinearInterpolator()
        rotateAnim.duration = 15000
        rotateAnim.repeatCount = Animation.INFINITE

        return rotateAnim
    }

    fun initializeSeekBar(){
        runnable = Runnable {
            songCurrDuration.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            seekbar.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(
                runnable, 200
            )
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (songPosition == musicListPA.size - 1) songPosition = 0
        else ++songPosition
        playMusic()
    }

}
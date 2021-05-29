package com.sagitest.countdowntimer

import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    var START_MILLIS: Long ?= null

    private var cdt: CountDownTimer ?= null
    var Runner = false
    var TimeLeftInMillis: Long ?= null
    var TimeEnd: Long = 10

    var songPlayer: MediaPlayer ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun TReact(view: View) {
        if (Runner) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun TReset(view: View) {resetTimer()}

    fun TSet(view: View) {
        var input: String = et_input.text.toString()
        if (input.length == 0) {
            Toast.makeText(this@MainActivity, "Field can't be empty", Toast.LENGTH_SHORT).show()
            return
        }

        var millisInput: Long = input.toLong() * 60000
        var zero: Long = 0
        if (millisInput == zero) {
            Toast.makeText(this@MainActivity, "Please enter a positive number", Toast.LENGTH_SHORT).show()
            return
        }

        setTimer(millisInput)
        et_input.setText("")
    }

    private fun setTimer(milliseconds: Long) {
        START_MILLIS = milliseconds
        resetTimer()
    }

    private fun startTimer() {
        TimeEnd = System.currentTimeMillis() + TimeLeftInMillis!!

        cdt = object : CountDownTimer( TimeLeftInMillis!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                TimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }
            override fun onFinish() {
                Runner = false
                btnSP.text = "Start"
                tvCounter.text = "00:00"

                //play a STOP! sound from raw folder
                playSound()

                watchInterfaceUpper()
            }
        }.start()

        Runner = true
        watchInterfaceUpper()
    }

    private fun pauseTimer() {
        cdt?.cancel()
        Runner = false
        watchInterfaceUpper()
    }

    private fun resetTimer() {
        TimeLeftInMillis = START_MILLIS
        updateCountDownText()
        watchInterfaceUpper()
    }

    private fun playSound() {
        if (songPlayer == null) {
            songPlayer = MediaPlayer.create(this, R.raw.sound1)
            songPlayer!!.isLooping = false
            songPlayer!!.start()
        } else songPlayer!!.start()
    }

    private fun updateCountDownText() {
        var hrs: Int = ((TimeLeftInMillis!! / 1000) / 3600).toInt()
        var mins: Int = (((TimeLeftInMillis!! / 1000) % 3600) / 60).toInt()
        var secs: Int = ((TimeLeftInMillis!! / 1000) % 60).toInt()

        if (hrs > 0) {
            tvCounter.text = String.format("%d:%02d:%02d", hrs, mins, secs)
        } else {
            tvCounter.text = String.format("%02d:%02d", mins, secs)
        }


        //var secs = (TimeLeftInMillis!!/1000).toInt()
        //val mins = secs / 60
        //val hrs = mins / 60
        //secs %= 60
        //tvCounter.text = String.format("%02d:%02d", mins, secs)
    }

    private fun watchInterfaceUpper() {
        if (Runner) {
            et_input.visibility = View.INVISIBLE
            btn_set.visibility = View.INVISIBLE
            btnReset.visibility = View.INVISIBLE
            btnSP.text = "Pause"
        } else {
//            when timer is not running, it can be:
//                -can be at 0 so reset visible and start not visible
//                -at full time so start visible and reset not visible
//                -not full or end so both start and reset are visible
            et_input.visibility = View.VISIBLE
            btn_set.visibility = View.VISIBLE
            btnSP.text = "Start"

            //for time somewhere in the middle of time
            if (TimeLeftInMillis!! < 1000) {
                btnSP.visibility = View.INVISIBLE
            } else {
                btnSP.visibility = View.VISIBLE
            }

            //not full
            if (TimeLeftInMillis!! < START_MILLIS!!) {
                btnReset.visibility = View.VISIBLE
            } else {
                btnReset.visibility = View.INVISIBLE
            }
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  prefs.edit()

        editor.putLong("startMillis", START_MILLIS!!)
        editor.putLong("MilliTimeLeft", TimeLeftInMillis!!)
        editor.putBoolean("Workin", Runner)
        editor.putLong("Ender", TimeEnd)

        editor.apply()

        if (cdt != null) {
            cdt?.cancel()
        }
    }

    override fun onStart() {
        super.onStart()

        var prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)

        START_MILLIS = prefs.getLong("startMillis", 900000)
        TimeLeftInMillis = prefs.getLong("MilliTimeLeft", START_MILLIS!!)
        Runner = prefs.getBoolean("Workin", false)

        updateCountDownText()
        watchInterfaceUpper()

        if (Runner) {
            TimeEnd = prefs.getLong("Ender", 0)
            TimeLeftInMillis = TimeEnd - System.currentTimeMillis()

            if (TimeLeftInMillis!! < 0) {
                TimeLeftInMillis = 0
                Runner = false

                updateCountDownText()
                watchInterfaceUpper()
            } else {
                startTimer()
            }

        }
    }
}

//onSave and onRestore are for orientation only
//onStop and onStart is for background support and orientation
/*
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putLong("MilliTimeLeft", TimeLeftInMillis)
    outState.putBoolean("Workin", Runner)
    outState.putLong("Ender", TimeEnd)
}

override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)

    TimeLeftInMillis = savedInstanceState.getLong("MilliTimeLeft")
    Runner = savedInstanceState.getBoolean("Workin")
    updateCountDownText()
    buttonUpper()

    if (Runner) {
        TimeEnd = savedInstanceState.getLong("Ender")
        startTimer()
    }
}
 */
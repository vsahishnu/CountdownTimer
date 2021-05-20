package com.sagitest.countdowntimer

import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val SECONDS_MILLIES = 1 * 1000L
    val START_MILLIS = 10 * 60 * SECONDS_MILLIES

    private var cdt: CountDownTimer ?= null
    var Runner = false
    var TimeLeftInMillis: Long = START_MILLIS
    var TimeEnd: Long = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCountDownText()
        buttonUpper()
    }

    fun TReact(view: View) {
        if (Runner) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun TReset(view: View) {resetTimer()}

    private fun startTimer() {
        TimeEnd = System.currentTimeMillis() + TimeLeftInMillis

        cdt = object : CountDownTimer( TimeLeftInMillis, SECONDS_MILLIES) {
            override fun onTick(millisUntilFinished: Long) {
                TimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }
            override fun onFinish() {
                Runner = false
                btnSP.text = "Start"
                buttonUpper()
            }
        }.start()

        Runner = true
        buttonUpper()
    }

    private fun pauseTimer() {
        cdt?.cancel()
        Runner = false
        buttonUpper()
    }

    private fun resetTimer() {
        TimeLeftInMillis = START_MILLIS
        updateCountDownText()
        buttonUpper()
    }

    private fun updateCountDownText() {
        var secs = (TimeLeftInMillis/1000).toInt()
        val mins = secs / 60
        secs %= 60

        tvCounter.text = String.format("%02d:%02d", mins, secs)
    }

    private fun buttonUpper() {
        if (Runner) {
            btnReset.visibility = View.INVISIBLE
            btnSP.text = "Pause"
        } else {
//            when timer is not running, it can be:
//                -can be at 0 so reset visible and start not visible
//                -at full time so start visible and reset not visible
//                -not full or end so both start and reset are visible
            btnSP.text = "Start"

            //for time somewhere in the middle of time
            if (TimeLeftInMillis < 1000) {
                btnSP.visibility = View.INVISIBLE
            } else {
                btnSP.visibility = View.VISIBLE
            }

            //not full
            if (TimeLeftInMillis < START_MILLIS) {
                btnReset.visibility = View.VISIBLE
            } else {
                btnReset.visibility = View.INVISIBLE
            }
        }
    }

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
}
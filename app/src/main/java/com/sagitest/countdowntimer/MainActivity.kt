package com.sagitest.countdowntimer

import android.os.CountDownTimer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val SECONDS_MILLIES = 1 * 1000L
    val START_MILLIS = 10 * 60 * SECONDS_MILLIES

    private var cdt: CountDownTimer ?= null
    var Runner = true
    var TimeLeftInMillis: Long = START_MILLIS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateCountDownText()
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
        cdt = object : CountDownTimer( TimeLeftInMillis, SECONDS_MILLIES) {
            override fun onTick(millisUntilFinished: Long) {
                TimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }
            override fun onFinish() {
                Runner = false
                btnSP.text = "Start"
                btnSP.visibility = View.INVISIBLE
                btnReset.visibility = View.VISIBLE
            }
        }.start()

        Runner = true
        btnSP.text = "Pause"
        btnReset.visibility = View.INVISIBLE
    }

    private fun pauseTimer() {
        cdt?.cancel()
        Runner = false
        btnSP.text = "Start"
        btnReset.visibility = View.VISIBLE
    }

    private fun resetTimer() {
        TimeLeftInMillis = START_MILLIS
        updateCountDownText()
        btnReset.visibility = View.INVISIBLE
        btnSP.visibility = View.VISIBLE
    }

    private fun updateCountDownText() {
        var secs = (TimeLeftInMillis/1000).toInt()
        val mins = secs / 60
        secs %= 60

        tvCounter.text = String.format("%02d:%02d", mins, secs)
    }
}
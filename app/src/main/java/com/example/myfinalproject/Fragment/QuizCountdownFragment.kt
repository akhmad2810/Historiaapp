package com.example.myfinalproject.Fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myfinalproject.R

class QuizCountdownFragment : Fragment() {

    private lateinit var tvCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_quiz_countdown_fragment, container, false)
        tvCount = view.findViewById(R.id.tvCountdown)

        startCountdown()
        return view
    }

    private fun startCountdown() {
        object : CountDownTimer(4000, 1000) {
            var number = 3

            override fun onTick(millisUntilFinished: Long) {
                if (number == 0) tvCount.text = "MULAI!"
                else tvCount.text = number.toString()
                number--
            }

            override fun onFinish() {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.containerFragment, QuizQuestionsFragment())
                    .commit()
            }
        }.start()
    }
}

package com.example.myfinalproject.Fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myfinalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class QuizHomeFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvLastScore: TextView
    private lateinit var btnStart: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.activity_quiz_home_fragment, container, false)

        tvUsername = view.findViewById(R.id.tvQuizUsername)
        tvLastScore = view.findViewById(R.id.tvQuizLastScore)
        btnStart = view.findViewById(R.id.btnStartQuiz)

       
        loadUserData()

        btnStart.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Mulai Quiz?")
                .setMessage("Quiz akan dimulai dan waktu berjalan. Lanjut?")
                .setPositiveButton("Mulai") { _, _ ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.containerFragment, QuizCountdownFragment())
                        .addToBackStack(null)
                        .commit()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadUserData() 
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser

        val username = user?.displayName ?: user?.email ?: "User"

        
        val userPref = requireContext().getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val lastScore = userPref.getInt("LAST_SCORE", 0)

        tvUsername.text = username
        tvLastScore.text = "Nilai Terakhir: $lastScore"
    }
}

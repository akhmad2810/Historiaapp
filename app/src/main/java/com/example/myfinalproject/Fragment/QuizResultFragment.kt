package com.example.myfinalproject.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myfinalproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


data class LeaderboardItem(
    val name: String = "",
    val score: Int = 0,
    val title: String = "Quiz Game"
)

class QuizResultFragment : Fragment() {

    companion object {
        fun newInstance(score: Int): QuizResultFragment {
            val fragment = QuizResultFragment()
            val bundle = Bundle()
            bundle.putInt("SCORE", score)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.quiz_result_fragment, container, false)

        val score = arguments?.getInt("SCORE", 0) ?: 0

        val tvLastScore = view.findViewById<TextView>(R.id.tvFinalScore)
        val btnBack = view.findViewById<Button>(R.id.btnBackQuiz)

      
        tvLastScore.text = "Nilai Anda: $score"

        
        val userPref = requireContext().getSharedPreferences("USER_PREF", 0)
        userPref.edit().putInt("LAST_SCORE", score).apply()

       
        saveScoreToLeaderboard(score)

        btnBack.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.containerFragment, QuizHomeFragment())
                .commit()
        }

        return view
    }

    
    private fun saveScoreToLeaderboard(score: Int) {
        val db = FirebaseDatabase.getInstance().reference

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: db.push().key!!
        val userName = user?.displayName ?: user?.email ?: "Pengguna"

        
        val title = when {
            score >= 90 -> "Master Quiz"
            score >= 70 -> "Advanced"
            score >= 50 -> "Intermediate"
            else -> "Beginner"
        }

        val data = LeaderboardItem(userName, score, title)

        db.child("leaderboard").child(userId).setValue(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Skor berhasil tersimpan!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan skor", Toast.LENGTH_SHORT).show()
            }
    }
}

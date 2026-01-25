package com.example.myfinalproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        imgProfile = findViewById(R.id.imgProfile)
        tvUsername = findViewById(R.id.tvUsername)
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.btnGantiAvatar).setOnClickListener {
            startActivity(Intent(this, AvatarActivity::class.java))
        }

        loadUserData()
        loadAvatar()
    }

    override fun onResume() {
        super.onResume()
        loadAvatar()
        loadUserData()
    }

    private fun loadUserData() {
        val user = auth.currentUser
        tvUsername.text = user?.displayName ?: user?.email ?: "User"
    }

    private fun loadAvatar() {
        progressBar.visibility = View.VISIBLE

        val user = auth.currentUser ?: return
        val uid = user.uid

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .child("avatar")

        dbRef.get().addOnSuccessListener {
            val avatarName = it.getValue(String::class.java)

            if (!avatarName.isNullOrEmpty()) {
                val resId = resources.getIdentifier(
                    avatarName,
                    "drawable",
                    packageName
                )

                Glide.with(this)
                    .load(resId)
                    .circleCrop()
                    .into(imgProfile)
            } else {
                imgProfile.setImageResource(R.drawable.avatar_default)
            }
            progressBar.visibility = View.GONE
        }
    }
}


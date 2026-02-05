package com.example.myfinalproject

import android.content.Context
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AvatarActivity : AppCompatActivity() {

    private val user = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseDatabase.getInstance().getReference("users")

    
    private val avatars = listOf(
        "boy",
        "gamer",
        "girl",
        "rabbit"
      
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatar)

        val gridAvatar = findViewById<GridLayout>(R.id.gridAvatar)
        loadAvatars(gridAvatar)
    }

    private fun loadAvatars(grid: GridLayout) {
        val context = this

        avatars.forEach { avatarName ->
            val imageView = ImageView(context)
            val resId = resources.getIdentifier(avatarName, "drawable", packageName)
            imageView.setImageResource(resId)
            imageView.contentDescription = avatarName

          
            imageView.layoutParams = GridLayout.LayoutParams().apply {
                width = 0 
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(16, 16, 16, 16)
            }

          
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.adjustViewBounds = true

            
            imageView.setOnClickListener { selectAvatar(avatarName) }

            
            grid.addView(imageView)
        }
    }

    private fun selectAvatar(avatarName: String) {
        val uid = user?.uid ?: return

        
        db.child(uid).child("photo").setValue(avatarName)
            .addOnSuccessListener {
                
                val sharedPref = getSharedPreferences("USER_DATA", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("user_avatar", avatarName)
                    apply()
                }

                Toast.makeText(this, "Avatar berhasil diubah", Toast.LENGTH_SHORT).show()
                finish() 
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengubah avatar", Toast.LENGTH_SHORT).show()
            }
    }
}

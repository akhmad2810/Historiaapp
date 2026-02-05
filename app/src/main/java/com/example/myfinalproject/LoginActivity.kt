package com.example.myfinalproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_GOOGLE = 100

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView
    private lateinit var googleBtn: SignInButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvForgotPassword: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        googleBtn = findViewById(R.id.googleBtn)
        progressBar = findViewById(R.id.progressBar)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)

        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    saveUserToLeaderboardIfNotExists()

                    launch(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "Login gagal: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        
        googleBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            startActivityForResult(googleSignInClient.signInIntent, RC_GOOGLE)
        }

        
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }


      
        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra("email", etEmail.text.toString().trim())
            startActivity(intent)
        }

    }

  
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
                    val credential =
                        GoogleAuthProvider.getCredential(account.idToken, null)

                    auth.signInWithCredential(credential).await()

                    saveGoogleUserToDatabase()
                    saveUserToLeaderboardIfNotExists()

                    launch(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    launch(Dispatchers.Main) {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@LoginActivity,
                            "Login Google gagal: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }


    private fun saveGoogleUserToDatabase() {
        val user = auth.currentUser ?: return

        val userData = mapOf(
            "username" to (user.displayName ?: "User"),
            "email" to (user.email ?: ""),
            "photo" to (user.photoUrl?.toString() ?: "")
        )

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(user.uid)
            .setValue(userData)
    }


    private fun saveUserToLeaderboardIfNotExists() {
        val user = auth.currentUser ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("leaderboard")
            .child(user.uid)

        ref.get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {

                val avatar = user.photoUrl?.toString()
                    ?: "https://ui-avatars.com/api/?name=${user.displayName ?: "User"}"

                val leaderboardData = mapOf(
                    "name" to (user.displayName ?: "User"),
                    "title" to "Pemula",
                    "score" to 0,
                    "avatarUrl" to avatar
                )

                ref.setValue(leaderboardData)
            }
        }
    }
}

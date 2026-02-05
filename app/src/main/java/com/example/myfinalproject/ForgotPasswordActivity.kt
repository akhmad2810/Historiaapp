package com.example.myfinalproject

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.myfinalproject.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnReset: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        btnReset = findViewById(R.id.btnReset)
        progressBar = findViewById(R.id.progressBar)

       
        val emailFromLogin = intent.getStringExtra("email")
        if (!emailFromLogin.isNullOrEmpty()) {
            etEmail.setText(emailFromLogin)
        }

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()

           
            if (email.isEmpty()) {
                etEmail.error = "Email wajib diisi"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Format email tidak valid"
                return@setOnClickListener
            }

            
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Reset Password")
                .setMessage("Link reset password akan dikirim ke:\n\n$email")
                .setPositiveButton("Kirim") { _, _ ->
                    sendResetEmail(email)
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }

    private fun sendResetEmail(email: String) {
        progressBar.visibility = View.VISIBLE
        btnReset.isEnabled = false

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                showSuccessDialog()
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                btnReset.isEnabled = true
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_success) 
            .setTitle("Berhasil")
            .setMessage(
                "Link reset password berhasil dikirim.\n\n" +
                        "Silakan cek email Anda."
            )
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }
}

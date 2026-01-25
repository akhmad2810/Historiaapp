package com.example.myfinalproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.myfinalproject.Fragment.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ===== AUTH CHECK =====
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // ===== GOOGLE SIGN IN =====
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

        // ===== TOOLBAR =====
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Peta"

        // ===== BOTTOM NAV =====
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // ===== DEFAULT FRAGMENT (QUIZ) =====
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_map
            openMap()
        }


        // ===== NAVIGATION HANDLER =====
        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_map -> {
                    openMap()
                    true
                }

                R.id.nav_quiz -> {
                    hideToolbar()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.containerFragment, QuizHomeFragment())
                        .commit()
                    invalidateOptionsMenu()
                    true
                }

                R.id.nav_leaderboard -> {
                    hideToolbar()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.containerFragment, LeaderboardFragment())
                        .commit()
                    invalidateOptionsMenu()
                    true
                }

                else -> false
            }
        }

    }

    private fun openMap() {
        showToolbar()
        supportActionBar?.title = "Peta"

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, MapFragment())
            .commit()

        invalidateOptionsMenu()
    }

    // ================= MENU TOOLBAR =================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isToolbarVisible = toolbar.visibility == View.VISIBLE
        menu?.findItem(R.id.action_profile)?.isVisible = isToolbarVisible
        menu?.findItem(R.id.action_logout)?.isVisible = isToolbarVisible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }

            R.id.action_logout -> {
                showLogoutDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // ================= TOOLBAR CONTROL =================
    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    // ================= LOGOUT =================
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->

                FirebaseAuth.getInstance().signOut()
                googleSignInClient.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}

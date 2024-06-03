package com.dqitech.authentication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dqitech.location.LocationActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSwitchToSignup: TextView
    private lateinit var tvSwitchUser: TextView

    private fun getUserAccounts(): List<String> {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userSet = sharedPreferences.getStringSet("userAccounts", setOf()) ?: setOf()
        return userSet.toList()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvSwitchToSignup = findViewById(R.id.tvSwitchToSignup)
        tvSwitchUser = findViewById(R.id.tvSwitchUser)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                saveUserAccount(email)  // Saved the logged-in user account
                val intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
            }
        }

        tvSwitchToSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        tvSwitchUser.setOnClickListener {
            showSwitchUserDialog()
        }
    }

    private fun showSwitchUserDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_switch_user, null)
        val lvUsers = dialogView.findViewById<ListView>(R.id.lvUsers)

        val users = getUserAccounts()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        lvUsers.adapter = adapter

        val builder = AlertDialog.Builder(this)
            .setTitle("Switch User")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()

        lvUsers.setOnItemClickListener { _, _, position, _ ->
            val selectedUser = users[position]
            etEmail.setText(selectedUser)
            dialog.dismiss()
        }
    }

    private fun saveUserAccount(email: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userSet = sharedPreferences.getStringSet("userAccounts", mutableSetOf())?.toMutableSet()
        userSet?.add(email)
        editor.putStringSet("userAccounts", userSet)
        editor.apply()
    }
}

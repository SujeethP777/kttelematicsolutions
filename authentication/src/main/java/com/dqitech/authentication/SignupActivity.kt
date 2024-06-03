package com.dqitech.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dqitech.location.LocationActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var etSignupEmail: EditText
    private lateinit var etSignupPhone: EditText
    private lateinit var etSignupPassword: EditText
    private lateinit var etSignupConfirmPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvSwitchToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        etSignupEmail = findViewById(R.id.etSignupEmail)
        etSignupPhone = findViewById(R.id.etSignupPhone)
        etSignupPassword = findViewById(R.id.etSignupPassword)
        etSignupConfirmPassword = findViewById(R.id.etSignupConfirmPassword)
        btnSignup = findViewById(R.id.btnSignup)
        tvSwitchToLogin = findViewById(R.id.tvSwitchToLogin)

        btnSignup.setOnClickListener {
            val email = etSignupEmail.text.toString().trim()
            val phone = etSignupPhone.text.toString().trim()
            val password = etSignupPassword.text.toString().trim()
            val confirmPassword = etSignupConfirmPassword.text.toString().trim()

            if (email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LocationActivity::class.java)
                startActivity(intent)
            }
        }

        tvSwitchToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

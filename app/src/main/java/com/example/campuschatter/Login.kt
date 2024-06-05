package com.example.campuschatter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private lateinit var mAuth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email,password)
        }


    }

    private fun login(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    if (user != null) {
                        if (user.isEmailVerified) {
                            // Email is verified
                            Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()
                            // Proceed to the main activity or dashboard
                            val intent = Intent(this@Login, MainActivity::class.java)

                            startActivity(intent)
                        } else {
                            // Email is not verified, show a message and possibly resend verification email
                            Toast.makeText(this, "Email is not verified. Please check your inbox.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // If login fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

}
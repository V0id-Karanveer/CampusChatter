package com.example.campuschatter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtName: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        edtName = findViewById(R.id.edit_name)
        edtEmail = findViewById(R.id.edit_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)
        mAuth = FirebaseAuth.getInstance()

        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            signUp(name,email,password)
        }

    }
    private fun signUp(name:String,email: String, password: String) {
        if (!email.endsWith("@learner.manipal.edu")) {
            Toast.makeText(this, "Please use a valid learner.manipal.edu email address.", Toast.LENGTH_LONG).show()
            return
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth.currentUser
                    Toast.makeText(this, "Registration successful.", Toast.LENGTH_SHORT).show()

                    // Optionally send verification email
                    user?.sendEmailVerification()?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Verification email sent to ${user.email}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val intent = Intent(this@SignUp,Login::class.java)
                    finish()
                    startActivity(intent)

                    // Redirect to login activity or main activity
                    // Intent to another activity here

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    private fun addUserToDatabase(name:String,email: String,uid:String){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email, uid))


    }

}
package com.example.campuschatter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var userRecylerView: RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth:FirebaseAuth
    private lateinit var logOut:Button
    private lateinit var mDbRef:DatabaseReference
    private lateinit var searchBar:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        searchBar = findViewById(R.id.searchBar)

        userRecylerView = findViewById(R.id.recyclerView)
        userRecylerView.layoutManager = LinearLayoutManager(this)
        userRecylerView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        logOut = findViewById(R.id.logoutButton)

        logOut.setOnClickListener {
            mAuth.signOut()
            finish()
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }
    private fun filterUsers(query: String) {
        val filteredList = ArrayList<User>()
        for (user in userList) {
            if (user.name?.toLowerCase()?.contains(query.toLowerCase()) == true) {
                filteredList.add(user)
            }
        }
        adapter.updateList(filteredList)
    }
}
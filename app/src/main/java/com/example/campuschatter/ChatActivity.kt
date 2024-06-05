package com.example.campuschatter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox:EditText
    private lateinit var sendButton: ImageView
    private lateinit var usernameBar:TextView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList1: ArrayList<com.example.campuschatter.Message>
    private lateinit var mDbRef: DatabaseReference
    var receiverRoom:String? = null
    var senderRoom:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid+senderUid
        receiverRoom = senderUid+receiverUid
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        usernameBar = findViewById(R.id.usernameBar)
        messageList1 = ArrayList()
        messageAdapter = MessageAdapter(this, messageList1)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = messageAdapter
        usernameBar.setText(name)

        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList1.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(com.example.campuschatter.Message::class.java)
                    messageList1.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        sendButton.setOnClickListener{
            val message = messageBox.text.toString()
            val messageObject = com.example.campuschatter.Message(message,senderUid)
            mDbRef.child("chats").child(senderRoom!!).child("messages").push().setValue(messageObject).addOnSuccessListener {
                mDbRef.child("chats").child(receiverRoom!!).child("messages").push().setValue(messageObject)
            }
            messageBox.setText("")

        }
    }
}
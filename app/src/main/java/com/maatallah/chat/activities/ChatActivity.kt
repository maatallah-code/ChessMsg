package com.maatallah.chat.activities

/* import android.os.Message */
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maatallah.chat.R
import com.maatallah.chat.adapters.ChatRecyclerAdapter
import com.maatallah.chat.models.Message
import com.maatallah.chat.models.User

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var currentUser: FirebaseUser?=null

    lateinit var fabSendMessage: FloatingActionButton
    lateinit var editMessage:EditText
    lateinit var rvChatList :RecyclerView
    lateinit var chatRecyclerAdapter:ChatRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = Firebase.auth
        db = Firebase.firestore
        currentUser = auth.currentUser

        fabSendMessage = findViewById(R.id.fabSendMessage)
        editMessage = findViewById(R.id.editMessage)
        rvChatList = findViewById(R.id.rvChatList)

        val userUuid = intent.getStringExtra("friend")!!

        db.collection("users")
            .document(userUuid)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    var user = result.toObject(User::class.java)
                    user?.let {
                        user.uuid = userUuid
                        setUserData(user)
                    }
                }

            }.addOnFailureListener {
                Log.e("ChatActivity","error getting user",it)
            }


    }

    private fun setUserData(user: User) {


        supportActionBar?.title=user.fullname

        chatRecyclerAdapter = ChatRecyclerAdapter()

        val messages= mutableListOf<Message>()

        rvChatList.apply {
            layoutManager=LinearLayoutManager(this@ChatActivity)
            adapter=chatRecyclerAdapter
        }

        fabSendMessage.setOnClickListener {

            val message =editMessage.text.toString()
            if (message.isNotEmpty()){
                val message = Message(
                    sender = currentUser!!.uid,
                receiver = user.uuid,
                    text = message,
                    timestamp = System.currentTimeMillis(),
                    isReceived = false
                )


                editMessage.setText("")

                val inpuMethodeManager = getSystemService(Context.INPUT_METHOD_SERVICE)as InputMethodManager
                inpuMethodeManager.hideSoftInputFromWindow(editMessage.windowToken,0)

                db.collection("messages").add(message)
                    .addOnSuccessListener {
                                    rvChatList.scrollToPosition(messages.size-1)
                    }.addOnFailureListener {
                        Log.e("ChatActivity","error adding message",it)
                    }
            }

        }






       // chatRecyclerAdapter.items = messages


        val sentQuery = db.collection("messages")
            .whereEqualTo("Sender",currentUser!!.uid)
            .whereEqualTo("receiver",user.uuid)
            .orderBy("timestamp",Query.Direction.ASCENDING)


        val receivedQuery = db.collection("messages")
            .whereEqualTo("Sender",user.uuid)
            .whereEqualTo("receiver",currentUser!!.uid)
            .orderBy("timestamp",Query.Direction.ASCENDING)


        sentQuery.addSnapshotListener{snapshot, exception ->
            if (exception != null){
                Log.e("ChatActivity","error getting messages",exception)
                return@addSnapshotListener
            }
            for (document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.e("ChatActivity sent:",message.toString())
                    message.isReceived=false
                    if (!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }

            if (messages.isNotEmpty()){
                chatRecyclerAdapter.items =messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }

        receivedQuery.addSnapshotListener{snapshot, exception ->
            if (exception != null){
                Log.e("ChatActivity","error getting messages",exception)
                return@addSnapshotListener
            }
            for (document in snapshot!!.documents){
                var message = document.toObject(Message::class.java)
                message?.let {
                    Log.e("ChatActivity received:",message.toString())
                    message.isReceived=true
                    if (!messages.contains(message)){
                        messages.add(message)
                    }
                }
            }

            if (messages.isNotEmpty()){
                chatRecyclerAdapter.items =messages.sortedBy { it.timestamp } as MutableList<Message>
                rvChatList.scrollToPosition(messages.size-1)
            }
        }



    }

    }






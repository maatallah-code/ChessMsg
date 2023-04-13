package com.maatallah.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maatallah.chat.R
import com.maatallah.chat.adapters.FriendsRecyclerAdapter
import com.maatallah.chat.models.Friend

class HomeActivity : AppCompatActivity() {


    lateinit var rvFriends: RecyclerView
    lateinit var fabChat: FloatingActionButton
    lateinit var friendsRecyclerAdapter:FriendsRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        rvFriends=findViewById(R.id.rvFreinds)
        fabChat=findViewById(R.id.fabChat)



        fabChat.setOnClickListener {

            Intent(this,UsersSearchActivity::class.java).also {
                startActivity(it)
            }

        }



        val friends = mutableListOf(
            Friend("abdelakder","Salut", "", 124578) ,
            Friend("mohamed","Salut", "", 124578),
            Friend("ali","Salut", "", 124578)

        )
        friendsRecyclerAdapter= FriendsRecyclerAdapter()
        friendsRecyclerAdapter.items = friends
        rvFriends.apply {
            layoutManager=LinearLayoutManager(this@HomeActivity)

        adapter=friendsRecyclerAdapter
    }}


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId==R.id.itemSettings){
            Toast.makeText(this,"Settings clicked!",Toast.LENGTH_LONG).show()
            Intent(this,SettingsActivity::class.java).also {
                startActivity(it)
            }

        }
            if (item.itemId==R.id.itemLogout){
               val auth = Firebase.auth
                auth.signOut()

                Intent(this,AuthentificationActivity::class.java).also {
                    startActivity(it)
                }
                finish()
            }
        return super.onOptionsItemSelected(item)
    }

}
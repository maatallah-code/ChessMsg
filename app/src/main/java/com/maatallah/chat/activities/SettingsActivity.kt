package com.maatallah.chat.activities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

import com.maatallah.chat.R
import com.maatallah.chat.models.User
import java.io.ByteArrayOutputStream
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var db:FirebaseFirestore

    private var currentUser:FirebaseUser?=null
   private lateinit var ivUser: ShapeableImageView
   private lateinit var layoutTextInputEmail:TextInputLayout
   private lateinit var layoutTextInputName:TextInputLayout
   lateinit var btnsave: MaterialButton
   var isImageChanged =false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        auth = Firebase.auth
        db=Firebase.firestore
        currentUser=auth.currentUser


        ivUser= findViewById(R.id.ivUser)
        layoutTextInputEmail = findViewById(R.id.layoutTextInputEmail)
        layoutTextInputName=findViewById(R.id.layoutTextInputName)
        btnsave=findViewById(R.id.btn_save)


        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){
            it?.let {
                Glide.with(this).load(it).placeholder(R.drawable.avatar).into(ivUser)
                isImageChanged=true
            }

        }

        ivUser.setOnClickListener {
            pickImage.launch("image/*")
        }

        if (currentUser!=null){
            db.collection("users").document(currentUser!!.uid).get().addOnSuccessListener { result ->
                if (result !=null){
                    var user = result.toObject(User::class.java)
                   user?.let {
                       user.uuid = currentUser!!.uid
                      setUserData(user)
                   }
                }
            }

        }else{
            Log.d("SettingsActivity", "No user found ")
        }






    }

    private fun setUserData(user: User) {
        layoutTextInputEmail.editText?.setText(user.email)
        layoutTextInputName.editText?.setText(user.fullname)
        //init image

        user.image?.let {
            Glide.with(this).load(it).placeholder(R.drawable.avatar).into(ivUser)
        }


        btnsave.setOnClickListener {
            layoutTextInputEmail.isErrorEnabled=false
            if (isImageChanged){
                uploadImageToFirebaseStorage(user)

            }else if (layoutTextInputName.editText?.text.toString() !=user.fullname){




                updateUserInfo(user)
            }else{
                Toast.makeText(this,"Your information are up to date",Toast.LENGTH_LONG).show()
                layoutTextInputName.clearFocus()
            }

        }

    }

    private fun uploadImageToFirebaseStorage(user: User) {

        var storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${user.uuid}")

        val bitmap = (ivUser.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val  data =baos.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->

                user.image = uri.toString()
                updateUserInfo(user)
            }
        }

    }

    private fun updateUserInfo(user: User) {

        var updateUser = hashMapOf<String,Any>(

            "fullname" to layoutTextInputName.editText?.text.toString() ,
            "image" to (user.image?:"")
        )

        db.collection("users").document(user.uuid).update(updateUser)
            .addOnSuccessListener {
                Toast.makeText(this,"Your information are up to date",Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                layoutTextInputName.error="Error occurred please try again later.."
                layoutTextInputEmail.isErrorEnabled=true
            }

    }
}
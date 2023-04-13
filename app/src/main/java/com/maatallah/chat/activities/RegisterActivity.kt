package com.maatallah.chat.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maatallah.chat.R

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    lateinit var layoutTextInputName: TextInputLayout
    lateinit var layoutTextInputEmail: TextInputLayout
    lateinit var layoutTextInputPassword: TextInputLayout
    lateinit var layoutTextInputConfirmPassword: TextInputLayout
    lateinit var btnRegister: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth= Firebase.auth

        layoutTextInputName=findViewById(R.id.layoutTextInputName)
        layoutTextInputEmail=findViewById(R.id.layoutTextInputEmail)
        layoutTextInputPassword=findViewById(R.id.layoutTextInputPassword)
        layoutTextInputConfirmPassword=findViewById(R.id.layoutTextInputConfirmPassword)
        btnRegister=findViewById(R.id.btnRegister)


        btnRegister.setOnClickListener {
            initErrors()

            val email = layoutTextInputEmail.editText?.text.toString()
            val name = layoutTextInputName.editText?.text.toString()
            val password = layoutTextInputPassword.editText?.text.toString()
            val confirmPassword = layoutTextInputConfirmPassword.editText?.text.toString()

            if (email.isEmpty()||name.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
                if (password.isEmpty()){
                    layoutTextInputPassword.error="Password is required!!"
                    layoutTextInputPassword.isErrorEnabled=true
                }
                if (confirmPassword.isEmpty()){
                    layoutTextInputConfirmPassword.error="Confirm Password is required!!"
                    layoutTextInputConfirmPassword.isErrorEnabled=true
                }
                if (email.isEmpty()){
                    layoutTextInputPassword.error="Email is required!!"
                    layoutTextInputPassword.isErrorEnabled=true
                }
                if (name.isEmpty()){
                    layoutTextInputPassword.error="Name is required!!"
                    layoutTextInputPassword.isErrorEnabled=true
                }


            }else{
                if (password!=confirmPassword){
                    layoutTextInputConfirmPassword.error="Passwords did not match"
                    layoutTextInputConfirmPassword.isErrorEnabled=true
                }else{
                    //auth.currentUser
                    // creation d'un utilistaeur dans le module authentification de firbase
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task ->
                        if (task.isSuccessful){

                            val user = hashMapOf(
                                "fullname" to name,
                                "email" to email,
                            )

                            val currentUser=auth.currentUser
                            // creation de l'utilisateur dans le module firestore
                            val db =Firebase.firestore
                            db.collection("users").document(currentUser!!.uid) .set(user).addOnCompleteListener {
                                Intent(this, HomeActivity::class.java).also {
                                    startActivity(it)
                                }
                            }.addOnFailureListener {
                                layoutTextInputConfirmPassword.error=" Error occurred please try again later"
                                layoutTextInputConfirmPassword.isErrorEnabled=true
                            }


                        }else{
                            layoutTextInputConfirmPassword.error=" Error occurred please try again later"
                            layoutTextInputConfirmPassword.isErrorEnabled=true
                        }


                    }
                }
            }

        }
    }

    private fun initErrors() {
        layoutTextInputEmail.isErrorEnabled=false
        layoutTextInputName.isErrorEnabled=false
        layoutTextInputPassword.isErrorEnabled=false
        layoutTextInputConfirmPassword.isErrorEnabled=false
    }
}
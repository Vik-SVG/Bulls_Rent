package com.lessons.bullsrent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lessons.bullsrent.databinding.ActivityUserProfileBinding


class ActivityUserProfile : AppCompatActivity() {
    private lateinit var bin: ActivityUserProfileBinding

    private lateinit var referenceDB:DatabaseReference
    private lateinit var mName:String
    private lateinit var mUsername:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = bin.root
        setContentView(view)

        referenceDB = FirebaseDatabase.getInstance().getReference("Users")

        showAllUserData()

    }

    private fun showAllUserData() {
        val intent = intent
        mName = intent.getStringExtra("name").toString()
        mUsername = intent.getStringExtra("username").toString()
        val mEmail = intent.getStringExtra("email")
        val mPassword = intent.getStringExtra("password")
        val mPhoneNumber = intent.getStringExtra("phoneNumber")

        bin.usernameUserProfile.text = mUsername
        bin.full1NameUserProfile.text = mName
        bin.emailProfile.editText?.setText(mEmail)
        bin.usernameProfile.editText?.setText(mName)
        bin.passwordProfile.editText?.setText(mPassword)
        bin.phoneNumberProfile.editText?.setText(mPhoneNumber)

        bin.updateBtn.setOnClickListener { updateProfile() }

    }

    fun updateProfile(){
        if (isNameChanged() or isPasswordChanged()){
            Toast.makeText(this, "Data has been update", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "Data is the same", Toast.LENGTH_SHORT).show()
        }

    }

    private fun isPasswordChanged(): Boolean {

        return true
    }

    private fun isNameChanged(): Boolean {
        if (!mName.equals(bin.usernameProfile.editText?.text.toString())){
            mName = bin.usernameProfile.editText?.text.toString()
            referenceDB.child(mUsername).child("name").setValue(bin.usernameProfile.editText?.text.toString())
            return true
        } else{
            return false
        }
    }


}
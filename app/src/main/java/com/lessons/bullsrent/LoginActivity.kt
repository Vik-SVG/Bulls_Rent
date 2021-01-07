package com.lessons.bullsrent

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.WindowManager
import com.google.firebase.database.*
import com.lessons.bullsrent.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

       /* with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                enterTransition = Explode()
                exitTransition = Explode()
            }
        }*/

        binding.signUpLoginActivityBtn.setOnClickListener { setTransitionForSignUpActivity() }

        setContentView(view)

        binding.goBtnLoginActivity.setOnClickListener { loginUser() }


    }

    override fun onBackPressed() {
        finish()
    }


    private fun setTransitionForSignUpActivity(){

        val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
        // startActivity(intent)

        val pair1: Pair<View, String> = Pair(binding.logoImageSecond, getString(R.string.logo_transition_name))
        val pair2: Pair<View, String> = Pair(binding.helloText, getString(R.string.text_transition_name))
        val pair3:Pair<View, String> = Pair(binding.signText, getString(R.string.description_text_transition))

        val pair4: Pair<View, String> = Pair(binding.usernameEditText, getString(R.string.user_name_transition))
        val pair5: Pair<View, String> = Pair(binding.passwordEditText,getString(R.string.password_transition))
        val pair6: Pair<View, String> = Pair(binding.goBtnLoginActivity, getString(R.string.go_button_transition))
        val pair7: Pair<View, String> = Pair(binding.signUpLoginActivityBtn, getString(R.string.login_sign_up_transition))



        val options =  ActivityOptions.makeSceneTransitionAnimation(this@LoginActivity,
                pair1, pair2, pair3, pair4, pair5, pair6, pair7,
        )

        startActivity(intent, options.toBundle())

    }

    private fun validateUserName():Boolean{
        val userNameValue = binding.usernameEditText.text.toString()

        return if(userNameValue.isEmpty()){
            binding.usernameEditText.error = "Field cannot be empty"
            false
        } else{
            binding.usernameEditText.error = null
            binding.usernameInputLoginActivity.isErrorEnabled = false
            true
        }

    }

    private fun validatePassword():Boolean{
        val passwordValue = binding.passwordEditText.text.toString()

        return if(passwordValue.isEmpty()){
            binding.passwordEditText.error = "Field cannot be empty"
            false
        } else{
            binding.passwordEditText.error = null
            binding.passwordInputLoginActivity.isErrorEnabled = false
            true
        }
    }

    private fun loginUser(){
        if(!validatePassword() or !validateUserName()){
            return
        } else{
            isUserExist()
        }

    }

    private fun isUserExist() {
        val usernameInput = binding.usernameEditText.text.toString().trim()
        val passwordInput = binding.passwordEditText.text.toString().trim()
        firebaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val checkUser:Query = firebaseReference.orderByChild("username").equalTo(usernameInput)

        checkUser.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){

                    binding.usernameEditText.error = null
                    binding.usernameInputLoginActivity.isErrorEnabled = false

                    val passwordFormDB:String? = snapshot.child(usernameInput).child("password").getValue(String::class.java)

                    if (passwordFormDB.equals(passwordInput)){

                        val nameFromDB:String? = snapshot.child(usernameInput).child("name").getValue(String::class.java)
                        val usernameFromDB:String? = snapshot.child(usernameInput).child("username").getValue(String::class.java)
                        val phoneNumberFromDB:String? = snapshot.child(usernameInput).child("phoneNumber").getValue(String::class.java)
                        val passwordFromDB:String? = snapshot.child(usernameInput).child("password").getValue(String::class.java)
                        val emailFromDB:String? = snapshot.child(usernameInput).child("email").getValue(String::class.java)

                        val intent = Intent(applicationContext, ActivityUserProfile::class.java)
                        intent.putExtra("name", nameFromDB)
                        intent.putExtra("password", passwordFormDB)
                        intent.putExtra("username", usernameFromDB)
                        intent.putExtra("phoneNumber", phoneNumberFromDB)
                        intent.putExtra("email", emailFromDB)
                        startActivity(intent)
                    } else{
                        binding.passwordEditText.error = "Wrong password"
                        binding.passwordEditText.requestFocus()

                    }
                } else{
                    binding.usernameEditText.error = "No such user"
                    binding.usernameEditText.requestFocus()

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}
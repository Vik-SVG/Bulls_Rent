package com.lessons.bullsrent

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.firebase.database.*
import com.lessons.bullsrent.databinding.ActivitySignUpBinding
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    val MAX_USERNAME_CHARACTERS = 14

    var lastChar = " "

    lateinit var mRootNode: FirebaseDatabase
    lateinit var mDBReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
       /* with(window){
            requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                enterTransition = Explode()
                exitTransition = Explode()
            }
        }*/
        setContentView(view)

        //maskPhoneNumber()

       // setMaxUserNameCharacters(MAX_USERNAME_CHARACTERS)

        val dbReference = FirebaseDatabase.getInstance().getReference("Users")


        binding.usernameSignUp.editText?.addTextChangedListener(object: TextWatcher{  //TODO check asynck?
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkIfUsernameAlreadyExist(object : CallbackForDatabaseListener {
                    override fun onCallback(userExist: Boolean) {
                        if (userExist) {
                            binding.usernameSignUp.error = "Username already exist"
                            binding.goBtnSignUp.isClickable = false
                        } else {
                            binding.usernameSignUp.error =""
                            binding.goBtnSignUp.isClickable = true
                        }
                    }
                }, p0.toString(), dbReference)
            }

            override fun afterTextChanged(p0: Editable?) {
                runBlocking { delay(100) }
            }
        })

        binding.goBtnSignUp.setOnClickListener {

            /*validateEmail()
            validateName()
            validatePassword()
            validateUserName()*/

            if(!validateName() or !validateEmail() or !validatePassword() or !validateUserName()){
                return@setOnClickListener
            }

            val phone = binding.phoneNumberSignUp.editText!!.text.toString()
            val username = binding.usernameSignUp.editText?.text.toString()

            val intent1 = Intent(applicationContext, VerificationActivity::class.java)
            intent1.putExtra("phoneNo", phone)
            startActivity(intent1)


          /*  val user = UserHelper(
                        binding.nameSignUp.editText?.text.toString(),
                        username,
                        binding.emailSignUp.editText?.text.toString(),
                        phone,
                        binding.passwordSignUp.editText?.text.toString()
                )
                mRootNode =
                        FirebaseDatabase.getInstance("https://bull-s-rent-dcb64-default-rtdb.firebaseio.com/")
                mDBReference = mRootNode.getReference("Users")
                mDBReference.child(username).setValue(user)

            val intent2 = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()*/

        }


    }

    private fun setMaxUserNameCharacters(maxNum: Int) {

        binding.usernameSignUpEditText.addTextChangedListener{

            val editEntryView = binding.usernameSignUpEditText
            val filterArray = arrayOfNulls<InputFilter>(1)

            if (editEntryView.length()>maxNum) {
                binding.usernameSignUp.error = "You've reach max characters."
                filterArray[0] = InputFilter.LengthFilter(maxNum)
                editEntryView.filters = filterArray

            } else if (editEntryView.length()<=maxNum){
                binding.usernameSignUp.error = ""

                filterArray[0] = InputFilter.LengthFilter(maxNum+1)
                editEntryView.filters = filterArray

            }

        }

    }

    private fun validateName():Boolean{
       val nameValue = binding.nameSignUp.editText?.text.toString()
        return if(nameValue.isEmpty()){
            binding.nameSignUp.error = "Field cannot be empty"
            false
        } else{
            binding.nameSignUp.error = null
            binding.nameSignUp.isErrorEnabled = false
            true
        }

    }

    private fun validateUserName():Boolean{
        val userNameValue = binding.usernameSignUp.editText?.text.toString()
        val noWhiteSpaces = Regex(pattern = "\\A\\w{4,20}\\z")

        return if(userNameValue.isEmpty()){
            binding.usernameSignUp.error = "Field cannot be empty"
            false
        } else if(userNameValue.length>15){
            binding.usernameSignUp.error = "Username is too long"
            false
        } else if(!userNameValue.matches(noWhiteSpaces)){
            binding.usernameSignUp.error = "Spaces isn't allowed"
            false
        }  else{
            binding.usernameSignUp.error = null
            true
        }

    }

    private fun validateEmail():Boolean{
        val mailValue = binding.emailSignUp.editText?.text.toString()
        val emailPattern = Regex(pattern = "[a-zA-z0-9._-]+@[a-z]+\\.+[a-z]+")

        return if(mailValue.isEmpty()){
            binding.emailSignUp.error = "Field cannot be empty"
            false
        } else if(!mailValue.matches(emailPattern)){
            binding.emailSignUp.error = "Invalid e-mail"
            false
        }
        else{
            binding.emailSignUp.error = null
            true
        }

    }

    fun validatePhoneNumber():Boolean{

        return true
    }

    private fun validatePassword():Boolean{
        val passwordValue = binding.passwordSignUp.editText?.text.toString()
        val passwordPattern = Regex(pattern = "^"+
                "(?=.*[a-zA-Z])"+
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)"+
                ".{4,}"+
                "$")

        return if(passwordValue.isEmpty()){
            binding.passwordSignUp.error = "Field cannot be empty"
            false
        } else if(!passwordValue.matches(passwordPattern)){
            binding.passwordSignUp.error = "Invalid password! Add at least 4 letters and one special character."
            false
        }
        else{
            binding.passwordSignUp.error = null
            true
        }

    }

    private fun maskPhoneNumber(){
        val editPhoneNum = binding.phoneNumberEditText

        editPhoneNum.doBeforeTextChanged { text, start, count, after ->
            val digits: Int = editPhoneNum.getText().toString().length
            if (digits > 1) lastChar = editPhoneNum.getText().toString().substring(digits - 1)
        }

        editPhoneNum.doOnTextChanged { text, start, before, count ->
            val digits: Int = editPhoneNum.getText().toString().length
            Log.d("LENGTH", "" + digits)
            if (lastChar != "-") {
                if (digits == 3 || digits == 7) {
                    editPhoneNum.append("-")
                }
            }
        }
    }

    private fun checkIfUsernameAlreadyExist(callback:CallbackForDatabaseListener, userNameValue:String, dbReference:DatabaseReference){

        val checkUser: Query = dbReference.orderByChild("username").equalTo(userNameValue)

        val postListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    callback.onCallback(true)
                } else{
                    callback.onCallback(false)
                }

            }

            override fun onCancelled(error: DatabaseError) {}
        }
        checkUser.addValueEventListener(postListener)
    }


     interface CallbackForDatabaseListener{
      fun onCallback(userExist:Boolean)
    }

}
package com.lessons.bullsrent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.lessons.bullsrent.databinding.ActivityVerificationBinding
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationBinding

    private lateinit var verificationCodeBySystem: String
    private var phoneNo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.progressBar.visibility = View.GONE

        phoneNo = intent.getStringExtra("phoneNo")

        sendVerificationCodeToUser(phoneNo)


        binding.verifyBtn.setOnClickListener {

            val codeSMS = binding.verificationCodeEnteredByUser.text.toString()
           if (codeSMS.isEmpty() or (codeSMS.length<6)){
               binding.verificationCodeEnteredByUser.error = "Wrong OTP..."
               binding.verificationCodeEnteredByUser.requestFocus()
               return@setOnClickListener
           } else{
               binding.progressBar.visibility = View.VISIBLE
               verifyCode(codeSMS)
           }
        }
    }

    private fun sendVerificationCodeToUser(phoneNo: String?) {

        val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)

                Toast.makeText(this@VerificationActivity, "Got sent $p0", Toast.LENGTH_SHORT).show();

                verificationCodeBySystem = p0
            }

            override fun onVerificationCompleted(cred: PhoneAuthCredential) {


                Toast.makeText(this@VerificationActivity, "Got ${cred.smsCode}", Toast.LENGTH_SHORT).show();

                val code = cred.smsCode

                if (code!=null){
                    binding.progressBar.visibility = View.VISIBLE
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@VerificationActivity, p0.message, Toast.LENGTH_SHORT).show();
            }
        }


      /*  PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+38"+phoneNo!!,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks)*/

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber("+38$phoneNo")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)


    }

    private fun verifyCode(code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationCodeBySystem!!, code)
        signInTheUserByCredential(credential)
    }

    private fun signInTheUserByCredential(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this@VerificationActivity) { p0 ->
            if (p0.isSuccessful) {
                Toast.makeText(this@VerificationActivity, "Your Account has been created successfully!", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@VerificationActivity, p0.exception?.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}
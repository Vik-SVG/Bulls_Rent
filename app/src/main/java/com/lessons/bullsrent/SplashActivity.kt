package com.lessons.bullsrent

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Pair
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.lessons.bullsrent.databinding.ActivitySplashscreenBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashscreenBinding

    lateinit var topAnimation: Animation
    lateinit var bottomAnimation: Animation
    lateinit var sunAnimation: Animation




    private lateinit var options:ActivityOptions

    private val SPLASH_SCREEN:Long = 4000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashscreenBinding.inflate(layoutInflater)
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

        setAnimation()


       GlobalScope.launch {

            delay(SPLASH_SCREEN)
            val intent = Intent(this@SplashActivity, LoginActivity::class.java)

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
               startActivity(intent, options.toBundle())
                supportFinishAfterTransition();
            }
        }


    }

    private fun setAnimation() {
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        sunAnimation = AnimationUtils.loadAnimation(this, R.anim.sun_anim)

       binding.logoImageFirst.animation = topAnimation
        binding.nationwideText.animation = bottomAnimation
        binding.logoText.animation = bottomAnimation
        binding.sunImg.animation = sunAnimation



            val pair1:Pair<View, String> = Pair(binding.logoImageFirst, getString(R.string.logo_transition_name))
            val pair2:Pair<View, String> = Pair(binding.logoText, getString(R.string.text_transition_name))
            val pair3:Pair<View, String> = Pair(binding.nationwideText, getString(R.string.description_text_transition))


            options =  ActivityOptions.makeSceneTransitionAnimation(this@SplashActivity, pair1, pair2, pair3)

    }
}

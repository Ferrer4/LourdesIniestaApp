package com.lourdesiniesta.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.lourdesiniesta.app.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animarEntrada()
    }

    private fun animarEntrada() {
        // Logo sube desde abajo con fade in
        binding.layoutLogo.translationY = 80f
        binding.layoutLogo.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Barra inferior aparece con retraso
        binding.layoutBottom.animate()
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(600)
            .withEndAction { navegarSiguiente() }
            .start()
    }

    private fun navegarSiguiente() {
        // Espera 1.5 s más antes de saltar
        binding.root.postDelayed({
            val destino = if (FirebaseAuth.getInstance().currentUser != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(destino)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 1500)
    }
}

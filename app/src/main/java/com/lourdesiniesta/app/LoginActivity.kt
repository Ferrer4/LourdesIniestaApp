package com.lourdesiniesta.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lourdesiniesta.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Si ya hay sesión, ir directo a MainActivity
        if (auth.currentUser != null) {
            goToMain()
            return
        }

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.btnRegistro.setOnClickListener { doRegister() }
    }

    private fun doLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateFields(email, password)) return

        setLoading(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setLoading(false)
                goToMain()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Error al iniciar sesión: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun doRegister() {
        val nombre = binding.etNombre.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (nombre.isEmpty()) {
            binding.tilNombre.error = "Introduce tu nombre"
            return
        }
        binding.tilNombre.error = null
        if (!validateFields(email, password)) return

        setLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user!!.uid
                val userData = hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "telefono" to "",
                    "rol" to "cliente"
                )
                db.collection("usuarios").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        setLoading(false)
                        goToMain()
                    }
                    .addOnFailureListener { e ->
                        setLoading(false)
                        Toast.makeText(this, "Error al guardar datos: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Error al registrarse: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun validateFields(email: String, password: String): Boolean {
        var ok = true
        if (email.isEmpty()) {
            binding.tilEmail.error = "Introduce tu email"
            ok = false
        } else {
            binding.tilEmail.error = null
        }
        if (password.length < 6) {
            binding.tilPassword.error = "Mínimo 6 caracteres"
            ok = false
        } else {
            binding.tilPassword.error = null
        }
        return ok
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !loading
        binding.btnRegistro.isEnabled = !loading
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

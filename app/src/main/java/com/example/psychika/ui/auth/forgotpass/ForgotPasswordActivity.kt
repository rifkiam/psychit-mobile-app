package com.example.psychika.ui.auth.forgotpass

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.psychika.R
import com.example.psychika.databinding.ActivityForgotPasswordBinding
import com.example.psychika.ui.auth.login.LoginActivity

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvLogin.setOnClickListener {
                val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btnSend.setOnClickListener {
                send()
            }
        }
    }

    private fun send() {
        val etEmail = binding.etForgotPassEmail.text

        if (etEmail!!.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)

            showToast(getString(R.string.confirm_send, etEmail.toString()))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
    }
}
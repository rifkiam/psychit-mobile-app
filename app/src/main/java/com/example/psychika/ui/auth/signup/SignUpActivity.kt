package com.example.psychika.ui.auth.signup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.psychika.R
import com.example.psychika.data.network.Result
import com.example.psychika.databinding.ActivitySignUpBinding
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.ui.auth.login.LoginActivity
import com.example.psychika.utils.Utils

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvLogin.setOnClickListener {
                val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            btnSignup.setOnClickListener {
                signUp()
            }
        }
    }

    private fun signUp() {
        val etFirstName = binding.etSignupFirstName.text
        val etLastName = binding.etSignupLastName.text
        val etEmail = binding.etSignupEmail.text
        val etPassword = binding.etSignupPassword.text
        val etConfirmPass = binding.etConfirmPassword.text

        if (etFirstName!!.isEmpty() || etLastName!!.isEmpty() || etEmail!!.isEmpty() || etPassword!!.isEmpty() || etConfirmPass!!.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (!Utils.isValidEmail(etEmail) || etPassword.length < 8 || etConfirmPass.length < 8) {
            showToast(getString(R.string.invalid_form))
        } else if (etConfirmPass.toString() != etPassword.toString()) {
            showToast(getString(R.string.pass_not_match))
        } else {
            viewModel.register(
                etFirstName.toString(),
                etLastName.toString(),
                etEmail.toString(),
                etPassword.toString(),
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            if (result.error.message == "[email must be unique]") {
                                showToast(getString(R.string.email_registered))
                            }
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            showToast(getString(R.string.try_login))

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
    }
}
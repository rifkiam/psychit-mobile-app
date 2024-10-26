package com.example.psychika.ui.auth.login

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.psychika.R
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.databinding.ActivityLoginBinding
import com.example.psychika.ui.MainActivity
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.ui.auth.forgotpass.ForgotPasswordActivity
import com.example.psychika.ui.auth.signup.SignUpActivity
import com.example.psychika.utils.Utils
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var doubleBack = false
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userModel: User = User()
    private lateinit var userPreference: UserPreference

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.apply {
            tvForgotPass.setOnClickListener {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
            tvSignup.setOnClickListener {
                val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
            layoutRemember.setOnClickListener {
                cbRemember.isChecked = !cbRemember.isChecked
            }
            btnLogin.setOnClickListener { login() }
            btnGoogle.setOnClickListener { loginWithGoogle() }
        }
    }

    override fun onBackPressed() {
        if (doubleBack) {
            finishAffinity()
            super.onBackPressed()
            return
        }

        doubleBack = true
        Toast.makeText(this@LoginActivity, R.string.press_back_again, Toast.LENGTH_SHORT).show()

        handler.postDelayed({
            doubleBack = false
        }, 2000)
    }

    private fun login() {
        val etLoginEmail = binding.etLoginEmail.text
        val etLoginPassword = binding.etLoginPassword.text

        if (etLoginEmail!!.isEmpty() || etLoginPassword!!.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (!Utils.isValidEmail(etLoginEmail.toString()) || etLoginPassword.length < 8) {
            showToast(getString(R.string.invalid_form))
        } else {
            viewModel.login(
                etLoginEmail.toString(),
                etLoginPassword.toString()
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val response = result.data

                            userModel.id = response.token

                            if (binding.cbRemember.isChecked) {
                                userModel.rememberMe = true
                            }

                            userPreference.setUser(userModel)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE

                            if (result.error.message == "There is no user with this email address!") {
                                showToast(getString(R.string.no_user))
                            }
                            if (result.error.message == "Incorrect password!") {
                                showToast(getString(R.string.invalid_input_user))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loginWithGoogle() {
        binding.progressBar.visibility = View.VISIBLE
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                binding.progressBar.visibility = View.GONE
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        viewModel.loginWithGoogle(idToken).observe(this) { result ->
                            if (result != null) {
                                when (result) {
                                    is Result.Loading -> {
                                        binding.progressBar.visibility = View.VISIBLE
                                    }
                                    is Result.Success -> {
                                        val userGoogleAuth = result.data
                                        userModel.id = userGoogleAuth.id ?: ""
                                        userModel.rememberMe = true
                                        userModel.googleAuth = true
                                        userPreference.setUser(userModel)

                                        binding.progressBar.visibility = View.GONE
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    is Result.Error -> {
                                        binding.progressBar.visibility = View.GONE
                                        val errorMessage = result.error.message
                                        showToast(errorMessage)
                                    }
                                }
                            }
                        }
                    } catch (e: GoogleIdTokenParsingException) {
                        binding.progressBar.visibility = View.GONE
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                binding.progressBar.visibility = View.GONE
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}
package com.example.psychika.ui.profile.displayprofile

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.psychika.R
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.data.network.firebase.UserGoogleAuth
import com.example.psychika.data.network.response.UserResponse
import com.example.psychika.databinding.FragmentProfileBinding
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.ui.auth.login.LoginActivity
import com.example.psychika.ui.history.HistoryActivity
import com.example.psychika.ui.maps.MapsActivity
import com.example.psychika.ui.profile.changepass.ChangePasswordActivity
import com.example.psychika.ui.profile.editprofile.EditProfileActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private var userModel: User = User()
    private lateinit var userPreference: UserPreference

    private lateinit var userApi: UserResponse
    private lateinit var auth: FirebaseAuth
    private lateinit var userGoogleAuth: UserGoogleAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        userPreference = UserPreference(requireContext())
        userModel = userPreference.getUser()

        if (!userModel.googleAuth) {
            getCurrentUserApi()
        } else {
            binding.btnChangePass.visibility = View.GONE
            getCurrentUserGoogleAuth()
        }

        binding.apply {
            btnMaps.setOnClickListener {
                val intent = Intent(requireContext(), MapsActivity::class.java)
                startActivity(intent)
            }
            btnHistory.setOnClickListener {
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                startActivity(intent)
            }
            btnEditProfile.setOnClickListener {
                navigatePage(EditProfileActivity::class.java)
            }
            btnChangePass.setOnClickListener {
                navigatePage(ChangePasswordActivity::class.java)
            }
            btnLanguage.setOnClickListener {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            btnLogout.setOnClickListener {
                logout()
            }
        }

        return binding.root
    }

    private fun getCurrentUserApi() {
        viewModel.getCurrentUserApi("Bearer ${userModel.id}").observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        userApi = result.data

                        binding.apply {
                            tvUserName.text = buildString {
                                append(userApi.firstName)
                                append(" ")
                                append(userApi.lastName)
                            }
                            tvUserEmail.text = userApi.email
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        Log.e(TAG, "${R.string.failed_get_account} ${result.error}")
                        showToast(getString(R.string.failed_get_account))
                    }
                }
            }
        }
    }

    private fun getCurrentUserGoogleAuth() {
        viewModel.getCurrentUserGoogleAuth(userModel.id!!).observe(requireActivity()) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        userGoogleAuth = result.data
                        Log.d(TAG, "userGoogleAuth : $userGoogleAuth")

                        binding.apply {
                            Glide
                                .with(requireContext())
                                .load(userGoogleAuth.profilePic)
                                .into(ivProfilePicture)
                            tvUserName.text = buildString {
                                append(userGoogleAuth.firstName)
                                append(" ")
                                append(userGoogleAuth.lastName)
                            }
                            tvUserEmail.text = userGoogleAuth.email
                        }
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE

                        Log.e(TAG, "${getString(R.string.cant_get_user_data_google)} : ${result.error.message}")
                        showToast(getString(R.string.cant_get_user_data_google))
                    }
                }
            }
        }
    }

    private fun navigatePage(destination: Class<*>) {
        val intent = Intent(requireContext(), destination)
        if (!userModel.googleAuth) {
            intent.putExtra("USER_RESPONSE", userApi)
        } else {
            intent.putExtra("USER_GOOGLE_AUTH", userGoogleAuth)
        }
        startActivity(intent)
    }

    private fun logout() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireContext())

            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())

            userModel.id = ""
            userModel.rememberMe = false
            userModel.googleAuth = false
            userPreference.setUser(userModel)

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            showToast(getString(R.string.success_logout))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "ProfileFragment"
    }
}
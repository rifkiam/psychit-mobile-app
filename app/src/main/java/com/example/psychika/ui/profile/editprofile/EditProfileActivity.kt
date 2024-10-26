package com.example.psychika.ui.profile.editprofile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.psychika.R
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.data.network.firebase.UserGoogleAuth
import com.example.psychika.data.network.response.UserResponse
import com.example.psychika.databinding.ActivityEditProfileBinding
import com.example.psychika.databinding.PopUpChangesBinding
import com.example.psychika.ui.MainActivity
import com.example.psychika.ui.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class EditProfileActivity : AppCompatActivity(), OnImageSelectedListener {
    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel by viewModels<EditProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userModel: User = User()
    private lateinit var userPreference: UserPreference
    private var userResponse: UserResponse? = null
    private var userGoogleAuth: UserGoogleAuth? = null

    private lateinit var db: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        db = Firebase.database

        binding.apply {
            ivBackButton.setOnClickListener { finish() }
            ivEditProfile.setOnClickListener { showBottomSheet() }
            btnSaveEdit.setOnClickListener { saveChanges() }

            if (!userModel.googleAuth) {
                userResponse = intent.getParcelableExtra("USER_RESPONSE")
                getCurrentUserApi()
            } else {
                ivEditProfile.visibility = View.GONE
                userGoogleAuth = intent.getParcelableExtra("USER_GOOGLE_AUTH")
                getCurrentUserGoogleAuth()
            }
        }
    }

    private fun getCurrentUserApi() {
        binding.apply {
            etEditFirstName.setText(userResponse!!.firstName)
            etEditLastName.setText(userResponse!!.lastName)
            etEditEmail.setText(userResponse!!.email)
        }
    }

    private fun getCurrentUserGoogleAuth() {
        binding.apply {
            Glide
                .with(this@EditProfileActivity)
                .load(userGoogleAuth!!.profilePic)
                .into(ivProfilePicture)
            etEditFirstName.setText(userGoogleAuth!!.firstName)
            etEditLastName.setText(userGoogleAuth!!.lastName)
            etEditEmail.setText(userGoogleAuth!!.email)
        }
    }

    private fun showBottomSheet() {
        val bottomSheetFragment = EditProfileBottomSheetFragment()
        bottomSheetFragment.setOnImageSelectedListener(this)
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun saveChanges() {
        val etFirstName = binding.etEditFirstName.text.toString()
        val etLastName = binding.etEditLastName.text.toString()
        val etEmail = binding.etEditEmail.text.toString()

        if (!userModel.googleAuth) {
            updateCurrentUserApi(etFirstName, etLastName, etEmail)
        } else {
            updateCurrentUserGoogleAuth(etFirstName, etLastName, etEmail)
        }
    }

    private fun updateCurrentUserApi(etFirstName: String, etLastName: String, etEmail: String) {
        viewModel.updateCurrentUserApi(
            "Bearer ${userModel.id}",
            etFirstName,
            etLastName,
            etEmail
        ).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> { }
                    is Result.Success -> {
                        showPopUp()
                    }
                    is Result.Error -> {
                        Log.d(TAG, "Failed to save changes: ${result.error.message}")
                        showToast(getString(R.string.failed_changes))
                    }
                }
            }
        }
    }

    private fun updateCurrentUserGoogleAuth(etFirstName: String, etLastName: String, etEmail: String) {
        val user = hashMapOf(
            "id" to userGoogleAuth!!.id,
            "profilePic" to userGoogleAuth!!.profilePic,
            "firstName" to etFirstName,
            "lastName" to etLastName,
            "email" to etEmail,
        )

        Log.d(TAG, "Update Current Google Auth: $user")

        viewModel.updateCurrentUserGoogle(userGoogleAuth!!.id!!, user).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE

                        showPopUp()
                    }
                    is Result.Error -> {
                        showToast(getString(R.string.failed_changes))
                    }
                }
            }
        }
    }

    private fun showPopUp() {
        val popupBinding = PopUpChangesBinding.inflate(layoutInflater)

        val alertDialog = AlertDialog.Builder(this)
            .setView(popupBinding.root)
            .setCancelable(false)
            .create()
        alertDialog.show()

        popupBinding.btnOk.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("navigateToProfile", true)
            startActivity(intent)
        }
    }

    override fun onImageSelected(imageUri: Uri) {
        binding.ivProfilePicture.setImageURI(imageUri)
    }

    private fun showToast(message: String) {
        Toast.makeText(this@EditProfileActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "EditProfileActivity"
    }
}
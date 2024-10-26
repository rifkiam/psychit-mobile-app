package com.example.psychika.ui.profile.changepass

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.psychika.R
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.data.network.Result
import com.example.psychika.data.network.response.UserResponse
import com.example.psychika.databinding.ActivityChangePasswordBinding
import com.example.psychika.databinding.PopUpChangesBinding
import com.example.psychika.ui.MainActivity
import com.example.psychika.ui.ViewModelFactory

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel by viewModels<ChangePasswordViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private var userModel: User = User()
    private lateinit var userPreference: UserPreference

    private var userResponse: UserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()

        userResponse = intent.getParcelableExtra("USER_RESPONSE")

        binding.apply {
            ivBackButton.setOnClickListener { finish() }
            etEditEmail.setText(userResponse!!.email)
            btnSaveEdit.setOnClickListener { saveChanges() }
        }
    }

    private fun saveChanges() {
        val etCurrentPass = binding.etEditCurrentPass.text
        val etNewPass = binding.etEditNewPass.text
        val etConfirmNewPass = binding.etEditConfirmNewPass.text

        if (etCurrentPass!!.isEmpty() || etNewPass!!.isEmpty() || etConfirmNewPass!!.isEmpty()) {
            showToast(getString(R.string.empty_form))
        } else if (etNewPass.length < 8 || etConfirmNewPass.length < 8) {
            showToast(getString(R.string.invalid_form))
        } else if (etNewPass.toString() != etConfirmNewPass.toString()) {
            showToast(getString(R.string.pass_not_match))
        } else {
            viewModel.updateChangePass(
                "Bearer ${userModel.id}",
                etCurrentPass.toString(),
                etNewPass.toString()
            ).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> { }
                        is Result.Success -> {
                            showPopUp()
                        }
                        is Result.Error -> {
                            showToast(result.error.message)
                        }
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
            alertDialog.dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_SHORT).show()
    }
}
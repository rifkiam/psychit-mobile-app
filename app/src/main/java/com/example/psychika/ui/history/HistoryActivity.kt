package com.example.psychika.ui.history

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psychika.R
import com.example.psychika.adapter.HistoryAdapter
import com.example.psychika.data.entity.DailyAveragePrediction
import com.example.psychika.data.local.preference.user.User
import com.example.psychika.data.local.preference.user.UserPreference
import com.example.psychika.databinding.ActivityHistoryBinding
import com.example.psychika.databinding.PopUpHistoryBinding
import com.example.psychika.ui.ViewModelFactory
import com.example.psychika.ui.history.chatbot.ChatbotHistory

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel by viewModels<HistoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var historyAdapter: HistoryAdapter

    private lateinit var userModel: User
    private lateinit var userPreference: UserPreference
    private lateinit var userId: String

    private var isDialogShowing = false
    private var dialogDate: String? = null
    private var avgPredict: Double? = null
    private var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreference = UserPreference(this)
        userModel = userPreference.getUser()
        userId = userModel.id!!

        if (savedInstanceState != null) {
            isDialogShowing = savedInstanceState.getBoolean("isDialogShowing")
            dialogDate = savedInstanceState.getString("dialogDate")
            avgPredict = savedInstanceState.getDouble("avgPredict")
        }

        setupRecyclerView()
        setDataChatHistoryDate()

        binding.apply {
            ivBack.setOnClickListener { finish() }
            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filterDate(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        if (isDialogShowing && dialogDate != null) {
            showPopUp(dialogDate!!, avgPredict!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isDialogShowing", isDialogShowing)
        outState.putString("dialogDate", dialogDate)
        avgPredict?.let { outState.putDouble("avgPredict", it) }
    }

    private fun filterDate(query: String) {
        historyAdapter.filterByDate(query)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this@HistoryActivity)
        historyAdapter = HistoryAdapter()

        binding.rvListHistory.apply {
            setLayoutManager(layoutManager)
            adapter = historyAdapter
        }

        historyAdapter.setOnItemClickCallBack(object : HistoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: DailyAveragePrediction) {
                showPopUp(data.date, data.averagePredict)
            }
        })
    }

    private fun setDataChatHistoryDate() {
        viewModel.getAllDateMessages(userId).observe(this) { history ->
            historyAdapter.setOriginalList(history)
        }
    }

    private fun showPopUp(date: String, predict: Double) {
        val popUpBinding = PopUpHistoryBinding.inflate(layoutInflater)

        popUpBinding.tvAdvice.text =
            if (predict < 80.0) {
                getString(R.string.low_mental_health)
            } else {
                getString(R.string.high_mental_health)
            }

        alertDialog = AlertDialog.Builder(this)
            .setView(popUpBinding.root)
            .setCancelable(false)
            .create()
        alertDialog?.show()

        popUpBinding.apply {
            btnBack.setOnClickListener {
                alertDialog?.dismiss()
                isDialogShowing = false
                dialogDate = null
                avgPredict = null
            }
            btnSeeHistory.setOnClickListener {
                isDialogShowing = false
                dialogDate = null
                avgPredict = null
                val intent = Intent(this@HistoryActivity, ChatbotHistory::class.java)
                intent.putExtra("DATE_CHAT_HISTORY", date)
                startActivity(intent)
                alertDialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }
}
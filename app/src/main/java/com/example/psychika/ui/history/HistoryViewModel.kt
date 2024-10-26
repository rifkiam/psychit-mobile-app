package com.example.psychika.ui.history

import androidx.lifecycle.ViewModel
import com.example.psychika.data.repository.PsychikaRepository

class HistoryViewModel(private val repository: PsychikaRepository): ViewModel() {
    fun getAllDateMessages(userId: String) =
        repository.getAllDateMessages(userId)
}
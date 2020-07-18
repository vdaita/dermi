package com.daita.dermi.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.daita.dermi.data.AppDatabase
import com.daita.dermi.data.Diagnosis
import com.daita.dermi.data.DiagnosisRepository

class DiagnosisViewModel(application: Application) : AndroidViewModel(application){
    private val repository: DiagnosisRepository

    val diagnoses: LiveData<List<Diagnosis>>

    init {
        val diagnosisDao = AppDatabase.getDatabase(application).diagnosisDao()
        repository = DiagnosisRepository(diagnosisDao)
        diagnoses = repository.diagnoses
    }

    fun insert(diagnosis: Diagnosis) {
        repository.insert(diagnosis)
    }

    fun delete(diagnosis: Diagnosis){
        repository.delete(diagnosis)
    }
}
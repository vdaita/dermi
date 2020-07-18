package com.daita.dermi.data

import android.os.AsyncTask
import androidx.lifecycle.LiveData

class DiagnosisRepository(private val diagnosisDao: DiagnosisDao){
    val diagnoses: LiveData<List<Diagnosis>> = diagnosisDao.getAll()
    fun getById(id: Int): Diagnosis{
        return diagnosisDao.getById(id)
    }
    fun insert(diagnosis: Diagnosis){
        val atask = InsertDiagnosisAsyncTask(diagnosisDao).execute(diagnosis)
    }
    fun delete(diagnosis: Diagnosis) {
        val atask = DeleteDiagnosisAsyncTask(diagnosisDao).execute(diagnosis)
    }
    fun update(diagnosis: Diagnosis){
        val atask = UpdateDiagnosisAsyncTask(diagnosisDao).execute(diagnosis)
    }

    private class InsertDiagnosisAsyncTask(diagnosisDao: DiagnosisDao) : AsyncTask<Diagnosis, Unit, Unit>() {
        val diagnosisDao = diagnosisDao

        override fun doInBackground(vararg p0: Diagnosis?) {
            diagnosisDao.insert(p0[0]!!)
        }
    }


    private class DeleteDiagnosisAsyncTask(diagnosisDao: DiagnosisDao) : AsyncTask<Diagnosis, Unit, Unit>() {
        val diagnosisDao = diagnosisDao

        override fun doInBackground(vararg p0: Diagnosis?) {
            diagnosisDao.delete(p0[0]!!)
        }
    }

    private class UpdateDiagnosisAsyncTask(diagnosisDao: DiagnosisDao) : AsyncTask<Diagnosis, Unit, Unit>() {
        val diagnosisDao = diagnosisDao

        override fun doInBackground(vararg p0: Diagnosis?) {
            diagnosisDao.update(p0[0]!!)
        }
    }

}
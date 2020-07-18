package com.daita.dermi.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnosis")
    fun getAll(): LiveData<List<Diagnosis>>

    @Query("SELECT * FROM diagnosis WHERE id=:id")
    fun getById(id: Int): Diagnosis

    @Delete
    fun delete(diagnosis: Diagnosis)

    @Insert
    fun insert(diagnosis: Diagnosis)

    @Update
    fun update(diagnosis: Diagnosis)
}
package com.daita.dermi.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daita.dermi.R
import com.daita.dermi.data.AppDatabase
import com.daita.dermi.data.Diagnosis
import com.daita.dermi.data.DiagnosisRepository
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val diagnosisDao = AppDatabase.getDatabase(application).diagnosisDao()
        val repository = DiagnosisRepository(diagnosisDao)
        val diagnoses: LiveData<List<Diagnosis>> = repository.diagnoses

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = DiagnosisAdapter(this, repository)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        diagnoses.observeForever {
            adapter.setDiagnoses(it)
        }

        addFab.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(600, 600)
                .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            val filePath: String? = ImagePicker.getFilePath(data)
            val intent = Intent(this, DiagnosisActivity::class.java)
            intent.putExtra("file", fileUri.toString())
            startActivity(intent)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }
}
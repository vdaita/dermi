package com.daita.dermi.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daita.dermi.R
import com.daita.dermi.data.AppDatabase
import com.daita.dermi.data.Diagnosis
import com.daita.dermi.data.DiagnosisRepository
import com.daita.dermi.data.Disease
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_diagnosis.*
import kotlinx.android.synthetic.main.activity_edit.*
import org.json.JSONObject
import org.parceler.Parcels


class EditActivity : AppCompatActivity() {

    lateinit var adapter: DiseaseAdapter
    var diseases: MutableList<Disease> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val diagnosisDao = AppDatabase.getDatabase(application).diagnosisDao()
        val repository = DiagnosisRepository(diagnosisDao)

        adapter = DiseaseAdapter(this)

        var recyclerView = findViewById<RecyclerView>(R.id.editRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (!(intent.getIntExtra("id", -1) == -1)){

            val diagnosis: Diagnosis = repository.getById(intent.getIntExtra("id", -1))
            val response = JSONObject(diagnosis.otherDiagnoses)

            var classes: MutableList<String> = mutableListOf()
            var confidences: MutableList<Float> = mutableListOf()

            var results = response.getJSONArray("result")
            var labels = response.getJSONArray("labels")
            for(x in 0..(results.length()-1)){
                confidences.add(results.getDouble(x).toFloat())
            }
            for(x in 0..(results.length()-1)){
                classes.add(labels.getString(x))
            }

            diseases.clear()
            for(x in 0..(classes.size - 1)){
                diseases.add(Disease(classes.get(x), confidences.get(x), null, "https://wikipedia.com/" + classes.get(x)))
            }
            adapter.setDiagnoses(diseases)
            recyclerView.adapter = adapter

            Picasso.get().load(Uri.parse(diagnosis.fileUri)).into(editImageView)

            editNotes.setText(diagnosis.notes)

            editSave.setOnClickListener {
                diagnosis.notes = editNotes.text.toString()
                repository.update(diagnosis)
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        editConsulation.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.skymd.com"))
            startActivity(browserIntent)
        }

    }
}
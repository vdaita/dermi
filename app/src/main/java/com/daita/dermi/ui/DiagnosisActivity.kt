package com.daita.dermi.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.daita.dermi.R
import com.daita.dermi.data.AppDatabase
import com.daita.dermi.data.Diagnosis
import com.daita.dermi.data.DiagnosisRepository
import com.daita.dermi.data.Disease
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_diagnosis.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf


class DiagnosisActivity : AppCompatActivity() {
    val TAG = "DiagnosisActivity"
    lateinit var labels: List<ImageLabel>

    var classes: MutableList<String> = mutableListOf()
    var confidences: MutableList<Float> = mutableListOf()
    lateinit var filePath: String
    var jsonResponse: String = ""
    var numLines: Int = 0

    lateinit var adapter: DiseaseAdapter

    var diseases: MutableList<Disease> = mutableListOf(Disease("Loading...", 0.00000f, null, null))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosis)


        adapter = DiseaseAdapter(this)
        var recyclerView = findViewById<RecyclerView>(R.id.diseaseRecyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setDiagnoses(diseases)

        filePath = intent.getStringExtra("file")!!
        Log.v(TAG, "path: " + filePath)
        Picasso.get().load(filePath).into(diagnosisImageView)
        diagnoseVolley(filePath)

        saveFab.setOnClickListener {
            save(filePath)
        }

        goToConsultation.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://www.skymd.com"))
            startActivity(browserIntent)
        }
    }

    fun save(filePath: String){
        val diagnosisDao = AppDatabase.getDatabase(application).diagnosisDao()
        val repository = DiagnosisRepository(diagnosisDao)
        var diagnosis: Diagnosis = Diagnosis()
        diagnosis.fileUri = filePath
        diagnosis.notes = notes.text.toString()
        diagnosis.primaryDiagnosis = classes.get(0)
        diagnosis.primaryDiagnosisConfidence = confidences.get(0)
        diagnosis.otherDiagnoses = jsonResponse
        repository.insert(diagnosis)
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun displayResult(){
//        primaryDiagnosis.setText(classes.get(0) + " " + confidences.get(0))
//        var secondaryVal: String = ""
//        for(x in 1..9){
//            secondaryVal += classes.get(x) + " " + confidences.get(x) + "\n"
//        }
//        secondaryDiagnosis.setText(secondaryVal)
////        progressBar.visibility = View.GONE
        diseases.clear()
        for(x in 0..(classes.size - 1)){
            diseases.add(Disease(classes.get(x), confidences.get(x), null, " "))
        }
        adapter.setDiagnoses(diseases)
    }

    fun diagnose(filePath: String){
        val image: InputImage
        try {
            image = InputImage.fromFilePath(this, Uri.parse(filePath))
            var localModel: LocalModel = LocalModel.Builder()
                .setAssetFilePath("sd128-b7.tflite")
                .build()
            var labelerOptions: CustomImageLabelerOptions = CustomImageLabelerOptions.Builder(localModel)
                .setConfidenceThreshold(0.0f)
                .setMaxResultCount(10)
                .build()
            var imageLabeler: ImageLabeler = ImageLabeling.getClient(labelerOptions)
            imageLabeler.process(image)
                .addOnSuccessListener {
                    // displayResult(it)
                }
                .addOnFailureListener{
                    Log.e(TAG, it.toString())
                }
        } catch (e: IOException){
            Log.e(TAG, e.toString())
        }
    }

    fun diagnoseVolley(filePath: String){
        val queue = Volley.newRequestQueue(this)
        var postParam = HashMap<String, String>();
        postParam.put("img", encoder(filePath))

        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
           "http://e7ccdfabf0c7.ngrok.io/diagnose", JSONObject(postParam as Map<*, *>),
            Response.Listener { response ->
                Log.d(TAG, response.toString())
                jsonResponse = response.toString()
                var results = response.getJSONArray("result")
                var labels = response.getJSONArray("labels")
                for(x in 0..(results.length()-1)){
                    confidences.add(results.getDouble(x).toFloat())
                }
                for(x in 0..(results.length()-1)){
                    classes.add(labels.getString(x))
                }
                displayResult()
            }, Response.ErrorListener { error ->
                Log.e(TAG, error.toString())
            }) {}

        queue.add(jsonObjReq)
    }

    fun encoder(filePath: String): String{
        val bytes = File(filePath.split(":/")[1]).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }
}
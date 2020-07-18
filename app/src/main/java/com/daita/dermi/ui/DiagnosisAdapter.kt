package com.daita.dermi.ui

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.daita.dermi.R
import com.daita.dermi.data.Diagnosis
import com.daita.dermi.data.DiagnosisRepository
import com.squareup.picasso.Picasso
import org.parceler.Parcels

class DiagnosisAdapter internal constructor(
    activityContext: Activity,
    repository: DiagnosisRepository
): RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(activityContext)
    private var diagnoses = emptyList<Diagnosis>()
    private val activityContext = activityContext
    private val repository = repository

    inner class DiagnosisViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diagnosisImageView: ImageView = itemView.findViewById<ImageView>(R.id.diseaseImageView)
        val diagnosisInfoText: TextView = itemView.findViewById<TextView>(R.id.diseaseInfo)
        val diagnosisDiseaseText: TextView = itemView.findViewById<TextView>(R.id.diseaseDiagnosis)
        val diseaseConstraintLayout: ConstraintLayout = itemView.findViewById<ConstraintLayout>(R.id.patientConstraintLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val itemView = inflater.inflate(R.layout.item_view, parent, false)
        return DiagnosisViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        val current = diagnoses[position]
        Picasso.get().load(current.fileUri).into(holder.diagnosisImageView)
        holder.diagnosisDiseaseText.text = current.primaryDiagnosis
        holder.diagnosisInfoText.text = current.notes
        holder.diseaseConstraintLayout.setOnLongClickListener{
            var builder = AlertDialog.Builder(this@DiagnosisAdapter.activityContext)
            builder.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                repository.delete(current)
                notifyDataSetChanged()
            })
            builder.setNegativeButton("Cancel", null)
            builder.setMessage("Do you want to delete this item?")
            var alertDialog = builder.create()
            alertDialog.show()
            true
        }
        holder.diseaseConstraintLayout.setOnClickListener {
            var intent = Intent(activityContext, EditActivity::class.java)
            intent.putExtra("id", current.id)
            activityContext.startActivity(intent)
        }
    }

    internal fun setDiagnoses(diagnoses: List<Diagnosis>){
        this.diagnoses = diagnoses
        notifyDataSetChanged()
    }

    override fun getItemCount() = diagnoses.size
}
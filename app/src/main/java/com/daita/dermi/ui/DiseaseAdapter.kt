package com.daita.dermi.ui

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.daita.dermi.R
import com.daita.dermi.data.Disease


class DiseaseAdapter internal constructor(
    context: Activity
): RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder>(){
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var diseases = emptyList<Disease>()
    private var activityContext = context

    inner class DiseaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val diseaseLinkImage: ImageView = itemView.findViewById<ImageView>(R.id.divDiseaseLink)
        val diagnosisConfidence: TextView = itemView.findViewById<TextView>(R.id.divDiseaseConfidence)
        val diagnosisDiseaseText: TextView = itemView.findViewById<TextView>(R.id.divDiseaseName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
        val itemView = inflater.inflate(R.layout.disease_item_view, parent, false)
        return DiseaseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
        val current = diseases[position]
        if(current.link == null){
            holder.diseaseLinkImage.visibility = View.INVISIBLE
        } else {
            holder.diseaseLinkImage.visibility = View.VISIBLE
            holder.diseaseLinkImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                var pd: String = current.diseaseName
                pd.replace('_', ' ')
                intent.putExtra(SearchManager.QUERY, pd) // query contains search string
                activityContext.startActivity(intent)
            }
        }
        holder.diagnosisDiseaseText.text = current.diseaseName
        if(current.confidence != 0.0f){
            holder.diagnosisConfidence.text = current.confidence.toString().substring(0..3)
        }
    }

    internal fun setDiagnoses(diseases: List<Disease>){
        this.diseases = diseases
        notifyDataSetChanged()
    }

    override fun getItemCount() = diseases.size
}
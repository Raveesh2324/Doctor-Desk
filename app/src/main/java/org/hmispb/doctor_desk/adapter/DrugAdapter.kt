package org.hmispb.doctor_desk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hmispb.doctor_desk.PrescriptionViewModel
import org.hmispb.doctor_desk.R
import org.hmispb.doctor_desk.model.DrugItem

class DrugAdapter(
    val drugList: MutableList<DrugItem>,
    private val prescriptionViewModel: PrescriptionViewModel
): RecyclerView.Adapter<DrugAdapter.DrugViewHolder>() {

    class DrugViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val drugName: TextView = view.findViewById(R.id.drug_name)
        val dosage: TextView = view.findViewById(R.id.dosage)
        val frequency : TextView = view.findViewById(R.id.frequency)
        val days : TextView = view.findViewById(R.id.days)
        val instructions : TextView = view.findViewById(R.id.instructions)
        val delete : ImageView = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrugViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.drug_list_item, parent, false)
        return DrugViewHolder(adapterLayout)
    }

    override fun getItemCount() = drugList.size

    override fun onBindViewHolder(holder: DrugViewHolder, position: Int) {
        val drugItem = drugList[position]
        holder.drugName.text = drugItem.drug.drugName
        holder.days.text = drugItem.days.toString()
        holder.dosage.text = drugItem.dosage.hgstrDoseName
        holder.frequency.text = drugItem.frequency.frequencyName
        holder.instructions.text = drugItem.instruction
        holder.delete.setOnClickListener {
            drugList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun updateData(newDrugList: MutableList<DrugItem>) {
        if (drugList.isEmpty()) drugList.addAll(newDrugList)
        drugList.clear()
        drugList.addAll(newDrugList)
        notifyDataSetChanged()
    }
}
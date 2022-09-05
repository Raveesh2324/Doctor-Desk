package org.hmispb.doctor_desk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hmispb.doctor_desk.PrescriptionViewModel
import org.hmispb.doctor_desk.R
import org.hmispb.doctor_desk.model.LabTestName

class TestAdapter(
    val testList: MutableList<LabTestName>,
    private val prescriptionViewModel: PrescriptionViewModel
): RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    class TestViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val test: TextView = view.findViewById(R.id.test)
        val delete : ImageView = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.test_list_item, parent, false)
        return TestViewHolder(adapterLayout)
    }

    override fun getItemCount() = testList.size

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val test = testList[position]
        holder.test.text = test.testName
        holder.delete.setOnClickListener {
            val tests = prescriptionViewModel.testList.value!!
            tests.removeAt(position)
            prescriptionViewModel.testList.postValue(tests)
        }
    }

    fun updateData(newDrugList: MutableList<LabTestName>) {
        if (testList.isEmpty()) testList.addAll(newDrugList)
        testList.clear()
        testList.addAll(newDrugList)
        notifyDataSetChanged()
    }
}
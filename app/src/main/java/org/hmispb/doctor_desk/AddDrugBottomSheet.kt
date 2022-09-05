package org.hmispb.doctor_desk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hmispb.doctor_desk.databinding.BottomsheetAddDrugBinding
import org.hmispb.doctor_desk.databinding.BottomsheetAddTestBinding
import org.hmispb.doctor_desk.model.Data
import org.hmispb.doctor_desk.model.DrugItem

@AndroidEntryPoint
class AddDrugBottomSheet(val data : Data,val prescriptionViewModel: PrescriptionViewModel) : BottomSheetDialogFragment() {
    private var _binding : BottomsheetAddDrugBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddDrugBinding.inflate(inflater,container,false)

        val drugList = mutableListOf<String>()
        for(drug in data.drugList) {
            drugList.add(drug.drugName)
        }
        binding.spinnerDrugName.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,drugList)

        val dosageList = mutableListOf<String>()
        for(dosage in data.drugDose) {
            dosageList.add(dosage.hgstrDoseName)
        }
        binding.spinnerDosage.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,dosageList)

        val frequencyList = mutableListOf<String>()
        for(frequency in data.drugFrequency) {
            frequencyList.add(frequency.frequencyName)
        }
        binding.spinnerFrequency.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,frequencyList)

        binding.addDrug.setOnClickListener {
            if(binding.days.text.isNullOrEmpty()) {
                binding.days.error = ""
                return@setOnClickListener
            }
            val drugItem = DrugItem(
                data.drugList[binding.spinnerDrugName.selectedItemPosition],
                data.drugDose[binding.spinnerDosage.selectedItemPosition],
                data.drugFrequency[binding.spinnerFrequency.selectedItemPosition],
                Integer.parseInt(binding.days.text.toString()),
                binding.instructions.text.toString()
            )
            val list = prescriptionViewModel.drugList.value ?: mutableListOf()
            list.add(drugItem)
            prescriptionViewModel.drugList.postValue(list)
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
package org.hmispb.doctor_desk

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hmispb.doctor_desk.databinding.BottomsheetAddDrugBinding
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

        binding.spinnerDrugName.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_spinner_drug)
           // dialog.window?.setLayout(940,1300)
            dialog.show()
            //Initiate and assign variable
            val editText = dialog.findViewById<EditText>(R.id.edit_text)
            val listView = dialog.findViewById<ListView>(R.id.list_view)
            //Array Adapter init
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,drugList)
            listView.adapter = adapter
            editText.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    adapter.filter.filter(p0)
                }

                override fun afterTextChanged(p0: Editable?) = Unit

            })
            listView.setOnItemClickListener { p0, p1, p2, p3 ->
                binding.spinnerDrugName.text = adapter.getItem(p2)
                dialog.dismiss()
            }
        }

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
            val drugItem = data.drugList.find {
                it.drugName == binding.spinnerDrugName.text
            }?.let { it1 ->
                DrugItem(
                    it1,
                    data.drugDose[binding.spinnerDosage.selectedItemPosition],
                    data.drugFrequency[binding.spinnerFrequency.selectedItemPosition],
                    Integer.parseInt(binding.days.text.toString()),
                    binding.instructions.text.toString()
                )
            }
            val list = prescriptionViewModel.drugList.value ?: mutableListOf()
            if (drugItem != null) {
                list.add(drugItem)
            }
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
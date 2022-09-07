package org.hmispb.doctor_desk

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.hmispb.doctor_desk.databinding.BottomsheetAddDrugBinding
import org.hmispb.doctor_desk.databinding.BottomsheetAddTestBinding
import org.hmispb.doctor_desk.model.Data
import org.hmispb.doctor_desk.model.LabTestName

class AddTestBottomSheet(val data : Data, val prescriptionViewModel: PrescriptionViewModel) : BottomSheetDialogFragment() {
    private var _binding : BottomsheetAddTestBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddTestBinding.inflate(inflater,container,false)

        val testList = mutableListOf<String?>()
        for(test in data.labTestName) {
            testList
                .add(test?.testName ?: "")
        }
        binding.spinnerTest.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,testList)

        binding.addTest.setOnClickListener {
            val tests = prescriptionViewModel.testList.value ?: mutableListOf()
            tests.add(data.labTestName[binding.spinnerTest.selectedItemPosition] ?: LabTestName.nullLabTestName )

            prescriptionViewModel.testList.postValue(tests)
            dismiss()
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
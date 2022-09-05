package org.hmispb.doctor_desk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.hmispb.doctor_desk.databinding.BottomsheetAddDrugBinding
import org.hmispb.doctor_desk.databinding.BottomsheetAddTestBinding
import org.hmispb.doctor_desk.model.Data

class AddTestBottomSheet(val data : Data) : BottomSheetDialogFragment() {
    private var _binding : BottomsheetAddTestBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddTestBinding.inflate(inflater,container,false)

        val testList = mutableListOf<String>()
        for(test in data.labTestName) {
            testList.add(test.testName)
        }
        binding.spinnerTest.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,testList)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
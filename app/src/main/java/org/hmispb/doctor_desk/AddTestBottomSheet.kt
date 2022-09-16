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
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
        binding.spinnerTest.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_spinner)
          //  dialog.window?.setLayout(940,1300)
            dialog.show()
            //Initiate and assign variable
            val editText = dialog.findViewById<EditText>(R.id.edit_text)
            val listView = dialog.findViewById<ListView>(R.id.list_view)
            //Array Adapter init
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,testList)
            listView.adapter = adapter
            editText.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    adapter.filter.filter(p0)
                }

                override fun afterTextChanged(p0: Editable?) = Unit

            })
            listView.setOnItemClickListener { p0, p1, p2, p3 ->
                binding.spinnerTest.text = adapter.getItem(p2)
                dialog.dismiss()
            }
        }

        binding.addTest.setOnClickListener {
            if (binding.spinnerTest.text=="") Toast.makeText(
                requireContext(),
                "Please select test",
                Toast.LENGTH_SHORT
            ).show()
            else {
                val tests = prescriptionViewModel.testList.value ?: mutableListOf()
                tests.add(data.labTestName.find {
                    it?.testName == binding.spinnerTest.text
                } ?: LabTestName.nullLabTestName)

                prescriptionViewModel.testList.postValue(tests)
                dismiss()
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
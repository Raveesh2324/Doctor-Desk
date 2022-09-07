package org.hmispb.doctor_desk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.hmispb.doctor_desk.adapter.DrugAdapter
import org.hmispb.doctor_desk.adapter.TestAdapter
import org.hmispb.doctor_desk.databinding.ActivityMainBinding
import org.hmispb.doctor_desk.model.Data
import org.hmispb.doctor_desk.model.Drugdtl
import org.hmispb.doctor_desk.model.Prescription
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drugAdapter: DrugAdapter
    private lateinit var testAdapter: TestAdapter
    private lateinit var prescriptionViewModel : PrescriptionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prescriptionViewModel = ViewModelProvider(this)[PrescriptionViewModel::class.java]
        val jsonString = resources!!.openRawResource(R.raw.data).bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString,Data::class.java)

        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        Log.d("hello",data.labTestName.toString())

        binding.crNoInitials.text = "${Persistence.hospitalStuff}$day$month$year"

        drugAdapter = DrugAdapter(mutableListOf(),prescriptionViewModel)
        binding.drugList.adapter = drugAdapter

        testAdapter = TestAdapter(mutableListOf(),prescriptionViewModel)
        binding.testList.adapter = testAdapter

        prescriptionViewModel.drugList.observe(this) {
            drugAdapter.updateData(it)
        }

        prescriptionViewModel.testList.observe(this) {
            testAdapter.updateData(it)
        }

        binding.drugAdd.setOnClickListener {
            val addDrugBottomSheet = AddDrugBottomSheet(data,prescriptionViewModel)
            addDrugBottomSheet.show(supportFragmentManager,"addDrug")
        }

        binding.testAdd.setOnClickListener {
            val addTestBottomSheet = AddTestBottomSheet(data,prescriptionViewModel)
            addTestBottomSheet.show(supportFragmentManager,"addTest")
        }

        binding.submit.setOnClickListener {
            if(binding.crno.text.isNullOrEmpty() || binding.history.text.isNullOrEmpty()) {
                if(binding.crno.text.isNullOrEmpty())
                    binding.crno.error = "Required"
                if(binding.history.text.isNullOrEmpty())
                    binding.history.text.isNullOrEmpty()
                Toast.makeText(this@MainActivity,"One or more fields are empty",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val drugs = prescriptionViewModel.drugList.value ?: mutableListOf()
            val drugDetails = mutableListOf<Drugdtl>()
            for(drugItem in drugs) {
                drugDetails.add(
                    Drugdtl(
                    drugItem.dosage.hgnumDoseId,
                    drugItem.drug.itemId,
                    drugItem.frequency.frequencyId,
                    drugItem.instruction,
                    drugItem.days.toString()
                ))
            }

            val tests = prescriptionViewModel.testList.value ?: mutableListOf()
            val testCodes = mutableListOf<Int>()
            for(test in tests) {
                testCodes.add(test.testCode)
            }
            val prescription = Prescription(
                CR_No = Integer.parseInt(binding.crno.text.toString()),
                Drugdtl = drugDetails,
                InvTestCode = testCodes
            )
            prescriptionViewModel.insertPrescription(prescription)
            binding.crno.setText("")
            binding.history.setText("")
            prescriptionViewModel.drugList.postValue(mutableListOf())
            prescriptionViewModel.testList.postValue(mutableListOf())
            Toast.makeText(this@MainActivity,"Prescription saved",Toast.LENGTH_SHORT).show()
        }

        prescriptionViewModel.prescriptionList.observe(this) {
            Log.d("listy",it.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.upload_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val view = LayoutInflater.from(this).inflate(R.layout.login_dialog,null,false)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        dialog.setOnShowListener { dialogInterface ->
            val username = dialog.findViewById<EditText>(R.id.username)
            val password = dialog.findViewById<EditText>(R.id.password)
            val upload = dialog.findViewById<Button>(R.id.upload)
            upload?.setOnClickListener {
                prescriptionViewModel.savePrescription(prescriptionViewModel.prescriptionList.value?.get(0)!! )
            }
            //TODO upload using user name and password to do, will consult with the team, above temp till then, but it works
//            upload?.setOnClickListener {
//                if(username?.text.toString().isEmpty() || password?.text.isNullOrEmpty()) {
//                    if(username?.text.toString().isEmpty())
//                        username?.error = "Required"
//                    if(password?.text.toString().isEmpty())
//                        password?.error = "Required"
//                    Toast.makeText(this@MainActivity,"One or more fields are empty", Toast.LENGTH_SHORT).show()
//                    return@setOnClickListener
//                }
//                patientViewModel.upload(username!!.text.toString(),password!!.text.toString())
            }
//            patientViewModel.uploaded.observe(this@MainActivity) { uploaded ->
//                if(uploaded) {
//                    Toast.makeText(this@MainActivity,"Data successfully uploaded", Toast.LENGTH_SHORT).show()
//                    dialogInterface.cancel()
//                    patientViewModel.uploaded.value = false
//                }
//            }
//        }
        dialog.show()
        return super.onOptionsItemSelected(item)
    }
}
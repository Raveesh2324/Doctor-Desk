package org.hmispb.doctor_desk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.hmispb.doctor_desk.adapter.DrugAdapter
import org.hmispb.doctor_desk.databinding.ActivityMainBinding
import org.hmispb.doctor_desk.model.Data

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drugAdapter: DrugAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prescriptionViewModel = ViewModelProvider(this)[PrescriptionViewModel::class.java]

        val jsonString = resources!!.openRawResource(R.raw.data).bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString,Data::class.java)

        Log.d("hello",data.labTestName.toString())

        drugAdapter = DrugAdapter(mutableListOf(),prescriptionViewModel)
        binding.drugList.adapter = drugAdapter

        prescriptionViewModel.drugList.observe(this) {
            drugAdapter.updateData(it)
        }

        binding.drugAdd.setOnClickListener {
            val addDrugBottomSheet = AddDrugBottomSheet(data,prescriptionViewModel)
            addDrugBottomSheet.show(supportFragmentManager,"addDrug")
        }

        binding.testAdd.setOnClickListener {
            val addTestBottomSheet = AddTestBottomSheet(data)
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
                if(username?.text.toString().isEmpty() || password?.text.isNullOrEmpty()) {
                    if(username?.text.toString().isEmpty())
                        username?.error = "Required"
                    if(password?.text.toString().isEmpty())
                        password?.error = "Required"
                    Toast.makeText(this@MainActivity,"One or more fields are empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                patientViewModel.upload(username!!.text.toString(),password!!.text.toString())
            }
//            patientViewModel.uploaded.observe(this@MainActivity) { uploaded ->
//                if(uploaded) {
//                    Toast.makeText(this@MainActivity,"Data successfully uploaded", Toast.LENGTH_SHORT).show()
//                    dialogInterface.cancel()
//                    patientViewModel.uploaded.value = false
//                }
//            }
        }
        dialog.show()
        return super.onOptionsItemSelected(item)
    }
}
package org.hmispb.doctor_desk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.gson.Gson
import org.hmispb.doctor_desk.databinding.ActivityMainBinding
import org.hmispb.doctor_desk.model.Data

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val prescriptionViewModel: PrescriptionViewModel by  viewModels()
        val jsonString = resources!!.openRawResource(R.raw.data).bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString,Data::class.java)
    }
}
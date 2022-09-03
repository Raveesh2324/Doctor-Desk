package org.hmispb.doctor_desk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import org.hmispb.doctor_desk.model.Data

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val jsonString = resources!!.openRawResource(R.raw.data).bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString,Data::class.java)

    }
}
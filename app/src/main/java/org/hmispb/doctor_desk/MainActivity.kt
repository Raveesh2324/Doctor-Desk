package org.hmispb.doctor_desk

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.hmispb.doctor_desk.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prescriptionViewModel : PrescriptionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prescriptionViewModel = ViewModelProvider(this)[PrescriptionViewModel::class.java]

        binding.newPrescription.setOnClickListener {
            val intent = Intent(this,PrescriptionActivity::class.java)
            startActivity(intent)
        }

        binding.printPrescription.setOnClickListener {
            val intent = Intent(this,PrintPrescriptionActivity::class.java)
            startActivity(intent)
        }

        prescriptionViewModel.prescriptionList.observe(this) {
            Log.d("sadge",it.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.upload_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val view = LayoutInflater.from(this).inflate(R.layout.login_dialog, null, false)
        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()
        dialog.setOnShowListener { dialogInterface ->
            val username = dialog.findViewById<EditText>(R.id.username)
            val password = dialog.findViewById<EditText>(R.id.password)
            val upload = dialog.findViewById<Button>(R.id.upload)
            upload?.setOnClickListener {
                if (username?.text.toString().isEmpty() || password?.text.isNullOrEmpty()) {
                    if (username?.text.toString().isEmpty())
                        username?.error = "Required"
                    if (password?.text.toString().isEmpty())
                        password?.error = "Required"
                    Toast.makeText(
                        this@MainActivity,
                        "One or more fields are empty",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                prescriptionViewModel.prescriptionList.observe(this@MainActivity) { prescriptions ->
                    prescriptionViewModel.upload(
                        username!!.text.toString(),
                        password!!.text.toString(),
                        prescriptions
                    )
                }
            }
            prescriptionViewModel.loginFailed.observe(this) { failed ->
                if(failed) {
                    Toast.makeText(
                        this@MainActivity,
                        "Incorrect username or password",
                        Toast.LENGTH_SHORT
                    ).show()
                    prescriptionViewModel.loginFailed.postValue(false)
                }
            }
            prescriptionViewModel.uploaded.observe(this@MainActivity) { uploaded ->
                if(!uploaded)
                    return@observe
                prescriptionViewModel.prescriptionList.observe(this) {
                    val pres = it.find { presc ->
                        !presc.isUploaded
                    }
                    if (pres==null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Data successfully uploaded",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogInterface.cancel()
                    }
                    prescriptionViewModel.uploaded.postValue(false)
                }
            }
        }
        dialog.show()
        return super.onOptionsItemSelected(item)
    }
}
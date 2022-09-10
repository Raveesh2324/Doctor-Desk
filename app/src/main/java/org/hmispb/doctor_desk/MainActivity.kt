package org.hmispb.doctor_desk

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.hmispb.doctor_desk.adapter.DrugAdapter
import org.hmispb.doctor_desk.adapter.TestAdapter
import org.hmispb.doctor_desk.databinding.ActivityMainBinding
import org.hmispb.doctor_desk.model.Data
import org.hmispb.doctor_desk.model.Drugdtl
import org.hmispb.doctor_desk.model.Prescription
import java.io.FileOutputStream
import java.io.IOException
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

        Log.d("hello",data.labTestName.toString())

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
            if(binding.crno.text.isNullOrEmpty() || binding.history.text.isNullOrEmpty() || binding.chiefComplaint.text.isNullOrEmpty()) {
                if(binding.crno.text.isNullOrEmpty())
                    binding.crno.error = "Required"
                if(binding.history.text.isNullOrEmpty())
                    binding.history.error = "Required"
                if(binding.chiefComplaint.text.isNullOrEmpty())
                    binding.chiefComplaint.error = "Required"
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
                InvTestCode = testCodes,
                pat_Name = if(binding.name.text.isNullOrEmpty()) null else binding.name.text.toString(),
                patGender = when(binding.genderRadioGroup.checkedRadioButtonId) {
                    R.id.male -> "M"
                    R.id.female -> "F"
                    R.id.transgender -> "T"
                    else -> null
                }
            )
            prescriptionViewModel.insertPrescription(prescription)
//            binding.crno.setText("")
//            binding.history.setText("")
//            prescriptionViewModel.drugList.postValue(mutableListOf())
//            prescriptionViewModel.testList.postValue(mutableListOf())
            Toast.makeText(this@MainActivity,"Prescription saved",Toast.LENGTH_SHORT).show()
        }

        prescriptionViewModel.prescriptionList.observe(this) {
            Log.d("listy",it.toString())
        }

        binding.print.setOnClickListener {
            val printManager = getSystemService(PRINT_SERVICE) as PrintManager
            printManager.print("Doctor Desk Document",object : PrintDocumentAdapter(){
                private var pdfDocument: PrintedPdfDocument? = null
                override fun onLayout(
                    oldAttributes: PrintAttributes?,
                    newAttributes: PrintAttributes,
                    cancellationSignal: CancellationSignal?,
                    callback: LayoutResultCallback,
                    extras: Bundle?
                ) {
                    pdfDocument = PrintedPdfDocument(this@MainActivity,newAttributes)
                    if(cancellationSignal?.isCanceled==true) {
                        callback.onLayoutCancelled()
                        return
                    }
                    val info = PrintDocumentInfo.Builder("doctor_desk_prescription.pdf")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build()
                    callback.onLayoutFinished(info,oldAttributes!=newAttributes)
                }

                override fun onWrite(
                    p0: Array<out PageRange>?,
                    destination: ParcelFileDescriptor?,
                    cancellationSignal: CancellationSignal?,
                    callback: WriteResultCallback?
                ) {
                    val page = pdfDocument?.startPage(0)
                    if(cancellationSignal?.isCanceled==true) {
                        callback?.onWriteCancelled()
                        pdfDocument?.close()
                        pdfDocument = null
                        return
                    }
                    drawPage(page)
                    pdfDocument?.finishPage(page)

                    try {
                        pdfDocument?.writeTo(
                            FileOutputStream(
                                destination?.fileDescriptor
                            )
                        )
                    } catch (e: IOException) {
                        callback?.onWriteFailed(e.toString())
                        return
                    } finally {
                        pdfDocument?.close()
                        pdfDocument = null
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                }
                // TODO: how to do aaaaaaaaaaa
                private fun drawPage(page: PdfDocument.Page?) {
                    val canvas: Canvas? = page?.canvas

                    // units are in points (1/72 of an inch)
                    val titleBaseLine = 72f
                    val leftMargin = 54f
                    val paint = Paint()
                    paint.color = Color.BLACK
                    paint.textSize = 36f
                    val format = BitmapFactory.decodeResource(resources!!,R.drawable.img23)
                    canvas?.drawBitmap(format,0f,0f,null)
//                    canvas?.drawBitmap()
                    Log.d("sadge","${canvas?.height} ${canvas?.width}")
                    canvas?.drawText("Test Title", leftMargin, titleBaseLine, paint)
                    paint.textSize = 11f
                    canvas?.drawText("Test paragraph", leftMargin, titleBaseLine + 25, paint)
                    paint.color = Color.BLUE
                    canvas?.drawRect(100f, 100f, 172f, 172f, paint)
                }

            },null)
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
            prescriptionViewModel.uploaded.observe(this@MainActivity) { uploaded ->
                lifecycleScope.launch {
                    if (uploaded && dialog.isShowing) {
                        Toast.makeText(
                            this@MainActivity,
                            if (prescriptionViewModel.containsNotUploaded()) "One or more entries were not uploaded" else "Data successfully uploaded",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialogInterface.cancel()
                        prescriptionViewModel.uploaded.postValue(false)
                    }
                }
            }
        }
        dialog.show()
        return super.onOptionsItemSelected(item)
    }
}
package org.hmispb.doctor_desk

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.tejpratapsingh.pdfcreator.utils.FileManager
import com.tejpratapsingh.pdfcreator.utils.PDFUtil
import dagger.hilt.android.AndroidEntryPoint
import org.hmispb.doctor_desk.databinding.ActivityPrintPrescriptionBinding
import org.hmispb.doctor_desk.model.Data
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class PrintPrescriptionActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPrintPrescriptionBinding
    private lateinit var prescriptionViewModel : PrescriptionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrintPrescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try { supportActionBar?.setDisplayHomeAsUpEnabled(true) } catch (e : Exception){}

        val sharedPreferences = getSharedPreferences(Utils.LOGIN_RESPONSE_PREF, MODE_PRIVATE)

        prescriptionViewModel = ViewModelProvider(this)[PrescriptionViewModel::class.java]

        val jsonString = resources!!.openRawResource(R.raw.data).bufferedReader().use { it.readText() }
        val data = Gson().fromJson(jsonString, Data::class.java)

        val hospitalCode = sharedPreferences.getString(Utils.HOSPITAL_CODE,"")
        val currentDate = Date()
        val currentMonth = currentDate.month+1
        val currentYear = currentDate.year + 1900
        val crMiddle = "${if(currentDate.date<10) "0" else ""}${currentDate.date}${if(currentMonth<10) "0" else ""}${currentMonth}${currentYear.toString().substring(2)}"
        binding.crStart.setText(hospitalCode)
        binding.crMid.setText(crMiddle)

        binding.print.setOnClickListener {
            if(binding.crno.text.isNullOrEmpty() || binding.crno.text.toString().length<3) {
                binding.crno.error = "Required"
                Toast.makeText(this,"Cr No is incomplete",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val crno = hospitalCode + crMiddle + binding.crno.text.toString()
            prescriptionViewModel.prescriptionList.observe(this) { prescriptionList ->
                val prescription = prescriptionList.findLast { prescription ->
                    prescription.CR_No == crno
                }
                if(prescription==null) {
                    Toast.makeText(this,"Prescription with this cr no does not exist",Toast.LENGTH_SHORT).show()
                    return@observe
                }
                val printManager = getSystemService(PRINT_SERVICE) as PrintManager
                val file = FileManager.getInstance().createTempFile(this@PrintPrescriptionActivity,"pdf",false)
                var html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html;charset=utf-32" >
                    <meta charset="utf-32">
                </head>
                <style>
                .header {
                    font-size: 35px;
                    font-weight: bold;
                    margin: 15px;
                }

                .presc {
                    width: 100%;
                    margin: 20px auto;
                    font-size: 25px;
                }

                table,th,td {
                    border: 2px solid black;
                    border-collapse: collapse;
                }

                th,td {
                    padding: 5px;
                }

                .tab {
                    width : 100%;
                    margin: 20px auto;
                }

                .drugs {
                    display: flex;
                    flex-direction: column;
                    padding: 10px;
                }
                </style>
                <body>
                    <center class="header">
                        ${sharedPreferences.getString(Utils.HOSPITAL_NAME,"")}
                    </center>
                    <hr width="100%" color="black" size="3px">
                    <div class="presc">OPD PRESCRIPTION</div>
                    <table class="tab">
                        <tr>
                            <td>
                                CR No. : <b>${hospitalCode + crMiddle + binding.crno.text.toString()}</b>
                            </td>
                            <td>
                                Date & Time : <br><b>${"${if(currentDate.date<10) "0" else ""}${currentDate.date}/${if(currentMonth<10) "0" else ""}${currentMonth}/${currentYear.toString().substring(2)}"}</b>
                            </td>
                            <td>
                                Category : <b></b>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Patient Name : <b>${if(prescription.pat_Name.isNullOrEmpty()) "" else prescription.pat_Name}</b>
                            </td>
                            <td>
                                Age/Gender :<br> <b>${if(prescription.patAge.isNullOrEmpty()) "" else prescription.patAge}/${if(prescription.patGender.isNullOrEmpty()) "" else prescription.patGender}</b>
                            </td>
                            <td>
                                Father/Spouse/Mother Name : <br><b>${if(prescription.patGaurdianName.isNullOrEmpty()) "" else prescription.patGaurdianName}</b>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                Residence : <b></b>
                            </td>
                            <td>
                                Mobile No. :<b>${if(prescription.mobileNumber.isNullOrEmpty()) "" else prescription.mobileNumber}</b>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                VITALS/GE :
                            </td>
                            <td colspan="2">
                                
                            </td>
                        </tr>
                        <tr>
                            <td>
                                CHIEF COMPLAINT :
                            </td>
                            <td colspan="2">
                                ${prescription.chiefComplaint}
                            </td>
                        </tr>
                        <tr>
                            <td>
                                HISTORY OF PRESENT ILLNESS : 
                            </td>
                            <td colspan="2">
                                ${prescription.history}
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <b>Diagnostic Tests:</b>
                                <div class="drugs">
            """.trimIndent()

                for(i in prescription.InvTestCode.indices) {
                    val testCode = prescription.InvTestCode[i]
                    val test = data.labTestName.find {
                        it?.testCode == testCode
                    }
                    html += """
                        <div>${i+1}. <b>${test?.testName}</b></div>
                    """.trimIndent()
                }

                html += """
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <b>Rx:</b>
                                <div class="drugs">
                """.trimIndent()

                for(i in prescription.Drugdtl.indices) {
                    val drugDtl = prescription.Drugdtl[i]
                    val drug = data.drugList.find {
                        it.itemId == drugDtl.drugId
                    }
                    val dosage = data.drugDose.find {
                        it.hgnumDoseId == drugDtl.dosageId
                    }
                    val frequency = data.drugFrequency.find {
                        it.frequencyId == drugDtl.frequencyId
                    }
                    html += """
                        <div>${i+1}. <b>${drug?.drugName}</b>, ${dosage?.hgstrDoseName}, ${frequency?.frequencyName}, ${drugDtl.noOfdays} Day${if(drugDtl.noOfdays.toInt()>1) "s" else ""},</div>
                    """.trimIndent()
                }

                html += """
                </div>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
            """.trimIndent()
                PDFUtil.generatePDFFromHTML(this@PrintPrescriptionActivity,file,html, object : PDFPrint.OnPDFPrintListener {
                    override fun onSuccess(file: File?) {
                        printManager.print("Doctor Desk Document",object : PrintDocumentAdapter(){
                            override fun onLayout(
                                oldAttributes: PrintAttributes?,
                                newAttributes: PrintAttributes,
                                cancellationSignal: CancellationSignal?,
                                callback: LayoutResultCallback,
                                extras: Bundle?
                            ) {
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
                                if(cancellationSignal?.isCanceled==true) {
                                    callback?.onWriteCancelled()
                                    return
                                }

                                try {
                                    val inputStream = FileInputStream(file)
                                    val outputStream = FileOutputStream(destination?.fileDescriptor)
                                    val buf = ByteArray(16384)
                                    var size: Int

                                    while (inputStream.read(buf).also { size = it } >= 0) {
                                        outputStream.write(buf, 0, size)
                                    }
                                } catch (e: IOException) {
                                    callback?.onWriteFailed(e.toString())
                                    return
                                }
                                callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                            }

                        },null)
                    }

                    override fun onError(exception: Exception?) {
                        exception?.printStackTrace()
                    }

                })
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
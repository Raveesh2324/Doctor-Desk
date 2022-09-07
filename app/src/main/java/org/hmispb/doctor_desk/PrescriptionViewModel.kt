package org.hmispb.doctor_desk

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hmispb.doctor_desk.model.DrugItem
import org.hmispb.doctor_desk.model.LabTestName
import org.hmispb.doctor_desk.model.LoginResponse
import org.hmispb.doctor_desk.model.Prescription
import org.hmispb.doctor_desk.room.PrescriptionRepository
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(private val prescriptionRepository: PrescriptionRepository) : ViewModel() {
    var prescriptionList = prescriptionRepository.getAllPrescriptions()
    var uploaded : MutableLiveData<Boolean> = MutableLiveData(false)
    val drugList : MutableLiveData<MutableList<DrugItem>> = MutableLiveData(mutableListOf())
    val testList : MutableLiveData<MutableList<LabTestName>> = MutableLiveData(mutableListOf())

    fun insertPrescription(prescription: Prescription) {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.insertPrescription(prescription)
        }
    }

    fun deletePrescription(prescription: Prescription) {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.deletePrescription(prescription)
        }
    }

    fun deleteAllPrescriptions() {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.deleteAllPrescriptions()
        }
    }

    private fun savePrescription(prescription: Prescription, hospitalCode: String, userId: String) {
        viewModelScope.launch {
            prescriptionRepository.savePrescription(prescription,hospitalCode,userId)
        }
    }

    suspend fun login(username : String, password : String) : LoginResponse? =
        prescriptionRepository.login(username, password)

    private fun setUploaded(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            prescriptionRepository.setUploaded(id)
        }
    }

    suspend fun containsNotUploaded() : Boolean {
        return prescriptionRepository.containsNotUploaded()
    }

    fun upload(username : String, password : String,prescriptions : List<Prescription>) {
        viewModelScope.launch {
            try {
                val response = login(username,password)
                for(prescription in prescriptions) {
                    if(response!=null && !prescription.isUploaded) {
                        try {
                            savePrescription(prescription, response.dataValue!![0][0], response.dataValue[0][2])
                            setUploaded(prescription.id ?: 0)
                        } catch (e : Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                uploaded.postValue(true)
            } catch (e : Exception) {
                e.printStackTrace()
            }
        }
    }
}
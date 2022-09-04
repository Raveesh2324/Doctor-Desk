package org.hmispb.doctor_desk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.hmispb.doctor_desk.model.Prescription
import org.hmispb.doctor_desk.room.PrescriptionRepository
import javax.inject.Inject

@HiltViewModel
class PrescriptionViewModel @Inject constructor(private val prescriptionRepository: PrescriptionRepository) : ViewModel() {
    var prescriptionList = prescriptionRepository.getAllPrescriptions()

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

    fun savePrescription(prescription: Prescription) {
        viewModelScope.launch {
            prescriptionRepository.savePrescription(prescription)
        }
    }
}
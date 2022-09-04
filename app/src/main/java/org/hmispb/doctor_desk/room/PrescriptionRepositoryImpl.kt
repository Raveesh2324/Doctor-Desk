package org.hmispb.doctor_desk.room

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import org.hmispb.doctor_desk.model.Prescription
import org.hmispb.doctor_desk.model.SavePrescriptionRequest

class PrescriptionRepositoryImpl(private val prescriptionDao: PrescriptionDao, private val prescriptionApi: PrescriptionApi) :
    PrescriptionRepository {

    override fun insertPrescription(prescription: Prescription) {
        prescriptionDao.insertPrescription(prescription)
    }

    override fun getAllPrescriptions(): LiveData<List<Prescription>> {
        return prescriptionDao.getAllPrescriptions()
    }

    override fun deletePrescription(prescription: Prescription) {
        prescriptionDao.deletePrescription(prescription)
    }

    override fun deleteAllPrescriptions() {
        prescriptionDao.deleteAllPrescriptions()
    }

    override suspend fun savePrescription(prescription: Prescription) {
        val prescriptionString = Gson().toJson(prescription)
        // TODO : hospitalCode and seatId unknown
        val request = SavePrescriptionRequest(0,0,prescriptionString)
        prescriptionApi.savePrescription(request)
    }
}
package org.hmispb.doctor_desk.room

import androidx.lifecycle.LiveData
import org.hmispb.doctor_desk.model.LoginResponse
import org.hmispb.doctor_desk.model.Prescription

interface PrescriptionRepository {
    fun insertPrescription(prescription: Prescription)

    fun getAllPrescriptions() : LiveData<List<Prescription>>

    fun deletePrescription(prescription: Prescription)

    fun deleteAllPrescriptions()

    suspend fun savePrescription(prescription: Prescription)

    suspend fun login(username: String, password: String) : LoginResponse?
}
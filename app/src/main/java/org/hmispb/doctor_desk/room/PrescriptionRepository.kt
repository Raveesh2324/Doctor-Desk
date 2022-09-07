package org.hmispb.doctor_desk.room

import androidx.lifecycle.LiveData
import org.hmispb.doctor_desk.model.LoginResponse
import org.hmispb.doctor_desk.model.Prescription

interface PrescriptionRepository {
    suspend fun insertPrescription(prescription: Prescription)

    fun getAllPrescriptions() : LiveData<List<Prescription>>

    suspend fun deletePrescription(prescription: Prescription)

    suspend fun deleteAllPrescriptions()

    suspend fun savePrescription(prescription: Prescription, username: String, password: String)

    suspend fun login(username: String, password: String) : LoginResponse?

    suspend fun setUploaded(id : Int)

    suspend fun containsNotUploaded() : Boolean
}
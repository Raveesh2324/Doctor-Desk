package org.hmispb.doctor_desk.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import org.hmispb.doctor_desk.model.Prescription

@Dao
interface PrescriptionDao {
    @Insert
    fun insertPrescription(prescription : Prescription)

    @Query("SELECT * FROM prescription")
    fun getAllPrescriptions() : LiveData<List<Prescription>>

    @Delete
    fun deletePrescription(prescription: Prescription)

    @Query("DELETE FROM prescription")
    fun deleteAllPrescriptions()
}
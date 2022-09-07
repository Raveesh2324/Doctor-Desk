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
    suspend fun insertPrescription(prescription : Prescription)

    @Query("SELECT * FROM prescription")
    fun getAllPrescriptions() : LiveData<List<Prescription>>

    @Delete
    suspend fun deletePrescription(prescription: Prescription)

    @Query("DELETE FROM prescription")
    suspend fun deleteAllPrescriptions()

    @Query("UPDATE prescription SET isUploaded=1 WHERE id=:id")
    suspend fun setUploaded(id : Int)

    @Query("SELECT count(*) FROM prescription WHERE isUploaded=0")
    suspend fun notUploadedCount() : Int
}
package org.hmispb.doctor_desk.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.hmispb.doctor_desk.model.Prescription

@Database(
    entities = [Prescription::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(TypeConverter::class)
abstract class PrescriptionDatabase : RoomDatabase() {
    abstract val prescriptionDao : PrescriptionDao
}
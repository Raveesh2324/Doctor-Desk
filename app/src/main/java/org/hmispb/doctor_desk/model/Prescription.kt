package org.hmispb.doctor_desk.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Prescription(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,
    val CR_No: String,
    val history : String,
    val chiefComplaint : String,
    val Drugdtl: List<Drugdtl>,
    val InvTestCode: List<Int>,
    val currentVisitDate: String? = null,
    val episodeCode: Int? = null,
    val episodeVisitNo: Int? = null,
    val hosp_code: Int? = null,
    val hrgnum_is_docuploaded: Int = 0,
    val lastVisitDate: String? = null,
    val patAge: String? = null,
    val patCat: String? = null,
    val patConsultantName: String? = null,
    val patDept: String? = null,
    val patGaurdianName: String? = null,
    val patGender: String? = null,
    val patQueueNo: String? = null,
    val patVisitType: String? = null,
    val mobileNumber : String? = null,
    val pat_Name: String? = null,
    val seatId: Int? = null,
    var isUploaded : Boolean = false,

)
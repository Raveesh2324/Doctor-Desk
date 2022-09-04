package org.hmispb.doctor_desk.model

data class SavePrescriptionRequest(
    val hospitalCode : Int,
    val seatId : Int,
    val inputDataJson : String,
    val modeForData : String = "PATIENT_PRESCRIPTION"
)
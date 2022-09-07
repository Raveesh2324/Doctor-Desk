package org.hmispb.doctor_desk.model

data class SavePrescriptionRequest(
    val hospitalCode : String,
    val seatId : String,
    val inputDataJson : String,
    val modeForData : String = "PATIENT_PRESCRIPTION"
)
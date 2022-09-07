package org.hmispb.doctor_desk.model

data class SavePrescriptionRequest(
    //TODO take hospital code and seat ID from Login api
    val hospitalCode : Int = 998,
    val seatId : Int = 10001,
    val inputDataJson : String,
    val modeForData : String = "PATIENT_PRESCRIPTION"
)
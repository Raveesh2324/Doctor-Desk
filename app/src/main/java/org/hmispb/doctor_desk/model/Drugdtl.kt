package org.hmispb.doctor_desk.model

data class Drugdtl(
    val dosageId : String,
    val drugId: String,
    val frequencyId : String,
    val instrunction: String,
    val noOfdays : String
)
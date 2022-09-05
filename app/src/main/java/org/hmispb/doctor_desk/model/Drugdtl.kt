package org.hmispb.doctor_desk.model

data class Drugdtl(
    val dosageId : Int,
    val drugId: Int,
    val frequencyId : Int,
    val instrunction: String,
    val noOfdays : String
)
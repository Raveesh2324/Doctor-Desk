package org.hmispb.doctor_desk.model

data class DrugItem(
    val drug : Drug,
    val dosage : DrugDose,
    val frequency: DrugFrequency,
    val days : Int,
    val instruction : String
)
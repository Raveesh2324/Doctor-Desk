package org.hmispb.doctor_desk.model

data class LabTestName(
    val labCode: Int,
    val labName: String,
    val testCode: Int,
    val testName: String = ""
){
    companion object{
        val nullLabTestName = LabTestName(labCode = -100,
      labName = "",
      testCode = -100,
      testName= ""
        )
    }
}
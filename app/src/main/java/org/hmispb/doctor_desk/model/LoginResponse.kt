package org.hmispb.doctor_desk.model

data class LoginResponse(
    val  UserName: String,
    val  hospitalCode : String,
    val  hospitalName: String,
    val  responseStatus: String
)
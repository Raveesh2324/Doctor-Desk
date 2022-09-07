package org.hmispb.doctor_desk.room

import org.hmispb.doctor_desk.model.LoginRequest
import org.hmispb.doctor_desk.model.LoginResponse
import org.hmispb.doctor_desk.model.SavePrescriptionRequest
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface PrescriptionApi {
    @Headers("Content-Type:text/plain")
    @POST("saveNewPrescription")
    suspend fun savePrescription(
        @Body prescriptionRequest: SavePrescriptionRequest
    )

    @Headers("Content-Type:text/plain")
    @POST("LoginAPI")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ) : LoginResponse?
}
package org.hmispb.doctor_desk.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.hmispb.doctor_desk.model.Drugdtl
import java.lang.reflect.Type

object TypeConverter {
    @TypeConverter
    fun DrugDtlToJSON(drugDtls: List<Drugdtl>) = Gson().toJson(drugDtls)!!

    @TypeConverter
    fun DrugDtlFromJSON(drugDtlJSON: String) : List<Drugdtl>{
        val type: Type = object: TypeToken<ArrayList<Drugdtl>>() {}.type
        return Gson().fromJson(
            drugDtlJSON,
            type
        ) ?: emptyList()
    }

    @TypeConverter
    fun InvTestCodeToJSON(invTestCodes: List<Int>) = Gson().toJson(invTestCodes)!!

    @TypeConverter
    fun InvTestCodeFromJSON(invTestCodesJSON: String): List<Int>{
        val type: Type = object: TypeToken<ArrayList<Int>>() {}.type
        return Gson().fromJson(
            invTestCodesJSON,
            type
        ) ?: emptyList()
    }
}
package org.hmispb.doctor_desk.room

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.hmispb.doctor_desk.Utils.password
import org.hmispb.doctor_desk.Utils.username
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrescriptionModule {
    @Provides
    @Singleton
    fun providePrescriptionDatabase(app : Application) : PrescriptionDatabase {
        return Room.databaseBuilder(
            app,
            PrescriptionDatabase::class.java,
            "prescription_database"
        ).build()
    }

    @Provides
    @Singleton
    fun providePrescriptionRepository(prescriptionDatabase: PrescriptionDatabase, prescriptionApi: PrescriptionApi) : PrescriptionRepository {
        return PrescriptionRepositoryImpl(prescriptionDatabase.prescriptionDao,prescriptionApi)
    }

    @Provides
    @Singleton
    fun providePrescriptionApi(app : Application) : PrescriptionApi {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val cacheSize = (10*1024*1024).toLong()
        val cache = Cache(app.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor(logging)
            .addInterceptor(BasicAuthInterceptor(
                username,
                password
            ))

            .addInterceptor{
                var request = it.request()
                request = if (hasNetwork(app) ==true)
                    request.newBuilder().header("Cache-Control","public, max-age="+5).build()
                else
                    request.newBuilder().header("Cache-Control","public, only-if-cached, max-stale="+60*60*24*7).build()
                it.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl("https://hmispb.in/HISUtilities/services/restful/EMMSMasterDataWebService/DMLService/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(PrescriptionApi::class.java)
    }

    private fun hasNetwork(app : Application): Boolean? {
        var isConnected: Boolean? = false
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        if (activeNetwork != null && activeNetwork.isConnected)
            isConnected = true
        return isConnected
    }
}
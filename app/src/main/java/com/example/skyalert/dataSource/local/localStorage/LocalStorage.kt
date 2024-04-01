package com.example.skyalert.dataSource.local.localStorage

import android.content.Context
import com.example.skyalert.model.remote.FiveDaysForecast
import com.google.gson.Gson
import kotlin.random.Random

class LocalStorage(val context: Context) : ILocalStorage {

    companion object {
        @Volatile
        private lateinit var INSTANCE: ILocalStorage
        fun getInstance(context: Context) = if (::INSTANCE.isInitialized) INSTANCE else synchronized(this) {
            INSTANCE = LocalStorage(context)
            INSTANCE
        }
    }

    override suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast): String {
        val fileName = Random.nextInt().toString() + ".txt"
        val fis = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        val json = Gson().toJson(fiveDaysForecast)
        fis.write(json.toByteArray())
        fis.close()
        return fileName
    }

    override suspend fun getFiveDaysForecast(fileName: String): FiveDaysForecast {
        val fos = context.openFileInput(fileName)
        val buffer = ByteArray(fos.available())
        fos.read(buffer)
        fos.close()
        val json = String(buffer)
        return Gson().fromJson(json, FiveDaysForecast::class.java)
    }


}
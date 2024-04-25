package com.example.skyalert.dataSource.local.localStorage

import android.content.Context
import android.util.Log
import com.example.skyalert.model.remote.FiveDaysForecast
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class LocalStorage(val context: Context) : ILocalStorage {

    companion object {
        @Volatile
        private lateinit var INSTANCE: ILocalStorage
        fun getInstance(context: Context) =
            if (::INSTANCE.isInitialized) INSTANCE else synchronized(this) {
                INSTANCE = LocalStorage(context)
                INSTANCE
            }
    }

    override suspend fun saveFiveDaysForecast(fiveDaysForecast: FiveDaysForecast): String {
        val fileName = Random.nextInt().toString() + ".txt"
        val fis = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        withContext(Dispatchers.IO) {
            val json = Gson().toJson(fiveDaysForecast)
            Log.d("LocalStorage", "saveFiveDaysForecast: $json")
            fis.write(json.toByteArray())
            fis.close()
        }
        return fileName
    }

    override suspend fun getFiveDaysForecast(fileName: String): FiveDaysForecast {
        val fos = context.openFileInput(fileName)
        val json: String
        withContext(Dispatchers.IO) {
            val buffer = ByteArray(fos.available())
            fos.read(buffer)
            fos.close()
            json = String(buffer)
        }
        val result  = Gson().fromJson(json, FiveDaysForecast::class.java)
        Log.d("LocalStorage", "getFiveDaysForecast: $result")
        return result
    }


}
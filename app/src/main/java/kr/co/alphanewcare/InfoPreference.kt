package kr.co.alphanewcare

import android.content.Context
import android.content.SharedPreferences

class InfoPreference {

    companion object{
        private var instance: InfoPreference? = null
        private var infoPrefs: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null


        fun getInstance(context: Context) {
            instance ?: synchronized(this) {
                instance ?: InfoPreference().also {
                    instance = it
                }
            }
        }

        fun init(context: Context) {
            infoPrefs = context.getSharedPreferences("info", Context.MODE_PRIVATE)
            editor = infoPrefs!!.edit()
        }

        fun setIsInitial(value : Boolean) { editor!!.putBoolean("isInitial", value).apply() }
        fun getIsInitial() : Boolean { return infoPrefs!!.getBoolean("isInitial", true) }
    }

}
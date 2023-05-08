package com.example.analytics

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.analytics.models.userInfo
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    lateinit var dialog: Dialog
    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var gson: Gson
    lateinit var handle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        gson = Gson()
        handle = findViewById(R.id.handle)

        findViewById<Button>(R.id.btn).setOnClickListener {
            createDialog()
            fetchUser()
        }
    }

    private fun fetchUser() {
        val api = codeforcesApi.create().getUser(handle.text.toString())
//        Log.e(this.toString(), "Start")
        api.enqueue(object : Callback<userInfo> {
            override fun onResponse(call: Call<userInfo>, response: Response<userInfo>) {
                Log.e(this.toString(), "Success")
                Log.e("msme", response.toString())
                Log.e("msme", response.body().toString())
                dialog.cancel() // remove dialog
                val intent = Intent(applicationContext, mainScreen::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                val res = response.body()!!.result[0]
//                val dt = findViewById<TextView>(R.id.date)
//                dt.text = unixTimeToCurrTime(res.lastOnlineTimeSeconds.toString())

                val jsonData = gson.toJson(res)
                editor.apply {
                    putString("json", jsonData)
                    putString("userHandle", handle.text.toString())
                    apply()
                }
                intent.putExtra("userResponse", jsonData)
                intent.putExtra("userHandle", handle.text.toString())
                intent.putExtra("lastOnline", unixTimeToCurrTime(res.lastOnlineTimeSeconds.toString()))
                applicationContext.startActivity(intent)
            }

            override fun onFailure(call: Call<userInfo>, t: Throwable) {
                Log.e(this.toString(), "ERROr")
                dialog.cancel()
            }
        })
    }

    private fun createDialog() {
        dialog = Dialog(this)
        dialog.apply {
            setContentView(R.layout.animation)
            setCancelable(false)
            show()
        }
    }

}
package com.example.analytics

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.analytics.models.ResultX
import com.example.analytics.models.new_item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class contestList : AppCompatActivity() {
    lateinit var sharedPref: SharedPreferences
    lateinit var gson: Gson
    lateinit var contestList: ArrayList<new_item>
    lateinit var editor: SharedPreferences.Editor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contest_list)

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        gson = Gson()


        val contestJson = sharedPref.getString("upcomingContest", "")
        val itemType = object : TypeToken<List<ResultX>>() {}.type
        val lst = gson.fromJson<ArrayList<ResultX>>(
            contestJson, itemType
        )
//        Log.e("From Fragment", lst.toString())
        contestList = arrayListOf()
        for (contest in lst) {
            contestList.add(
                new_item(
                    contest.name,
                    unixTimeToCurrTime(contest.startTimeSeconds.toString())
                )
            )
        }

        val adapter = adapter(contestList)
        val rv = findViewById<RecyclerView>(R.id.rv)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(this)

    }
}
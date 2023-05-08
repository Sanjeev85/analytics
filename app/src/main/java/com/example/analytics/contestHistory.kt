package com.example.analytics

import android.content.SharedPreferences
import android.graphics.Color.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.analytics.databinding.ActivityContestHistoryBinding
import com.example.analytics.models.ResultXXX
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.abs

class contestHistory : AppCompatActivity() {
    lateinit var sharedPref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var gson: Gson
    lateinit var binding: ActivityContestHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContestHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE)
        editor = sharedPref.edit()
        gson = Gson()
        var bestRank = Int.MAX_VALUE
        var worstRank = 0
        var maxUp = 0
        var maxDown = 0


        val jsonList = sharedPref.getString("ratingChanges", "")
        Log.e("data", jsonList.toString())
        val itemType = object : TypeToken<List<ResultXXX>>() {}.type
        val ratingData = gson.fromJson<ArrayList<ResultXXX>>(jsonList, itemType)
        val yValues = arrayListOf<Entry>()
        val labels = arrayListOf<String>()

        val numberOfContests = ratingData.size

        var prevRating = 0
        for (i in ratingData.indices) {
            labels.add(unixTimeToCurrTime(ratingData[i].ratingUpdateTimeSeconds.toString()))
            maxUp = maxOf(prevRating - ratingData[i].newRating, maxUp)
            if (ratingData[i].newRating - prevRating <= 0) {
                maxDown = abs(ratingData[i].newRating - prevRating)
            }
            worstRank = maxOf(worstRank, ratingData[i].rank)
            prevRating = ratingData[i].newRating
            bestRank = minOf(ratingData[i].rank, bestRank)
            yValues.add(Entry((i + 5).toFloat(), ratingData[i].newRating.toFloat()))
        }
        Log.e("lable", labels.toString())


        binding.maxDown.text = maxDown.toString()
        binding.maxUp.text = maxUp.toString()
        binding.userName.text = ratingData[0].handle
        binding.NumOfContest.text = numberOfContests.toString()
        binding.WorstRank.text = worstRank.toString()
        binding.BestRank.text = bestRank.toString()

        binding.ratingChangesForUser.text = "Rating Changes For ${ratingData[0].handle}"


        val set1 = LineDataSet(yValues, "Rating Changes For ${ratingData[0].handle}")

        set1.fillAlpha = 110
        set1.valueTextSize = 14f
        set1.color = RED
        set1.isHighlightEnabled = true
        set1.lineWidth = 5f
        set1.valueTextColor = BLACK


        val x_axisLabel = binding.reportingChart.xAxis
        x_axisLabel.apply {
            position = XAxisPosition.BOTTOM
            setDrawGridLines(false)
            labelCount = ratingData.size
            labelRotationAngle = 90f
            valueFormatter = null
//            valueFormatter = IndexAxisValueFormatter(labels)
        }

        val dataset = mutableListOf<ILineDataSet>()
        dataset.add(set1)

        val data = LineData(dataset)
        binding.reportingChart.data = data

    }

}
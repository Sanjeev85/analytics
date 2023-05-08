package com.example.analytics

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.example.analytics.models.Result
import com.example.analytics.models.ResultX
import com.example.analytics.models.ResultXXX
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.w3c.dom.Text
import retrofit2.*
import java.io.IOException
import java.net.URL
import java.util.*

class mainScreen : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson
    private lateinit var jsonHandle: String
    private lateinit var profileName: TextView
    private lateinit var tvUserHandle: TextView
    private lateinit var userMaxRating: TextView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_demo)
        // init
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE)
        editor = sharedPref.edit()
        gson = Gson()
        profileName = findViewById(R.id.profileName)
        tvUserHandle = findViewById(R.id.tvUserHandle)
        userMaxRating = findViewById(R.id.userMaxRating)
        image = findViewById(R.id.image)

        findViewById<TextView>(R.id.switchAccount).setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        val json = sharedPref.getString("json", "")
        jsonHandle = sharedPref.getString("userHandle", "").toString()
        val obj = gson.fromJson(json, Result::class.java)

        "$jsonHandle Profile".also { profileName.text = it }
        val dt = intent.getStringExtra("lastOnline")
        findViewById<TextView>(R.id.date).text = dt
        populateCurrentPage(obj)

        // call all api's
        GlobalScope.launch {
            callEverything()
        }
        //upComing Contests
        findViewById<LinearLayoutCompat>(R.id.upcoming_contest).setOnClickListener {
            val intent = Intent(this@mainScreen, contestList::class.java)
            startActivity(intent)
//            finish()
        }

        findViewById<LinearLayoutCompat>(R.id.ContestHistory).setOnClickListener {
            val intent = Intent(this@mainScreen, contestHistory::class.java)
            startActivity(intent)
//            finish()
        }

        findViewById<LinearLayoutCompat>(R.id.submissions).setOnClickListener {
            val intent = Intent(this, Analytics::class.java)
            startActivity(intent)
//            finish()
        }
    }

    private suspend fun callEverything() {
        val status = fetchAllData()
    }

    private suspend fun fetchAllData() {
        Log.e(TAG, "Inside Upcoming Contest")
        val upComingContests = ArrayList<ResultX>()

        // * Upcoming Contest RecyclerView Data Fetch
        withContext(Dispatchers.Main) {
            val api = codeforcesApi.create().getContestList().awaitResponse()
            if (api.isSuccessful) {
                val allContests = api.body()!!.result
                for (contest in allContests) {
                    if (contest.phase == "BEFORE") {
                        upComingContests.add(contest)
                    }
                }
                Log.e("contesst--", upComingContests.toString())
                val contestArray = gson.toJson(upComingContests)
                editor.apply {
                    putString("upcomingContest", contestArray)
                    apply()
                }
            }
        }

        // * Rating Changes Fetch FOr LineChart

        val ratings = arrayListOf<ResultXXX>()
        withContext(Dispatchers.IO) {
            val api = codeforcesApi.create().getRatingChanges(jsonHandle).awaitResponse()
            if (api.isSuccessful) {
                val allRatings = api.body()!!.result
                for (rating in allRatings) {
                    ratings.add(rating)
                }

                val ratingChangesArray = gson.toJson(ratings)
                editor.apply {
                    putString("ratingChanges", ratingChangesArray)
                    apply()
                }
            }
        }

        // * BarCHART FETCH START
        withContext(Dispatchers.IO) {
            val api = codeforcesApi.create().getSolvedProblems(jsonHandle).awaitResponse()
            Log.e("problems *****", api.body()!!.result.indices.toString())
            val hashMap = TreeMap<String, Int>()
            for (i in api.body()!!.result.indices) {
                val tags_ = api.body()!!.result[i].problem.tags
//                Log.e("tags -----", tags_.toString())
                for (ele in tags_) {
                    val prev = hashMap.getOrDefault(ele, 0)
                    hashMap[ele] = prev + 1
                }
//                Log.e("hashmap------", hashMap.toString())
            }
            val ratingWiseCount = TreeMap<Int, Int>()

            for (i in api.body()!!.result.indices) {
                val currSubmission = api.body()!!.result[i]
                if (currSubmission.verdict == "OK") {
                    val problemRating = currSubmission.problem.rating
                    val curr = ratingWiseCount.getOrDefault(problemRating, 0)
                    ratingWiseCount[problemRating] = curr + 1
                }
            }
            Log.e("rating wise", ratingWiseCount.toString())
            val problemRatingCount = gson.toJson(ratingWiseCount)
            editor.apply {
                putString("BarChartData", problemRatingCount)
                apply()
            }
            // * Pie Chart Data Extract From Above BarChart
            var sum = 0
            for (value in hashMap.values) {
                sum += value
            }

            val pie = gson.toJson(hashMap)
            editor.apply {
                putString("PieChartData", pie)
                putString("sum_val", sum.toString())
                apply()
            }
        }
    }

    private fun dlg() {
        val dialog = Dialog(this@mainScreen)
        dialog.apply {
            setContentView(R.layout.animation)
            show()
        }
    }

    private fun getImageFromUrl(src: String): Bitmap? {
        return try {
            val url = URL(src)
            val httpConn = url.openConnection()
            httpConn.apply {
                doInput = true
                connect()
            }
            val inputStream = httpConn.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            bitmap
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("exception", e.message.toString())
            null
        }
    }


    private fun populateCurrentPage(obj: Result) {

        // setImage Resource
        CoroutineScope(Dispatchers.IO).launch {
            val bm = getImageFromUrl(obj.avatar)
            withContext(Dispatchers.Main) {
                image.setImageBitmap(bm)
            }
        }

        findViewById<TextView>(R.id.tvUserHandle).apply {
            text = obj.handle
            setTextColor(getColorByRating(obj.rating))
        }

        tvUserHandle.setTextColor(getColorByRating(obj.maxRating))
        userMaxRating.text = buildString {
            append(obj.maxRank)
            append("( ")
            append(obj.maxRating)
            append(" )")
        }

        userMaxRating.setTextColor(getColorByRating(obj.rating))
    }

}
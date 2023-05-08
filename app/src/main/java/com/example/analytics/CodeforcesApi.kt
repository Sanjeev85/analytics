package com.example.analytics

import com.example.analytics.models.Contests
import com.example.analytics.models.ratingChanges
import com.example.analytics.models.submissions
import com.example.analytics.models.userInfo
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface codeforcesApi {
    @GET("/api/user.info")
    fun getUser(@Query("handles") user_name: String): Call<userInfo>

//    https://codeforces.com/api/user.status?handle=abhi5hekk
    @GET("/api/user.status")
    fun getSolvedProblems(@Query("handle") user_handle: String?) : Call<submissions>

    @GET("/api/contest.list")
    fun getContestList(): Call<Contests>
//    https://codeforces.com/api/contest.list?gym=true

    @GET("/api/user.rating")
    fun getRatingChanges(@Query("handle") user_handle: String?): Call<ratingChanges>

    companion object {
        var BASE_URL = "https://codeforces.com/"

        fun create(): codeforcesApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(codeforcesApi::class.java)
        }
    }
}
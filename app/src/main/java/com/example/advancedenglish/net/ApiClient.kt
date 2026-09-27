package com.example.advancedenglish.net

import android.util.Log
import com.example.advancedenglish.model.ApiInfo
import com.example.advancedenglish.model.Article
import com.example.advancedenglish.model.ArticleType
import com.example.advancedenglish.model.parseArticles
import com.example.advancedenglish.model.parseLexiles
import com.example.advancedenglish.model.parseTypes
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("library/articleTypeList")
    fun articleTypeList(): Call<ApiInfo>

    @GET("appArticle/selectList")
    fun selectList(): Call<ApiInfo>

    @GET("library/articleList")
    fun articleList(
        @Query("lexile") lexile: Int,
        @Query("typeId") typeId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiInfo>
}

object ApiClient {

    private const val BASE = "https://shiqu.zhilehuo.com/advenglish/"
    private const val COOKIE = "sid=KubCdUZ+vb8NgDhKQT9Hle7LAL1+X41tSQRGQn8mJBs="

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("Cookie", COOKIE)
                .header("User-Agent", "EnglishLibrary/1.0 (Android)")
                .header("Accept", "application/json")
                .build()
            chain.proceed(req)
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val service = Retrofit.Builder()
        .baseUrl(BASE)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun fetchArticleTypes(): List<ArticleType> {
        val resp = service.articleTypeList().execute()
        Log.d("ApiClient", "文章列表code为" + resp.code())
        val env = resp.body() ?: throw IOException("HTTP ${resp.code()}")
        return parseTypes(env)
    }

    fun fetchLexiles(): List<Int> {
        val resp = service.selectList().execute()
        Log.d("ApiClient", "返回难度值code为" + resp.code())
        val env = resp.body() ?: throw IOException("HTTP ${resp.code()}")
        return parseLexiles(env)
    }

    fun fetchArticles(lexile: Int, typeId: Int, page: Int, size: Int = 10): List<Article> {
        val resp = service.articleList(lexile, typeId, page, size).execute()
        Log.d("ApiClient", "返回文章code为" + resp.code() + " lexile=" + lexile + " type=" + typeId + " page=" + page)
        val env = resp.body() ?: throw IOException("HTTP ${resp.code()}")
        return parseArticles(env)
    }
}

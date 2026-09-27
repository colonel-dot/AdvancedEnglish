package com.example.advancedenglish.model

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class ArticleType(
    val id: Int = 0,
    @SerializedName("type") val name: String = ""
)

data class Article(
    val id: Int = 0,
    val title: String = "",
    val cover: String? = null,
    @SerializedName("lexile") val difficulity: Int = 0,
    @SerializedName("wordNum") val wordCount: Int = 0,
    val typeId: Int = 0,
    @SerializedName("type") val typeName: String = "",
    @SerializedName("stage") val age: String? = null
)

data class ApiInfo(
    val code: JsonElement? = null,
    val msg: String? = null,
    val data: JsonElement? = null
)

private val gson = Gson()

fun parseTypes(info: ApiInfo): List<ArticleType> {
    val d = info.data
    if (d == null || !d.isJsonArray) return emptyList()
    val out = ArrayList<ArticleType>()
    for (e in d.asJsonArray) {
        if (e.isJsonObject) out.add(gson.fromJson(e, ArticleType::class.java))
    }
    return out
}

fun parseLexiles(info: ApiInfo): List<Int> {
    val d = info.data
    if (d == null || !d.isJsonArray) return emptyList()
    val out = ArrayList<Int>()
    for (e in d.asJsonArray) {
        if (e.isJsonPrimitive && e.asJsonPrimitive.isNumber) out.add(e.asInt)
    }
    return out
}

fun parseArticles(info: ApiInfo): List<Article> {
    val d = info.data
    if (d == null || !d.isJsonObject) return emptyList()
    val list = d.asJsonObject.get("list")
    if (list == null || !list.isJsonArray) return emptyList()
    val out = ArrayList<Article>()
    for (e in list.asJsonArray) {
        if (e.isJsonObject) out.add(gson.fromJson(e, Article::class.java))
    }
    return out
}

package com.example.advancedenglish.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.advancedenglish.model.Article
import com.example.advancedenglish.model.ArticleType
import com.example.advancedenglish.net.ApiClient
import com.example.advancedenglish.ui.ArticleAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<ArticleType>>(emptyList())
    val categories: StateFlow<List<ArticleType>> = _categories.asStateFlow()

    private val _difficulity = MutableStateFlow<List<Int>>(emptyList())
    val difficulity: StateFlow<List<Int>> = _difficulity.asStateFlow()

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()

    private val _refreshing = MutableStateFlow(false)
    val refreshing: StateFlow<Boolean> = _refreshing.asStateFlow()

    private val _footerState = MutableStateFlow(ArticleAdapter.FOOTER_IDLE)
    val footerState: StateFlow<Int> = _footerState.asStateFlow()

    private var currentTypeId = 0
    private var currentDifficulity = 5
    private var page = 1
    private val size = 10
    private var loading = false

    fun start() {
        viewModelScope.launch {
            val fallback = listOf(
                ArticleType(0, "精选阅读"),
                ArticleType(1, "人工智能"),
                ArticleType(2, "科技前沿"),
                ArticleType(3, "历史文化"),
                ArticleType(4, "自然科学"),
                ArticleType(5, "社会百态"),
            )
            try {
                val list = withContext(Dispatchers.IO) { ApiClient.fetchArticleTypes() }
                _categories.value = if (list.isEmpty()) fallback else list
            } catch (e: Exception) {
                Log.e("LibraryVM", "分类加载失败", e)
                _categories.value = fallback
            }
        }
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) { ApiClient.fetchLexiles() }
                _difficulity.value = if (list.isEmpty()) (5..995 step 5).toList() else list
            } catch (e: Exception) {
                Log.e("LibraryVM", "难度加载失败", e)
                _difficulity.value = (5..995 step 5).toList()
            }
        }
        reload()
    }

    fun onCategoryClick(position: Int, type: ArticleType) {
        currentTypeId = if (position == 0) 0 else type.id
        reload()
    }

    fun onDifficulityClick(lexile: Int) {
        currentDifficulity = lexile
        reload()
    }


    fun onLoadMore() {
        if (loading) return
        if (_footerState.value == ArticleAdapter.FOOTER_NO_MORE) return
        loading = true
        page++
        _footerState.value = ArticleAdapter.FOOTER_LOADING
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    ApiClient.fetchArticles(currentDifficulity, currentTypeId, page, size)
                }
                _articles.value = _articles.value + list
                Log.d("LibraryVM", "加载的 page=$page 条数=${list.size}")
                _footerState.value =
                    if (list.size < size) ArticleAdapter.FOOTER_NO_MORE
                    else ArticleAdapter.FOOTER_IDLE
            } catch (e: Exception) {
                page--
                _footerState.value = ArticleAdapter.FOOTER_IDLE
                Log.e("LibraryVM", "加载更多失败", e)
            } finally {
                loading = false
            }
        }
    }

    fun reload() {
        if (loading) return
        loading = true
        page = 1
        _refreshing.value = true
        viewModelScope.launch {
            try {
                val list = withContext(Dispatchers.IO) {
                    ApiClient.fetchArticles(currentDifficulity, currentTypeId, page, size)
                }
                _articles.value = list
                Log.d("LibraryVM", "刷新完成 lexile=$currentDifficulity type=$currentTypeId 条数=${list.size}")
                _footerState.value =
                    if (list.size < size) ArticleAdapter.FOOTER_NO_MORE
                    else ArticleAdapter.FOOTER_IDLE
            } catch (e: Exception) {
                Log.e("LibraryVM", "刷新失败", e)
            } finally {
                loading = false
                _refreshing.value = false
            }
        }
    }
}

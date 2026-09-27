package com.example.advancedenglish

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.advancedenglish.ui.ArticleAdapter
import com.example.advancedenglish.ui.CategoryAdapter
import com.example.advancedenglish.ui.DifficulityAdapter
import com.example.advancedenglish.vm.LibraryViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val vm: LibraryViewModel by viewModels()

    private lateinit var rvCategory: RecyclerView
    private lateinit var rvDifficulity: RecyclerView
    private lateinit var rvArticle: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var difficulityAdapter: DifficulityAdapter
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sb = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sb.left, sb.top, sb.right, sb.bottom)
            insets
        }

        rvCategory = findViewById(R.id.rvCategory)
        rvDifficulity = findViewById(R.id.rvDifficulity)
        rvArticle = findViewById(R.id.rvArticle)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        categoryAdapter = CategoryAdapter { pos, type ->
            vm.onCategoryClick(pos, type)
        }
        rvCategory.layoutManager = LinearLayoutManager(this)
        rvCategory.adapter = categoryAdapter

        difficulityAdapter = DifficulityAdapter { lexile ->
            vm.onDifficulityClick(lexile)
        }
        rvDifficulity.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvDifficulity.adapter = difficulityAdapter

        articleAdapter = ArticleAdapter { article ->
            Log.d("MainActivity", "点击了文章 ${article.title}")
        }
        val articleLayoutManager = LinearLayoutManager(this)
        rvArticle.layoutManager = articleLayoutManager
        rvArticle.adapter = articleAdapter

        swipeRefresh.setColorSchemeResources(R.color.brand_blue, R.color.accent_orange)
        swipeRefresh.setOnRefreshListener { vm.reload() }

        rvArticle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                val last = articleLayoutManager.findLastVisibleItemPosition()
                if (last >= articleAdapter.itemCount - 2) vm.onLoadMore()
            }
        })

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { vm.categories.collect { categoryAdapter.submit(it) } }
                launch { vm.difficulity.collect { difficulityAdapter.submit(it) } }
                launch { vm.articles.collect { articleAdapter.reset(it) } }
                launch { vm.refreshing.collect { swipeRefresh.isRefreshing = it } }
                launch { vm.footerState.collect { articleAdapter.footerState = it } }
            }
        }

        vm.start()
    }
}

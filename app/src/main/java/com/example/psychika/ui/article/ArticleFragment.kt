package com.example.psychika.ui.article

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.psychika.R
import com.example.psychika.adapter.ArticleAdapter
import com.example.psychika.data.entity.Article
import com.example.psychika.databinding.FragmentArticleBinding
import com.example.psychika.ui.article.detail.DetailArticleActivity
import java.util.Locale

class ArticleFragment : Fragment() {
    private lateinit var binding: FragmentArticleBinding
    private val list = ArrayList<Article>()
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(layoutInflater)

        list.addAll(getListArticle())
        showListArticle()

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTitle(s.toString())
            }

            override fun afterTextChanged(s: Editable?) { }
        })

        return binding.root
    }

    private fun filterTitle(query: String?) {
        if (query != null) {
            val filteredArticle = ArrayList<Article>()
            for (i in list) {
                if (i.title.lowercase(Locale.getDefault()).contains(query)) {
                    filteredArticle.add(i)
                }
            }

            if (filteredArticle.isEmpty()) {
                articleAdapter.setFilteredList(filteredArticle)
            } else {
                articleAdapter.setFilteredList(filteredArticle)
            }
        } else {
            articleAdapter.setFilteredList(list)
        }
    }

    private fun showListArticle() {
        val layoutManager = LinearLayoutManager(requireContext())
        articleAdapter = ArticleAdapter(list)

        binding.rvListArticle.apply {
            setLayoutManager(layoutManager)
            adapter = articleAdapter
        }

        articleAdapter.setOnItemClickCallBack(object : ArticleAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Article) {
                showSelectedArticle(data)
            }
        })
    }

    private fun getListArticle(): ArrayList<Article> {
        val dataProfilePic = resources.obtainTypedArray(R.array.article_publisher_profile_pic)
        val dataPublisher = resources.getStringArray(R.array.article_publisher)
        val dataTitle = resources.getStringArray(R.array.article_title)
        val dataDate = resources.getStringArray(R.array.article_date_publish)
        val dataPhoto = resources.obtainTypedArray(R.array.article_photo)
        val dataDesc = resources.getStringArray(R.array.article_desc)

        val listArticle = ArrayList<Article>()
        for (i in dataTitle.indices) {
            val article = Article(
                profilePic = dataProfilePic.getResourceId(i, -1),
                publisher = dataPublisher[i],
                title = dataTitle[i],
                date = dataDate[i],
                photo = dataPhoto.getResourceId(i, -1),
                desc = dataDesc[i]
            )
            listArticle.add(article)
        }
        return listArticle
    }

    private fun showSelectedArticle(data: Article) {
        val moveWithParcelableIntent = Intent(requireContext(), DetailArticleActivity::class.java)
        moveWithParcelableIntent.apply {
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_PUBLISHER, data.publisher)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_TITLE, data.title)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_DATE, data.date)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_PHOTO, data.photo)
            putExtra(DetailArticleActivity.EXTRA_ARTICLE_DESC, data.desc)
        }
        startActivity(moveWithParcelableIntent)
    }
}
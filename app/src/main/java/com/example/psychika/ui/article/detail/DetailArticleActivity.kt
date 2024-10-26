package com.example.psychika.ui.article.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.psychika.databinding.ActivityDetailArticleBinding

class DetailArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvPublisher = intent.extras!!.getString(EXTRA_ARTICLE_PUBLISHER)
        val tvTitle = intent.extras!!.getString(EXTRA_ARTICLE_TITLE)
        val tvDate = intent.extras!!.getString(EXTRA_ARTICLE_DATE)
        val ivPhoto = intent.extras!!.getInt(EXTRA_ARTICLE_PHOTO, -1)
        val tvDesc = intent.extras!!.getString(EXTRA_ARTICLE_DESC)

        binding.apply {
            ivBackButton.setOnClickListener { finish() }
            tvArticlePublisher.text = tvPublisher
            tvArticleTitle.text = tvTitle
            tvArticleDate.text = tvDate
            ivArticlePicture.setImageResource(ivPhoto)
            tvArticleDesc.text = tvDesc
        }
    }

    companion object {
        const val EXTRA_ARTICLE_PUBLISHER = "extra_article_publisher"
        const val EXTRA_ARTICLE_TITLE = "extra_article_title"
        const val EXTRA_ARTICLE_DATE = "extra_article_date"
        const val EXTRA_ARTICLE_PHOTO = "extra_article_photo"
        const val EXTRA_ARTICLE_DESC = "extra_article_desc"
    }
}
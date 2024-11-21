package com.example.psychika

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.example.psychika.adapter.ArticleAdapter
import com.example.psychika.data.entity.Article
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull

class ArticleAdapterTest {

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleList: ArrayList<Article>
    private lateinit var context: Context
    private var clickedArticle: Article? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        articleList = arrayListOf(
            Article(
                profilePic = R.drawable.profile_picture,
                publisher = "Publisher1",
                title = "Title1",
                date = "Date1",
                photo = R.drawable.article_photo_01,
                desc = "Description1"
            ),
            Article(
                profilePic = R.drawable.profile_picture,
                publisher = "Publisher2",
                title = "Title2",
                date = "Date2",
                photo = R.drawable.article_photo_02,
                desc = "Description2"
            )
        )
        articleAdapter = ArticleAdapter(articleList)

        articleAdapter.setOnItemClickCallBack(object : ArticleAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Article) {
                clickedArticle = data
            }
        })
    }

    @Test
    fun testGetItemCount() {
        assertThat(articleAdapter.itemCount, `is`(2))
    }

    @Test
    fun testSetFilteredList() {
        assertThat(articleAdapter.listArticle.size, `is`(2))
        assertThat(articleAdapter.listArticle[0].title, `is`("Title1"))

        val filteredList = arrayListOf(
            Article(
                profilePic = R.drawable.profile_picture,
                publisher = "Publisher3",
                title = "Title3",
                date = "Date3",
                photo = R.drawable.article_photo_03,
                desc = "Description3"
            )
        )

        articleAdapter.setFilteredList(filteredList)

        assertThat(articleAdapter.listArticle.size, `is`(1))
        assertThat(articleAdapter.listArticle[0].title, `is`("Title3"))
    }

    @Test
    fun testOnBindViewHolder() {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_row_article, null)

        val holder = ArticleAdapter.ViewHolder(itemView)

        articleAdapter.onBindViewHolder(holder, 0)

        val tvPublisher = holder.itemView.findViewById<TextView>(R.id.tv_publisher)
        val tvTitle = holder.itemView.findViewById<TextView>(R.id.tv_title)
        val tvDate = holder.itemView.findViewById<TextView>(R.id.tv_date)
        val ivPhoto = holder.itemView.findViewById<ImageView>(R.id.iv_article_picture)

        assertNotNull(tvPublisher)
        assertThat(tvPublisher.text.toString(), `is`("Publisher1"))

        assertNotNull(tvTitle)
        assertThat(tvTitle.text.toString(), `is`("Title1"))

        assertNotNull(tvDate)
        assertThat(tvDate.text.toString(), `is`("Date1"))

        assertNotNull(ivPhoto)
        assertThat(ivPhoto.drawable.constantState, `is`(itemView.context.getDrawable(R.drawable.article_photo_01)?.constantState))
    }

    @Test
    fun testSetOnItemClickCallBack() {
        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = articleAdapter

        var clickedArticle: Article? = null
        articleAdapter.setOnItemClickCallBack(object : ArticleAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: Article) {
                clickedArticle = data
            }
        })

        recyclerView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)

        val viewHolder = recyclerView.findViewHolderForAdapterPosition(0) as? ArticleAdapter.ViewHolder
        viewHolder?.itemView?.performClick()

        assertNotNull(clickedArticle)
        assertThat(clickedArticle?.title, `is`("Title1"))
        assertThat(clickedArticle?.publisher, `is`("Publisher1"))
    }

}

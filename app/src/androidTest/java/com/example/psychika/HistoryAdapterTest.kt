package com.example.psychika

import android.content.Context
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.example.psychika.adapter.HistoryAdapter
import com.example.psychika.data.entity.DailyAveragePrediction
import com.example.psychika.databinding.ItemRowHistoryBinding
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat

class HistoryAdapterTest {
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var historyList: ArrayList<DailyAveragePrediction>
    private lateinit var context: Context

    private var itemClickCalled = false
    private var clickedItem: DailyAveragePrediction? = null

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        historyList = arrayListOf(
            DailyAveragePrediction(
                date = "2024-11-01",
                averagePredict = 23.5
            ),
            DailyAveragePrediction(
                date = "2024-11-02",
                averagePredict = 21.0
            )
        )
        historyAdapter = HistoryAdapter()

        historyAdapter.setOriginalList(historyList)

        historyAdapter.setOnItemClickCallBack(object : HistoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: DailyAveragePrediction) {
                itemClickCalled = true
                clickedItem = data
            }
        })
    }

    @Test
    fun testSetOriginalList() {
        assertThat(historyAdapter.itemCount, `is`(2))

        assertThat(historyAdapter.currentList[0].date, `is`("2024-11-01"))
        assertThat(historyAdapter.currentList[0].averagePredict, `is`(23.5))

        assertThat(historyAdapter.currentList[1].date, `is`("2024-11-02"))
        assertThat(historyAdapter.currentList[1].averagePredict, `is`(21.0))
    }

    @Test
    fun testOnBindViewHolder() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val inflater = LayoutInflater.from(context)
        val binding = ItemRowHistoryBinding.inflate(inflater)

        val viewHolder = historyAdapter.ViewHolder(binding)

        val position = 0
        historyAdapter.onBindViewHolder(viewHolder, position)

        val percentageText = viewHolder.binding.tvPredict.text.toString()
        val percentageValue = percentageText.replace("%", "").toDouble() / 100

        val formattedPercentage = String.format("%.2f%%", percentageValue)

        assertThat(viewHolder.binding.tvDate.text.toString(), `is`("2024-11-01"))
        assertThat(formattedPercentage, `is`("23.50%"))
    }

    @Test
    fun testSetOnItemClickCallBack() {
        var itemClickCalled = false
        var clickedItem: DailyAveragePrediction? = null

        historyAdapter.setOnItemClickCallBack(object : HistoryAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: DailyAveragePrediction) {
                itemClickCalled = true
                clickedItem = data
            }
        })

        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(context))
        val viewHolder = historyAdapter.ViewHolder(binding)
        historyAdapter.onBindViewHolder(viewHolder, 0)
        viewHolder.itemView.performClick()

        assertThat(itemClickCalled, `is`(true))
        assertThat(clickedItem?.date, `is`("2024-11-01"))
        assertThat(clickedItem?.averagePredict, `is`(23.5))
    }

    @Test
    fun testFilterByDate() {
        val originalList = arrayListOf(
            DailyAveragePrediction(date = "2024-11-01", averagePredict = 23.5),
            DailyAveragePrediction(date = "2024-11-02", averagePredict = 21.0),
            DailyAveragePrediction(date = "2024-11-03", averagePredict = 20.0)
        )

        historyAdapter.setOriginalList(originalList)

        val filterDate = "2024-11-02"
        historyAdapter.filterByDate(filterDate)

        Thread.sleep(1000)

        assertThat(historyAdapter.currentList.size, `is`(1))

        assertThat(historyAdapter.currentList[0].date, `is`("2024-11-02"))
        assertThat(historyAdapter.currentList[0].averagePredict, `is`(21.0))

        val invalidDate = "2024-11-05"
        historyAdapter.filterByDate(invalidDate)

        Thread.sleep(1000)

        assertThat(historyAdapter.currentList.size, `is`(0))
    }



    //    @Test
//    fun testOnBindViewHolder_setOnItemClickCallback() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        val inflater = LayoutInflater.from(context)
//        val binding = ItemRowHistoryBinding.inflate(inflater)
//
//        val viewHolder = historyAdapter.ViewHolder(binding)
//
//        val position = 0
//        historyAdapter.onBindViewHolder(viewHolder, position)
//
//        viewHolder.itemView.performClick()
//
//        assertThat(itemClickCalled, `is`(true))
//        assertThat(clickedItem.date, `is`("2024-11-01"))
//        assertThat(clickedItem.averagePredict, `is`(23.5))
//    }
}
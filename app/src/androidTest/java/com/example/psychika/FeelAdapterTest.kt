package com.example.psychika

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.example.psychika.adapter.FeelAdapter
import com.example.psychika.data.entity.Feel
import com.example.psychika.data.local.preference.feel.FeelPreference
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class FeelAdapterTest {

    private lateinit var context: Context
    private lateinit var feelAdapter: FeelAdapter
    private lateinit var listFeel: MutableList<Feel>
    private lateinit var feelPreference: FeelPreference

    @Before
    fun setUp() {
        // Initialize context
        context = ApplicationProvider.getApplicationContext()

        // Initialize FeelPreference
        feelPreference = FeelPreference(context)

        // Initialize listFeel
        listFeel = mutableListOf(
            Feel(photo = R.drawable.ic_heart, desc = "Happy", isSelected = false),
            Feel(photo = R.drawable.ic_heart, desc = "Sad", isSelected = false)
        )

        val feelList = ArrayList(listFeel)  // Convert MutableList to ArrayList
        feelAdapter = FeelAdapter(feelList, feelPreference)
    }

    @Test
    fun testGetItemCount() {
        assertThat(feelAdapter.itemCount, `is`(listFeel.size))
    }

    @Test
    fun testOnBindViewHolder() {
        val parent = FrameLayout(context)

        // Create ViewHolders
        val firstViewHolder = feelAdapter.onCreateViewHolder(parent, 0)
        val secondViewHolder = feelAdapter.onCreateViewHolder(parent, 1)

        // Bind first item
        feelAdapter.onBindViewHolder(firstViewHolder, 0)
        val ivFeel1 = firstViewHolder.itemView.findViewById<ImageView>(R.id.iv_feel)
        val tvDesc1 = firstViewHolder.itemView.findViewById<TextView>(R.id.tv_desc_feel)
        val boxLayout1 = firstViewHolder.itemView.findViewById<View>(R.id.box_layout)

        // Debug: Print information about the first item
        val firstFeel = listFeel[0]
        println("First item description: ${firstFeel.desc}")
        println("Expected drawable resource: ${firstFeel.photo}")
        println("Actual drawable resource: ${ivFeel1.drawable?.mutate()?.constantState}")
        println("First item selection state: ${firstFeel.isSelected}")

        // Assert that views are populated correctly
        assertThat(ivFeel1.drawable?.mutate()?.constantState?.equals(context.getDrawable(firstFeel.photo)?.constantState), `is`(true)) // Compare drawable state

        // Ensure the description is correct
        assertThat(tvDesc1.text.toString(), `is`(firstFeel.desc))

        // Assert that the background color is as expected
        assertThat(
            boxLayout1.background.constantState,
            `is`(context.getDrawable(R.color.primary_50)?.constantState)
        )

        // Handle selection for first item
        firstViewHolder.itemView.performClick()

        // Debug: Print selection state after click
        println("First item selection state after click: ${firstFeel.isSelected}")
        println("Second item selection state after click: ${listFeel[1].isSelected}")

        // Assert that the first item is selected and the second item is not
        assertThat(firstFeel.isSelected, `is`(true))
        val secondFeel = listFeel[1]
        assertThat(secondFeel.isSelected, `is`(false))

        // Bind second item
        feelAdapter.onBindViewHolder(secondViewHolder, 1)
        val ivFeel2 = secondViewHolder.itemView.findViewById<ImageView>(R.id.iv_feel)
        val tvDesc2 = secondViewHolder.itemView.findViewById<TextView>(R.id.tv_desc_feel)
        val boxLayout2 = secondViewHolder.itemView.findViewById<View>(R.id.box_layout)

        // Debug: Print information about the second item
        println("Second item description: ${secondFeel.desc}")
        println("Expected drawable resource: ${secondFeel.photo}")
        println("Actual drawable resource: ${ivFeel2.drawable?.mutate()?.constantState}")
        println("Second item selection state before click: ${secondFeel.isSelected}")

        // Assert that the second item views are populated correctly
        assertThat(ivFeel2.drawable?.mutate()?.constantState?.equals(context.getDrawable(secondFeel.photo)?.constantState), `is`(true)) // Check drawable state
        assertThat(tvDesc2.text.toString(), `is`(secondFeel.desc))
        assertThat(
            boxLayout2.background.constantState,
            `is`(context.getDrawable(R.color.primary_50)?.constantState)
        )

        // Handle selection for second item
        secondViewHolder.itemView.performClick()

        // Debug: Print selection state after click
        println("First item selection state after second click: ${firstFeel.isSelected}")
        println("Second item selection state after second click: ${secondFeel.isSelected}")

        // Assert that the second item is selected and the first item is deselected
        assertThat(firstFeel.isSelected, `is`(false))
        assertThat(secondFeel.isSelected, `is`(true))
    }
}

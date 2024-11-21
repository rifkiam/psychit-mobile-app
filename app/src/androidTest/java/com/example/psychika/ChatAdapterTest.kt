package com.example.psychika

import android.content.Context
import android.view.LayoutInflater
import androidx.test.core.app.ApplicationProvider
import com.example.psychika.adapter.ChatAdapter
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.databinding.*
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ChatAdapterTest {

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatMessages: MutableList<ChatMessage>
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        chatMessages = mutableListOf(
            ChatMessage(role = "user", content = "User message"),
            ChatMessage(role = "assistant", content = "Bot message"),
            ChatMessage(role = "error", content = "Error message"),
            ChatMessage(role = "loading", content = "Loading...")
        )
        chatAdapter = ChatAdapter(chatMessages)
    }

    @Test
    fun testOnBindViewHolder() {
        for ((index, message) in chatMessages.withIndex()) {
            when (message.role) {
                "user" -> {
                    val binding = ItemUserChatBinding.inflate(LayoutInflater.from(context))
                    val viewHolder = chatAdapter.UserChatViewHolder(binding)
                    chatAdapter.onBindViewHolder(viewHolder, index)
                    assertThat(binding.tvMessage.text.toString(), `is`(message.content))
                }
                "assistant" -> {
                    val binding = ItemBotChatBinding.inflate(LayoutInflater.from(context))
                    val viewHolder = chatAdapter.BotChatViewHolder(binding)
                    chatAdapter.onBindViewHolder(viewHolder, index)
                    assertThat(binding.tvMessage.text.toString(), `is`(message.content))
                }
                "loading" -> {
                    val binding = ItemChatLoadingBinding.inflate(LayoutInflater.from(context))
                    val viewHolder = chatAdapter.ChatLoadingViewHolder(binding)
                    chatAdapter.onBindViewHolder(viewHolder, index)
                }
                "error" -> {
                    val binding = ItemBotErrorBinding.inflate(LayoutInflater.from(context))
                    val viewHolder = chatAdapter.BotErrorViewHolder(binding)
                    chatAdapter.onBindViewHolder(viewHolder, index)
                    assertThat(binding.tvMessage.text.toString(), `is`(message.content))
                }
            }
        }
    }

    @Test
    fun testGetItemCount() {
        assertThat(chatAdapter.itemCount, `is`(4))

        chatAdapter.addChatMessage(ChatMessage("New user message", "user"))

        assertThat(chatAdapter.itemCount, `is`(5))
    }

    @Test
    fun testAddChatMessage() {
        assertThat(chatAdapter.itemCount, `is`(4))

        val newMessage = ChatMessage(role = "user", content = "New user message")
        chatAdapter.addChatMessage(newMessage)

        assertThat(chatAdapter.itemCount, `is`(5))

        val lastMessage = chatAdapter.chatMessages.last()
        assertThat(lastMessage.content, `is`("New user message"))
        assertThat(lastMessage.role, `is`("user"))
    }

    @Test
    fun testUpdateChatMessages() {
        assertThat(chatAdapter.itemCount, `is`(4))

        val newMessages = listOf(
            ChatMessage(role = "user", content = "Updated user message"),
            ChatMessage(role = "assistant", content = "Updated bot message")
        )
        chatAdapter.updateChatMessages(newMessages)

        assertThat(chatAdapter.itemCount, `is`(2))

        assertThat(chatAdapter.chatMessages.size, `is`(2))
        assertThat(chatAdapter.chatMessages[0].content, `is`("Updated user message"))
        assertThat(chatAdapter.chatMessages[1].content, `is`("Updated bot message"))
    }

    @Test
    fun testRemoveLoadingMessage() {
        assertThat(chatAdapter.itemCount, `is`(4))
        assertThat(chatAdapter.chatMessages.last().role, `is`("loading"))

        chatAdapter.removeLoadingMessage()

        assertThat(chatAdapter.itemCount, `is`(3))

        assertThat(chatAdapter.chatMessages.any { it.role == "loading" }, `is`(false))
    }

    @Test
    fun testGetItemViewType() {
        val expectedViewTypes = listOf(
            R.layout.item_user_chat,     // "user"
            R.layout.item_bot_chat,      // "assistant"
            R.layout.item_bot_error,      // "error"
            R.layout.item_chat_loading  // "loading"
        )

        for (i in chatMessages.indices) {
            val actualViewType = chatAdapter.getItemViewType(i)
            assertThat(actualViewType, `is`(expectedViewTypes[i]))
        }
    }
}

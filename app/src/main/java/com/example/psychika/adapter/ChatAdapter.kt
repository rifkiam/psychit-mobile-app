package com.example.psychika.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.psychika.R
import com.example.psychika.data.entity.ChatMessage
import com.example.psychika.databinding.ItemBotChatBinding
import com.example.psychika.databinding.ItemBotErrorBinding
import com.example.psychika.databinding.ItemUserChatBinding
import com.example.psychika.databinding.ItemChatLoadingBinding

class ChatAdapter(private var chatMessages: MutableList<ChatMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class UserChatViewHolder(private val binding: ItemUserChatBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind (chatMessage: ChatMessage) {
            binding.apply {
                tvMessage.text = chatMessage.content
                tvTime.text = chatMessage.time
            }
        }
    }

    inner class BotChatViewHolder(private val binding: ItemBotChatBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind (chatMessage: ChatMessage) {
            binding.apply {
                tvMessage.text = chatMessage.content
                tvTime.text = chatMessage.time
            }
        }
    }

    inner class BotErrorViewHolder(private val binding: ItemBotErrorBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bind (chatMessage: ChatMessage) {
            binding.apply {
                tvMessage.text = chatMessage.content
                tvTime.text = chatMessage.time
            }
        }
    }

    inner class ChatLoadingViewHolder(binding: ItemChatLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        val role = chatMessages[position].role
        return when (role) {
            "user" -> R.layout.item_user_chat
            "assistant" -> R.layout.item_bot_chat
            "loading" -> R.layout.item_chat_loading
            else -> R.layout.item_bot_error
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_user_chat -> {
                val binding = ItemUserChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                UserChatViewHolder(binding)
            }
            R.layout.item_bot_chat -> {
                val binding = ItemBotChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BotChatViewHolder(binding)
            }
            R.layout.item_chat_loading -> {
                val binding = ItemChatLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChatLoadingViewHolder(binding)
            }
            else -> {
                val binding = ItemBotErrorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BotErrorViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = chatMessages[position]
        when (holder) {
            is UserChatViewHolder -> holder.bind(chatMessage)
            is BotChatViewHolder -> holder.bind(chatMessage)
            is BotErrorViewHolder -> holder.bind(chatMessage)
        }
    }

    override fun getItemCount(): Int = chatMessages.size

    fun addChatMessage(message: ChatMessage) {
        chatMessages.add(message)
        notifyItemInserted(chatMessages.size - 1)
    }

    fun removeLoadingMessage() {
        chatMessages.removeAt(chatMessages.size - 1)
        notifyItemRemoved(chatMessages.size)
    }

    fun updateChatMessages(messages: List<ChatMessage>) {
        chatMessages.clear()
        chatMessages.addAll(messages)
        notifyDataSetChanged()
    }
}

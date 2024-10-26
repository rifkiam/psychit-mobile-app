package com.example.psychika.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.psychika.R
import com.example.psychika.data.entity.Feel
import com.example.psychika.data.local.preference.feel.FeelPreference

class FeelAdapter(private val listFeel: ArrayList<Feel>, private val feelPreference: FeelPreference) : RecyclerView.Adapter<FeelAdapter.ViewHolder>() {
    private var selectedItemPosition: Int = RecyclerView.NO_POSITION
    private var initialSelected: Boolean = feelPreference.isInitialSelected()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFeel: ImageView = itemView.findViewById(R.id.iv_feel)
        val tvDesc: TextView = itemView.findViewById(R.id.tv_desc_feel)
        val boxLayout: LinearLayout = itemView.findViewById(R.id.box_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_feel, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listFeel.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feel = listFeel[position]

        holder.ivFeel.setImageResource(feel.photo)
        holder.tvDesc.text = feel.desc

        if (feel.isSelected) {
            holder.boxLayout.setBackgroundResource(R.color.primary_500)
        } else {
            holder.boxLayout.setBackgroundResource(R.color.primary_50)
        }

        if (initialSelected) {
            holder.itemView.setOnClickListener(null)
        } else {
            holder.itemView.setOnClickListener {
                val previousSelectedItemPosition = selectedItemPosition
                selectedItemPosition = holder.adapterPosition

                if (previousSelectedItemPosition != RecyclerView.NO_POSITION) {
                    val previousSelectedFeel = listFeel[previousSelectedItemPosition]
                    previousSelectedFeel.isSelected = false
                    feelPreference.setFeel(previousSelectedFeel, previousSelectedItemPosition)
                    notifyItemChanged(previousSelectedItemPosition)
                }

                val selectedFeel = listFeel[selectedItemPosition]
                selectedFeel.isSelected = true
                feelPreference.setFeel(selectedFeel, selectedItemPosition)
                notifyItemChanged(selectedItemPosition)
            }
        }
    }
}


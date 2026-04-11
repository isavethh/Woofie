package com.example.woofie.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.woofie.R
import com.example.woofie.data.RoleplayMessage
import com.google.android.material.card.MaterialCardView

class RoleplayChatAdapter : RecyclerView.Adapter<RoleplayChatAdapter.ChatViewHolder>() {

    private val messages = mutableListOf<RoleplayMessage>()

    fun addMessage(message: RoleplayMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardWoofie = itemView.findViewById<MaterialCardView>(R.id.cardWoofie)
        private val textWoofie = itemView.findViewById<TextView>(R.id.textWoofie)
        private val cardUser = itemView.findViewById<MaterialCardView>(R.id.cardUser)
        private val textUser = itemView.findViewById<TextView>(R.id.textUser)

        fun bind(msg: RoleplayMessage) {
            if (msg.isFromWoofie) {
                cardWoofie.visibility = View.VISIBLE
                textWoofie.text = msg.text
                cardUser.visibility = View.GONE
            } else {
                cardUser.visibility = View.VISIBLE
                textUser.text = msg.text
                cardWoofie.visibility = View.GONE
            }
        }
    }
}

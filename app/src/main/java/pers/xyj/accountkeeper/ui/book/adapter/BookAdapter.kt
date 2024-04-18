package pers.xyj.accountkeeper.ui.book.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextClock
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.BookVo
import java.text.SimpleDateFormat

class BookAdapter(val data: ArrayList<BookVo>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bookName: TextView = view.findViewById(R.id.book_name)
        var memberCount: TextView = view.findViewById(R.id.member_count)
        var description: TextView = view.findViewById(R.id.description_text)
        var createTime: TextView = view.findViewById(R.id.create_time)
        var amount: TextView = view.findViewById(R.id.amount)
        var settingButton: Button = view.findViewById(R.id.setting_button)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onSettingButtonClick(position: Int)

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        val viewHolder: BookViewHolder = BookViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        viewHolder.settingButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onSettingButtonClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book: BookVo = data[position]
        holder.bookName.text = book.name
        holder.memberCount.text = book.memberCount.toString()
        holder.description.text = book.description
        var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        holder.createTime.text = format.format(book.createTime)
        holder.amount.text = String.format("%.2f", book.amount)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, book: BookVo) {
        data.add(position, book)
        notifyItemInserted(position)
    }
}
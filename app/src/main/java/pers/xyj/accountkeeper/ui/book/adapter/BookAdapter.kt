package pers.xyj.accountkeeper.ui.book.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.setruth.doublewillow.utils.SPUtil
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.UserInfo
import pers.xyj.accountkeeper.util.appContext
import java.text.SimpleDateFormat

class BookAdapter(val data: ArrayList<BookVo>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    val spUtil = SPUtil(appContext)
    val userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
    val userId: Long = userInfo.id

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bookContainer: LinearLayout = view.findViewById(R.id.book_container)
        var bookName: TextView = view.findViewById(R.id.book_name)
        var memberCount: TextView = view.findViewById(R.id.member_count)
        var description: TextView = view.findViewById(R.id.description_text)
        var createTime: TextView = view.findViewById(R.id.create_time)
        var amount: TextView = view.findViewById(R.id.amount)
        var amountLabel: TextView = view.findViewById(R.id.amount_label)
        var settingButton: Button = view.findViewById(R.id.setting_button)
        var reportButton: Button = view.findViewById(R.id.report_button)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onSettingButtonClick(position: Int)
        fun onReportButtonClick(position: Int)

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
        viewHolder.reportButton.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onReportButtonClick(position)
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
        var amountResult:Double = book.incomeAmount - book.outcomeAmount
        if (amountResult >= 0){
            holder.amountLabel.text = "账本盈余（元）："
            holder.amountLabel.setTextColor(Color.parseColor("#0EA06F"))
            holder.amount.setTextColor(Color.parseColor("#0EA06F"))
            holder.amount.text = String.format("%.2f", amountResult)
        }else{
            holder.amountLabel.text = "账本亏损（元）："
            holder.amount.text = String.format("%.2f", -amountResult)
            holder.amountLabel.setTextColor(Color.RED)
            holder.amount.setTextColor(Color.RED)
        }
        if (book.createBy != userId) {
            holder.bookContainer.setBackgroundColor(Color.parseColor("#F3FEB0"))
        }else{
            holder.bookContainer.setBackgroundColor(Color.parseColor("#F0F8FA"))
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, book: BookVo) {
        data.add(position, book)
        notifyItemInserted(position)
    }
}
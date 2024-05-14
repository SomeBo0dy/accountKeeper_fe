package pers.xyj.accountkeeper.ui.smartKeep.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.setruth.doublewillow.utils.SPUtil
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.BookVo
import pers.xyj.accountkeeper.repository.model.UserInfo
import pers.xyj.accountkeeper.util.appContext
import java.text.SimpleDateFormat

class SelectBookAdapter (val data: ArrayList<BookVo>) :
    RecyclerView.Adapter<SelectBookAdapter.SelectBookViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    val spUtil = SPUtil(appContext)
    val userInfo = spUtil.getObjectItem("userInfo", UserInfo()) as UserInfo
    val userId: Long = userInfo.id
    var mPosition: Int = -1
    inner class SelectBookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var selectBookItem: LinearLayout = view.findViewById(R.id.select_book_item)
        var selectBookName: TextView = view.findViewById(R.id.select_book_name)
        var selectMemberCount: TextView = view.findViewById(R.id.select_member_count)
        var selectDescription: TextView = view.findViewById(R.id.select_description_text)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectBookViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.select_book_item, parent, false)
        val viewHolder: SelectBookViewHolder = SelectBookViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
            mPosition = position
            notifyDataSetChanged()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SelectBookViewHolder, position: Int) {
        val book: BookVo = data[position]
        holder.selectBookName.text = book.name
        holder.selectMemberCount.text = book.memberCount.toString()
        holder.selectDescription.text = book.description
        if (mPosition != position){
            holder.selectBookItem.setBackgroundColor(Color.parseColor("#F0F8FA"))
        }else{
            holder.selectBookItem.setBackgroundColor(Color.parseColor("#F3FEB0"))
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
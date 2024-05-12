package pers.xyj.accountkeeper.ui.bill.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.Bill
import pers.xyj.accountkeeper.repository.entity.Record
import pers.xyj.accountkeeper.ui.record.adapter.RecordAdapter
import pers.xyj.accountkeeper.util.LogUtil
import java.text.SimpleDateFormat

class BillAdapter (val data: ArrayList<Bill>) :
    RecyclerView.Adapter<BillAdapter.BillViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    var format: SimpleDateFormat = SimpleDateFormat("HH:mm")

    inner class BillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var billName: TextView = view.findViewById(R.id.bill_name)
        var dateTime: TextView = view.findViewById(R.id.date_text)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.bill_item, parent, false)
        val viewHolder: BillViewHolder = BillViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        val bill: Bill = data[position]
        holder.billName.text = bill.billName
        holder.dateTime.text = format.format(bill.reminderTime)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, bill: Bill) {
        data.add(position, bill)
        notifyItemInserted(position)
    }
}
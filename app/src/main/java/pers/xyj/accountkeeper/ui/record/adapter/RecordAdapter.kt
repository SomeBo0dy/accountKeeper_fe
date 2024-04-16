package pers.xyj.accountkeeper.ui.record.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.Record

class RecordAdapter(val data: ArrayList<Record>) :
    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    inner class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var typeImage: ImageView = view.findViewById(R.id.typeImage)
        var typeText: TextView = view.findViewById(R.id.typeText)
        var amount: TextView = view.findViewById(R.id.record_amount)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        val viewHolder: RecordViewHolder = RecordViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record: Record = data[position]
        Picasso.get().load(record.imgUrl).into(holder.typeImage)
        holder.typeText.text = record.description
        holder.amount.text = String.format("%.2f", record.amount)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, record: Record) {
        data.add(position, record)
        notifyItemInserted(position)
    }
}
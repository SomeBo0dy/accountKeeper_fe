package pers.xyj.accountkeeper.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.model.TypeNameAndCountVo

class StatisticItemAdapter(val data: ArrayList<TypeNameAndCountVo>,val isIncome :Boolean) :
    RecyclerView.Adapter<StatisticItemAdapter.StatisticItemViewHolder>() {

    private lateinit var mListener: OnItemClickListener
//    var totalAmount: Double = 1.0
    inner class StatisticItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: TextView = view.findViewById(R.id.statistic_name)
        var itemCount: TextView = view.findViewById(R.id.statistic_count)
        var itemAmount: TextView = view.findViewById(R.id.statistic_amount)
        var recordTypeText: TextView = view.findViewById(R.id.record_type_text)
//        var percentage: TextView = view.findViewById(R.id.statistic_percentage)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticItemViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.statistics_item, parent, false)
        val viewHolder: StatisticItemViewHolder = StatisticItemViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: StatisticItemViewHolder, position: Int) {
        val typeNameAndCount: TypeNameAndCountVo = data[position]
        holder.itemName.text = typeNameAndCount.typeName
        holder.itemCount.text = typeNameAndCount.count.toString()
        if (isIncome){
            holder.recordTypeText.text = "收入"
        }else{
            holder.recordTypeText.text = "支出"
        }
        holder.itemAmount.text = String.format("%.2f", typeNameAndCount.amount)
//        holder.percentage.text = String.format("%.1f", typeNameAndCount.amount/totalAmount * 100)
    }

    override fun getItemCount(): Int {
        return data.size
    }

//    fun setTotalAmount(total: Double) {
//        totalAmount = total
//    }
}
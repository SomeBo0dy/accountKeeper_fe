package pers.xyj.accountkeeper.ui.smartKeep.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.model.SmartKeepRecordVo
import java.text.SimpleDateFormat

class SmartResultAdapter(val data: ArrayList<SmartKeepRecordVo>) :
    RecyclerView.Adapter<SmartResultAdapter.SmartResultViewHolder>() {

    private lateinit var mListener: OnItemClickListener
    private var format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    inner class SmartResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var smartRecordDescription: TextView = view.findViewById(R.id.smart_record_description)
        var smartRecordAmount: TextView = view.findViewById(R.id.smart_record_amount)
        var smartRecordCreateDate: TextView = view.findViewById(R.id.smart_record_create_date)
        var removeSmartRecordButton: Button = view.findViewById(R.id.remove_smart_record_button)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartResultViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.smart_record_item, parent, false)
        val viewHolder: SmartResultViewHolder = SmartResultViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SmartResultViewHolder, position: Int) {
        val smartRecord: SmartKeepRecordVo = data[position]
        var description = smartRecord.description
        if (description == ""){
            description = "未识别到账单条目名称"
        }
        holder.smartRecordDescription.text = description
        holder.smartRecordAmount.text = String.format("%.2f", smartRecord.amount)
        holder.smartRecordCreateDate.text = format.format(smartRecord.createDate)
        holder.removeSmartRecordButton.setOnClickListener {
            data.remove(smartRecord)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
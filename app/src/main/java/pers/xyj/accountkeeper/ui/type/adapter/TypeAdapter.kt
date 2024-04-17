package pers.xyj.accountkeeper.ui.type.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.Type

class TypeAdapter(val data: ArrayList<Type>) :
    RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    inner class TypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: CheckBox = view.findViewById(R.id.type_check_box)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.type_item, parent, false)
        val viewHolder: TypeViewHolder = TypeViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        val type: Type = data[position]
        holder.checkBox.tag = type.id
        holder.checkBox.text = type.name
        holder.checkBox.isChecked = type.isChecked
        var checkBox = holder.checkBox
        checkBox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            // 当有一个单选框被选中时，将其他的单选框设为未选中状态
            if (isChecked) {
                if (buttonView == checkBox){
                    for (item in data){
                        item.isChecked = false
                    }
                    type.isChecked = true
                    notifyDataSetChanged()
                }
            }
        }
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, type: Type) {
        data.add(position, type)
        notifyItemInserted(position)
    }
}
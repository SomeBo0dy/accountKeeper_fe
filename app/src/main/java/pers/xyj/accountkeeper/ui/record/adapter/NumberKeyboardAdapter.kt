package pers.xyj.accountkeeper.ui.record.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pers.xyj.accountkeeper.R
import pers.xyj.accountkeeper.repository.entity.Record

class NumberKeyboardAdapter(val data: ArrayList<String>) :
    RecyclerView.Adapter<NumberKeyboardAdapter.NumberKeyboardViewHolder>() {

    private lateinit var mListener: OnItemClickListener

    inner class NumberKeyboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var typeImage: ImageView = view.findViewById(R.id.typeImage)

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberKeyboardViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.number_keyboard_item, parent, false)
        val viewHolder: NumberKeyboardViewHolder = NumberKeyboardViewHolder(itemView)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            mListener.onItemClick(position)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NumberKeyboardViewHolder, position: Int) {
        val key: String = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun add(position: Int, record: String) {
        data.add(position, record)
        notifyItemInserted(position)
    }
}
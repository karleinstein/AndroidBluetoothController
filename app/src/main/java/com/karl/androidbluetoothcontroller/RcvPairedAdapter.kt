package com.karl.androidbluetoothcontroller

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_paired_device.view.*

class RcvPairedAdapter(context: Context, private val listPairedDevices: ArrayList<Model>)
    : RecyclerView.Adapter<RcvPairedAdapter.ViewHolder>() {
    private var onItemClickListener: OnItemClickListener? = null
    private val inflater = LayoutInflater.from(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.item_paired_device, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listPairedDevices.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = listPairedDevices[position].nameDevice
        holder.itemView.setOnClickListener {
            onItemClickListener!!.OnItemClicked(position)
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    interface OnItemClickListener {
        fun OnItemClicked(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.txtName
    }

}
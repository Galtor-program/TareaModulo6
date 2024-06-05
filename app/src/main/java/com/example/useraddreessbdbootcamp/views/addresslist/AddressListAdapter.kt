package com.example.useraddreessbdbootcamp.views.addresslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.useraddreessbdbootcamp.R
import com.example.useraddreessbdbootcamp.entities.Address
import com.example.useraddreessbdbootcamp.entities.User

class AddressListAdapter(private val onItemClick: (Address) -> Unit) :
    ListAdapter<Address, AddressListAdapter.AddressViewHolder>(AddressComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        return AddressViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentAddress = getItem(position)
        holder.bind(currentAddress)
    }

    class AddressViewHolder(
        itemView: View,
        private val onItemClick: (Address) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val streetTextView: TextView = itemView.findViewById(R.id.calle)
        private val cityTextView: TextView = itemView.findViewById(R.id.ciudad)
        private val numberTextView: TextView = itemView.findViewById(R.id.numero)

        fun bind(address: Address) {
            streetTextView.text = address.street
            cityTextView.text = address.city
            numberTextView.text = address.number.toString()

            itemView.setOnClickListener {
                onItemClick(address)
            }
        }
    }

    class AddressComparator : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.idAdress == newItem.idAdress
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
}
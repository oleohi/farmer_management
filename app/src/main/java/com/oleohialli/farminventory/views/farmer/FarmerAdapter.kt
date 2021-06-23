package com.oleohialli.farminventory.views.farmer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oleohialli.farminventory.R
import com.oleohialli.farminventory.data.FarmerInfo
import com.oleohialli.farminventory.databinding.FarmerItemLayoutBinding
import kotlin.coroutines.suspendCoroutine

class FarmerAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FarmerInfo, FarmerAdapter.FarmerViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val binding =
            FarmerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FarmerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }


    inner class FarmerViewHolder(private val binding: FarmerItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val farmer = getItem(position)
                        listener.onItemClick(farmer)
                    }
                }
            }
        }

        fun bind(farmer: FarmerInfo) {
            binding.apply {
                farmerName.text = farmer.farmerName
                farmerPhone.text = farmer.farmerPhone
                if (farmer.farmerPhoto != "")
                    image.setImageURI(farmer.farmerPhoto.toUri())
                else
                    image.setImageResource(R.drawable.person_placeholder_transparent)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(farmer: FarmerInfo)
    }


    class DiffCallback : DiffUtil.ItemCallback<FarmerInfo>() {
        override fun areItemsTheSame(oldItem: FarmerInfo, newItem: FarmerInfo): Boolean {
            return oldItem.id == oldItem.id
        }

        override fun areContentsTheSame(oldItem: FarmerInfo, newItem: FarmerInfo): Boolean {
            return oldItem == newItem
        }

    }
}
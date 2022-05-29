package com.interview.pagination.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.interview.pagination.R
import com.interview.pagination.databinding.AdapterItemBinding
import com.interview.pagination.model.Results
import com.interview.pagination.utils.loadUrl

class UsersAdapter(private val clicked: (Results) -> Unit) :
    PagingDataAdapter<Results, UsersAdapter.UsersViewHolder>(UsersDiffCallback()) {


    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(
            AdapterItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class UsersViewHolder(
        private val binding: AdapterItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Results?) {
            binding.let {
                it.root.setOnClickListener {
                    clicked.invoke(data!!)
                }
                it.tvName.text = it.root.context.getString(
                    R.string.adapter_item,
                    data?.name?.first,
                    data?.name?.last
                )
                it.ivProfilePic.loadUrl(data?.picture?.large!!)
            }
        }
    }

    private class UsersDiffCallback : DiffUtil.ItemCallback<Results>() {
        override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem == newItem
        }
    }

}
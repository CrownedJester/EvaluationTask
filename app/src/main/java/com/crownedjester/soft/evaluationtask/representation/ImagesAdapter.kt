package com.crownedjester.soft.evaluationtask.representation

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.crownedjester.soft.evaluationtask.data.model.Image
import com.crownedjester.soft.evaluationtask.databinding.ItemPhotoBinding

class ImagesAdapter :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem.uriString == newItem.uriString

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem == newItem

    }

    val differ = AsyncListDiffer(this, differCallBack)

    class ImagesViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {

            binding.photoImageView.load(Uri.parse(image.uriString)) {
                transformations(RoundedCornersTransformation(16f))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val photo = differ.currentList[position]

        holder.bind(photo)
    }

    override fun getItemCount(): Int = differ.currentList.size

}
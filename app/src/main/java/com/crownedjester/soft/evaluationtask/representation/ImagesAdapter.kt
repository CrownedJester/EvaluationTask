package com.crownedjester.soft.evaluationtask.representation

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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

    private var _isCheckBoxesVisible = false
    val isCheckBoxesVisible get() = _isCheckBoxesVisible

    private val differCallBack = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem.uriString == newItem.uriString

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem == newItem

    }

    val differ = AsyncListDiffer(this, differCallBack)

    class ImagesViewHolder(val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image) {

            binding.photoImageView.load(uri = Uri.parse(image.uriString)) {
                transformations(RoundedCornersTransformation(16f))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ItemPhotoBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)

        return ImagesViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val photo = differ.currentList[position]

        holder.binding.apply {
            checkToDeleteCheckbox.setOnClickListener {
                photo.isChecked = checkToDeleteCheckbox.isChecked

                val countChecked = differ.currentList.count { image -> image.isChecked }

                Log.i("ImageAdapter", "Count of checked images: $countChecked")
            }

            checkToDeleteCheckbox.visibility =
                if (_isCheckBoxesVisible) View.VISIBLE else View.GONE

        }

        holder.itemView.setOnLongClickListener {
            _isCheckBoxesVisible = !_isCheckBoxesVisible

            if (!_isCheckBoxesVisible) {
                differ.currentList.forEach {
                    if (it.isChecked) {
                        it.isChecked = false
                    }
                }
                val countChecked = differ.currentList.count { image -> image.isChecked }

                Log.i("ImageAdapter", "Count of checked images: $countChecked")
            }

            notifyDataSetChanged()
            Log.i(
                "PhotoAdapter",
                if (_isCheckBoxesVisible) "Checkboxes are visible" else "checkboxes are invisible"
            )
            true
        }

        holder.bind(photo)
    }

    override fun getItemCount(): Int = differ.currentList.size

}
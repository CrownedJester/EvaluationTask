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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.crownedjester.soft.evaluationtask.data.model.Image
import com.crownedjester.soft.evaluationtask.databinding.ItemPhotoBinding


private const val TAG = "ImagesAdapter"

class ImagesAdapter(
    private val adapterClickCallback: AdapterClickCallback
) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    private var isCheckBoxesVisible: Boolean = false

    private val differCallBack = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem.uriString == newItem.uriString

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean =
            oldItem == newItem

    }

    val differ = AsyncListDiffer(this, differCallBack)

    class ImagesViewHolder(val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("CheckResult")
        fun bind(image: Image) {

//            binding.photoImageView.load(image.uriString) {
//                transformations(RoundedCornersTransformation(16f))
//            }
            binding.checkToDeleteCheckbox.isChecked = false

            Glide.with(itemView)
                .load(Uri.parse(image.uriString))
                .transform(RoundedCorners(16))
                .into(binding.photoImageView)
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
                photo?.isChecked = checkToDeleteCheckbox.isChecked

                val countChecked = differ.currentList.count { image -> image.isChecked }

                Log.i(TAG, "Count of checked images: $countChecked")
            }

            checkToDeleteCheckbox.visibility =
                if (isCheckBoxesVisible) View.VISIBLE else View.GONE

        }

        adapterClickCallback.onListEmpty {
            isCheckBoxesVisible = false
            adapterClickCallback.onItemLongClicked(isCheckBoxesVisible)
        }

        holder.itemView.setOnLongClickListener {
            isCheckBoxesVisible = !isCheckBoxesVisible
            adapterClickCallback.onItemLongClicked(isCheckBoxesVisible)
            Log.i(TAG, differ.currentList.toString())

            if (!isCheckBoxesVisible) {
                differ.currentList.forEach {
                    if (it.isChecked) {
                        it.isChecked = false
                    }
                }
                val countChecked = differ.currentList.count { image -> image.isChecked }

                Log.i(TAG, "Count of checked images: $countChecked")
            }

            notifyDataSetChanged()
            Log.i(
                TAG,
                if (isCheckBoxesVisible) "Checkboxes are visible" else "checkboxes are invisible"
            )
            true
        }

        photo?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = differ.currentList.size

}
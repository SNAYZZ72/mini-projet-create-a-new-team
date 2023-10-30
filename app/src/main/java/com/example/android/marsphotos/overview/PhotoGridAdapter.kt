package com.example.android.marsphotos.overview

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.LogPrinter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.MarsPhoto
import com.example.android.marsphotos.databinding.GridViewItemBinding
import kotlin.math.log

class PhotoGridAdapter :
    ListAdapter<MarsPhoto, PhotoGridAdapter.MarsPhotosViewHolder>(DiffCallback) {

    private lateinit var listener: (MarsPhoto, Boolean) -> Unit
    private val selectedPhotos = mutableListOf<MarsPhoto>()

    class MarsPhotosViewHolder(
        private val binding: GridViewItemBinding,
        private val listener: (MarsPhoto, Boolean) -> Unit,
        private val selectedPhotos: MutableList<MarsPhoto>

    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Définissez la bordure par défaut ici (non sélectionnée)
            binding.marsImage.setBackgroundResource(0)
        }

        fun bind(photo: MarsPhoto) {
            bindImage(photo.url)

            /*
            binding.root.setOnClickListener {
                // Appel du listener lorsque l'utilisateur clique sur une image
                val isSelected = !selectedPhotos.contains(photo)
                listener(photo, isSelected)
            }
            */
            binding.root.setOnLongClickListener {
                val isSelected = !selectedPhotos.contains(photo)
                listener(photo, isSelected)
                return@setOnLongClickListener true
            }

            // Mettez ici l'UI de l'élément sélectionné
            val isSelected = selectedPhotos.contains(photo)
            setSelected(isSelected)
        }

        fun setSelected(isSelected: Boolean) {
            if (isSelected) {
                // Définissez la bordure (sélectionnée)
                val borderWidth = 10 // Largeur de la bordure en pixels
                val borderColor = Color.RED // Couleur de la bordure (rouge)

                val border = GradientDrawable()
                border.setColor(0) // Couleur de fond transparente
                border.setStroke(borderWidth, borderColor) // Largeur et couleur de la bordure
                binding.marsImage.background = border
            } else {
                // Supprimez la bordure (non sélectionnée)
                binding.marsImage.setBackgroundResource(0)
            }
        }

        private fun bindImage(imgUrl: String) {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            binding.marsImage.load(imgUri) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MarsPhoto>() {
        override fun areItemsTheSame(oldItem: MarsPhoto, newItem: MarsPhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MarsPhoto, newItem: MarsPhoto): Boolean {
            return oldItem.url == newItem.url
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MarsPhotosViewHolder {
        return MarsPhotosViewHolder(
            GridViewItemBinding.inflate(LayoutInflater.from(parent.context))
            , listener, selectedPhotos
        )
    }

    override fun onBindViewHolder(holder: MarsPhotosViewHolder, position: Int) {
        val marsPhoto = getItem(position)
        holder.bind(marsPhoto)
        holder.setSelected(selectedPhotos.contains(marsPhoto)) // Appelez setSelected ici
    }

    fun getSelectedPhotos(): List<MarsPhoto> {
        return selectedPhotos
    }

    fun setOnPhotoSelectedListener(listener: (MarsPhoto, Boolean) -> Unit) {
        this.listener = listener
    }

    fun clearSelection() {
        selectedPhotos.clear()
        notifyDataSetChanged()

    }
    fun setSelected(photo: MarsPhoto, isSelected: Boolean) {
        if (isSelected) {
            selectedPhotos.add(photo)
        } else {
            selectedPhotos.remove(photo)
        }
        notifyDataSetChanged()
    }


}

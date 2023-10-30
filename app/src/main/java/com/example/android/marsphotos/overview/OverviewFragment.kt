package com.example.android.marsphotos.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.databinding.FragmentOverviewBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.data.MarsPhoto
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by viewModels()

    private val binding: FragmentOverviewBinding by lazy {
        FragmentOverviewBinding.inflate(layoutInflater)
    }

    private val adapter: PhotoGridAdapter by lazy {
        PhotoGridAdapter()
    }

    private var selectedPhotos = mutableSetOf<MarsPhoto>()
    private var isSelectionModeActive = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root.apply {
            binding.photosGrid.adapter = adapter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
            photos.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            error.observe(viewLifecycleOwner) { errorMessage ->
                if (errorMessage != null) {
                    binding.errorTextView.text = errorMessage
                    binding.errorTextView.visibility = View.VISIBLE
                } else {
                    binding.errorTextView.visibility = View.GONE
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Nothing to do here for swiping
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    isSelectionModeActive = true
                    updateDeleteButtonState()
                } else {
                    isSelectionModeActive = false
                    adapter.clearSelection()
                    updateDeleteButtonState()
                }

                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                isSelectionModeActive = false
                adapter.clearSelection()
                updateDeleteButtonState()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.photosGrid)

        adapter.setOnPhotoSelectedListener { photo, isSelected ->
            if (isSelected) {
                selectedPhotos.add(photo)
            } else {
                selectedPhotos.remove(photo)
            }

            adapter.setSelected(photo, isSelected)
            updateDeleteButtonState()
            Log.d("SelectedPhotos", "Selected photos: ${selectedPhotos.map { it.id }}")
        }



        binding.deleteButton.setOnClickListener {
            selectedPhotos.forEach { photo ->
                viewModel.deletePhoto(photo.id)
            }
            selectedPhotos.clear()
            adapter.notifyDataSetChanged()
            updateDeleteButtonState()

        }


    }

    private fun updateDeleteButtonState() {
        if (selectedPhotos.isNotEmpty()) {
            binding.deleteButton.visibility = View.VISIBLE
            binding.deleteButton.isEnabled = true
        } else {
            binding.deleteButton.visibility = View.GONE
            binding.deleteButton.isEnabled = false
        }
    }


}

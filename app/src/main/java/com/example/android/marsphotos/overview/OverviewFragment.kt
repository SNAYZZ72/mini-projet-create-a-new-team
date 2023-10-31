package com.example.android.marsphotos.overview

import android.app.AlertDialog
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.marsphotos.databinding.FragmentOverviewBinding
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsphotos.R
import com.example.android.marsphotos.data.MarsDatabase
import com.example.android.marsphotos.data.MarsPhoto
import kotlinx.coroutines.launch
import com.example.android.marsphotos.repository.MarsRepository
import java.io.ByteArrayOutputStream
import java.util.UUID
import android.util.Base64

import okhttp3.internal.filterList

class OverviewFragment : Fragment() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2


    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    val encodedImage = convertBitmapToBase64(imageBitmap)
                    saveImageToDatabase(encodedImage)
                }
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, selectedImageUri)
                    val encodedImage = convertBitmapToBase64(imageBitmap)
                    saveImageToDatabase(encodedImage)
                }
            }
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun saveImageToDatabase(encodedImage: String) {
        viewModel.savePhotoToDatabase(encodedImage)
    }

    private val database by lazy {
        MarsDatabase.getInstance(requireContext())
    }

    private val repository: MarsRepository by lazy {
        MarsRepository(database.marsPhotoDao, requireContext())
    }

    private val viewModel: OverviewViewModel by viewModels{
        OverviewViewModelFactory(repository, database)
    }

    private val binding: FragmentOverviewBinding by lazy {
        FragmentOverviewBinding.inflate(layoutInflater)
    }

    private val adapter: PhotoGridAdapter by lazy {
        PhotoGridAdapter()
    }

    private var selectedPhotos = mutableSetOf<MarsPhoto>()
    private var isSelectionModeActive = false

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Supprimer les photos sélectionnées")
        builder.setMessage("Êtes-vous sûr de vouloir supprimer les photos sélectionnées ?")

        // Bouton de confirmation
        builder.setPositiveButton("Supprimer") { dialog, which ->
            // Supprimez les photos ici
            selectedPhotos.forEach { photo ->
                viewModel.deletePhoto(photo.id)
            }
            selectedPhotos.clear()
            adapter.notifyDataSetChanged()
            updateButtonState()
        }

        // Bouton d'annulation
        builder.setNegativeButton("Annuler") { dialog, which ->
            dialog.dismiss() // Fermez la boîte de dialogue
        }

        val dialog = builder.create()
        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_overview, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter_liked -> {
                val likedPhotos = viewModel.photos.value?.filter { it.liked == true }
                if (likedPhotos.isNullOrEmpty()) {
                    adapter.submitList(viewModel.photos.value)
                } else {
                    adapter.submitList(likedPhotos)
                }
                true
            }
            R.id.action_filter_all -> {
                adapter.submitList(viewModel.photos.value)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Active l'affichage du menu
    }


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

        val capturePhotoButton = view.findViewById<Button>(R.id.capturePhotoButton)
        val selectPhotoButton = view.findViewById<Button>(R.id.selectPhotoButton)

        capturePhotoButton.setOnClickListener {
            openCamera()
        }

        selectPhotoButton.setOnClickListener {
            openGallery()
        }

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
                    updateButtonState()
                } else {
                    isSelectionModeActive = false
                    adapter.clearSelection()
                    updateButtonState()
                }

                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                isSelectionModeActive = false
                adapter.clearSelection()
                updateButtonState()
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
            updateButtonState()
            Log.d("SelectedPhotos", "Selected photos: ${selectedPhotos.map { it.id }}")
        }

        binding.deleteButton.setOnClickListener {
            if (selectedPhotos.isNotEmpty()) {
                showDeleteConfirmationDialog()
            }
            else {
                selectedPhotos.forEach { photo ->
                    viewModel.deletePhoto(photo.id)
                }

                selectedPhotos.clear()
                adapter.notifyDataSetChanged()
                updateButtonState()
            }
        }
        val shareButton = binding.shareButton

        shareButton.setOnClickListener {
            // Partagez les photos sélectionnées ici
            shareSelectedPhotos()
        }

        val cancelButton = binding.cancelButton

        cancelButton.setOnClickListener {
            // Annuler la sélection en désélectionnant toutes les images
            adapter.clearSelection()
            selectedPhotos.clear()
            updateButtonState()
            binding.deleteButton.visibility = View.GONE
            binding.shareButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            binding.deleteButton.isEnabled = false
            binding.shareButton.isEnabled = false
        }

        // Gestion du clic simple sur une photo
        adapter.setOnItemClickListener { photo ->
            val detailFragment = DetailFragment.newInstance(photo.url, photo.liked ?: false, photo.id, repository, database)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        adapter.setOnLongItemClickListener { photo ->
            // Mettez votre logique de sélection ici
            if (selectedPhotos.contains(photo)) {
                selectedPhotos.remove(photo)
            } else {
                selectedPhotos.add(photo)
            }
            adapter.setSelected(photo, selectedPhotos.contains(photo))
            updateButtonState()
        }
    }

    private fun shareSelectedPhotos() {
        val selectedPhotoUris = selectedPhotos.map { it.url.toUri() }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(selectedPhotoUris))
            type = "image/*"
        }

        startActivity(Intent.createChooser(sendIntent, "Partager les photos sélectionnées"))
    }

    private fun updateButtonState() {
        if (selectedPhotos.isNotEmpty()) {
            // Au moins une photo est sélectionnée, affiche les boutons "Supprimer" et "Partager"
            binding.deleteButton.visibility = View.VISIBLE
            binding.shareButton.visibility = View.VISIBLE
            binding.cancelButton.visibility = View.VISIBLE
            binding.deleteButton.isEnabled = true
            binding.shareButton.isEnabled = true
        } else {
            // Aucune photo sélectionnée, cache les boutons "Supprimer" et "Partager"
            binding.deleteButton.visibility = View.GONE
            binding.shareButton.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
            binding.deleteButton.isEnabled = false
            binding.shareButton.isEnabled = false
        }
    }


}

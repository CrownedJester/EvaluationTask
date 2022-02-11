package com.crownedjester.soft.evaluationtask.representation

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.crownedjester.soft.evaluationtask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterClickCallback {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val imagesViewModel by viewModels<ImagesViewModel>()

    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var requestImagesRetrievingFromGallery: ActivityResultLauncher<String>? = null

    private var adapter: ImagesAdapter = ImagesAdapter(this)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //check read storage permission
        initRequestPermissionLauncher()
        requestPermissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        initImagesRetrieverLauncher()

        binding.apply {

            lifecycleScope.launch {
                imagesViewModel.imagesStateFlow.collectLatest { images ->
                    adapter.differ.submitList(images)
                    Log.i(TAG, "List data updated")
                }
            }

            lifecycleScope.launch {
                imagesViewModel.folderTitleStateFlow.collectLatest { title ->
                    folderEditText.setText(title)
                    Log.i(TAG, "Folder title applied")
                }
            }

            lifecycleScope.launch {
                imagesViewModel.subFolderTitleStateFlow.collectLatest { title ->
                    subfolderEditText.setText(title)
                    Log.i(TAG, "SubFolder title applied")
                }
            }
        }

        binding.apply {
            photosRv.adapter = adapter

            addImagesBtn.setOnClickListener {
                requestImagesRetrievingFromGallery?.launch("image/*")
            }


            folderEditText.apply {
                setOnEditorActionListener { view, actionId, _ ->
                    var handled = false
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        clearFocus()
                        (getSystemService(INPUT_METHOD_SERVICE) as (InputMethodManager)).hideSoftInputFromWindow(
                            view.windowToken,
                            0
                        )
                        imagesViewModel.updateFolderTitle(text.toString())
                        handled = true
                    }
                    handled
                }
            }

            subfolderEditText.apply {
                setOnEditorActionListener { view, actionId, _ ->
                    var handled = false
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        clearFocus()

                        (getSystemService(INPUT_METHOD_SERVICE) as (InputMethodManager)).hideSoftInputFromWindow(
                            view.windowToken,
                            0
                        )

                        imagesViewModel.updateSubFolderTitle(text.toString())

                        handled = true
                    }
                    handled
                }
            }
        }

    }

    private fun initImagesRetrieverLauncher() {
        requestImagesRetrievingFromGallery =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
                uriList?.onEach { currentUri ->
                    imagesViewModel.addImage(currentUri)
                }
            }
    }

    private fun initRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(this, "All Permissions Granted!", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        this,
                        "Important permission was denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onItemLongClicked(isVisible: Boolean) {
        binding.deleteBtn.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun onDeleteButtonPressed(onAction: () -> Unit) {
        binding.deleteBtn.setOnClickListener {
            adapter.differ.currentList.onEach { image ->
                if (image.isChecked) imagesViewModel.deleteImage(image)
            }

            onAction()

            Log.i(TAG, "Performed delete btn pressed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
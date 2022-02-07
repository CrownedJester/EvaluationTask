package com.crownedjester.soft.evaluationtask.representation

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.crownedjester.soft.evaluationtask.data.model.Image
import com.crownedjester.soft.evaluationtask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //    private val imagesViewModel by viewModels<ImagesViewModel>()
    private val imagesList = mutableListOf<Image>()

    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var requestImagesRetrievingFromGallery: ActivityResultLauncher<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //check read storage permission
        initRequestPermissionLauncher()
        requestPermissionLauncher?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        initImagesRetrieverLauncher()

        val adapter = ImagesAdapter()
        adapter.differ.submitList(imagesList)
        binding.photosRv.adapter = adapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {

                binding.addImagesBtn.setOnClickListener {
                    requestImagesRetrievingFromGallery?.launch("image/*")
                    adapter.differ.submitList(imagesList)
                }
            }

        }
    }

    private fun initImagesRetrieverLauncher() {
        requestImagesRetrievingFromGallery =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
                uriList?.onEach { currentUri ->
                    imagesList.add(
                        Image(
                            uriString = currentUri.toString()
                        )
                    )
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.crownedjester.soft.evaluationtask.representation

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
import androidx.core.view.size
import androidx.lifecycle.lifecycleScope
import com.crownedjester.soft.evaluationtask.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AdapterClickCallback {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val imagesViewModel by viewModels<ImagesViewModel>()

    private var requestImagesRetrievingFromGallery: ActivityResultLauncher<Array<String>>? = null

    private var adapter: ImagesAdapter = ImagesAdapter(this)

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            bottomNav.menu.getItem(bottomNav.menu.size - 1).isChecked = true

            addImagesBtn.setOnClickListener {
                requestImagesRetrievingFromGallery?.launch(arrayOf("image/*"))
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

                        Toasty.success(
                            this@MainActivity,
                            "Название папки изменено!",
                            Toast.LENGTH_SHORT
                        ).show()

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

                        Toasty.success(
                            this@MainActivity,
                            "Название подпапки изменено!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    handled
                }
            }
        }

    }

    private fun initImagesRetrieverLauncher() {
        requestImagesRetrievingFromGallery =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList ->
                uriList?.onEach { currentUri ->
                    imagesViewModel.addImage(currentUri)
                }
                if (uriList.isNotEmpty()) {
                    Toasty.success(this, "Изображения успешно добавлены").show()
                }
            }
    }

    override fun onItemLongClicked(isVisible: Boolean) {
        binding.deleteBtn.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        if (isVisible) {
            Toasty.info(this, "Удерживайте повторно, чтобы скрыть!", Toasty.LENGTH_LONG).show()

        }
    }

    override fun onDeleteButtonPressed(onAction: () -> Unit) {
        binding.deleteBtn.setOnClickListener {
            adapter.differ.currentList.onEach { image ->
                if (image.isChecked) imagesViewModel.deleteImage(image)
            }

            onAction()

            Toasty.success(this, "Изображения удалены!", Toasty.LENGTH_LONG).show()

            Log.i(TAG, "Performed delete btn pressed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
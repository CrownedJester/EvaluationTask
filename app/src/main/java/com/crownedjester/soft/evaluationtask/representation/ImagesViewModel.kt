package com.crownedjester.soft.evaluationtask.representation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crownedjester.soft.evaluationtask.data.model.Image
import com.crownedjester.soft.evaluationtask.domain.repository.DatabaseRepository
import com.crownedjester.soft.evaluationtask.features.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "ImagesViewModel"

@HiltViewModel
class ImagesViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private var _imagesStateFlow = MutableStateFlow(listOf<Image>())
    val imagesStateFlow: StateFlow<List<Image>> = _imagesStateFlow

    private var _folderTitleStateFlow = MutableStateFlow("")
    val folderTitleStateFlow: StateFlow<String> = _folderTitleStateFlow

    private var _subFolderTitleStateFlow = MutableStateFlow("")
    val subFolderTitleStateFlow: StateFlow<String> = _subFolderTitleStateFlow

    init {
        getImages()
        applyFolderTitle()
        applySubFolderTitle()
    }

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            val image = Image(uriString = uri.toString())
            databaseRepository.addImage(image)
            Log.i(TAG, "Image $image successfully added")
        }
    }

    fun deleteImage(image: Image) {
        viewModelScope.launch {
            databaseRepository.deleteImage(image)
            Log.i(TAG, "Image $image successfully deleted")
        }
    }

    private fun getImages() =
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getImages().collectLatest {
                _imagesStateFlow.emit(it)
                Log.i(TAG, "Images loaded")
            }
        }

    fun updateFolderTitle(title: String) {
        viewModelScope.launch {
            dataStoreRepository.setFolderTitle(title)
        }
    }

    fun updateSubFolderTitle(title: String) {
        viewModelScope.launch {
            dataStoreRepository.setSubFolderTitle(title)
        }
    }

    private fun applyFolderTitle() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.folderTitle.collectLatest {
                _folderTitleStateFlow.emit(it)
                Log.i(TAG, "Folder updated to $it")
            }
        }
    }

    private fun applySubFolderTitle() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.subFolderTitle.collectLatest {
                _subFolderTitleStateFlow.emit(it)
                Log.i(TAG, "Subfolder updated to $it")
            }
        }
    }
}
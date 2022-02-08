package com.crownedjester.soft.evaluationtask.representation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crownedjester.soft.evaluationtask.data.model.Image
import com.crownedjester.soft.evaluationtask.domain.repository.DatabaseRepository
import com.crownedjester.soft.evaluationtask.features.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
        viewModelScope.launch {
            getImages()
            applyFolderTitle()
            applySubFolderTitle()
        }
    }

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            val image = Image(uriString = uri.toString())
            databaseRepository.addImage(image)
        }
    }

    fun deleteImage(image: Image) {
        viewModelScope.launch {
            databaseRepository.deleteImage(image)
        }
    }

    suspend fun getImages() {
        databaseRepository.getImages().collect {
            _imagesStateFlow.emit(it)
        }
    }

    fun updateFolderTitle(title: String) {
        viewModelScope.launch {
            dataStoreRepository.setFolderTitle(title)
            applyFolderTitle()
        }
    }

    fun updateSubFolderTitle(title: String) {
        viewModelScope.launch {
            dataStoreRepository.setSubFolderTitle(title)
            applySubFolderTitle()
        }
    }

    private suspend fun applyFolderTitle() {
        dataStoreRepository.folderTitle.collect {
            _folderTitleStateFlow.emit(it)
        }
    }

    private suspend fun applySubFolderTitle() {
        dataStoreRepository.subFolderTitle.collect {
            _subFolderTitleStateFlow.emit(it)
        }
    }
}
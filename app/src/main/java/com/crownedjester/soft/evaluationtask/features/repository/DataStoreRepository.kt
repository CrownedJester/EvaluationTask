package com.crownedjester.soft.evaluationtask.features.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {

    val folderTitle: Flow<String>
    val subFolderTitle: Flow<String>

    suspend fun setFolderTitle(title: String)

    suspend fun setSubFolderTitle(title: String)
}
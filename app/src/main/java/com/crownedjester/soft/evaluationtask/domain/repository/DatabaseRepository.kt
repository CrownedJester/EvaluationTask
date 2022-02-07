package com.crownedjester.soft.evaluationtask.domain.repository

import com.crownedjester.soft.evaluationtask.data.model.Image
import kotlinx.coroutines.flow.Flow

interface DatabaseRepository {

    fun getImages(): Flow<List<Image>>

    suspend fun addImage(image: Image)

    suspend fun deleteImage(image: Image)
}
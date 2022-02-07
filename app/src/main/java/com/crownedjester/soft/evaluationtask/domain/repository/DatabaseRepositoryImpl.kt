package com.crownedjester.soft.evaluationtask.domain.repository

import com.crownedjester.soft.evaluationtask.data.data_source.ImageDao
import com.crownedjester.soft.evaluationtask.data.model.Image
import kotlinx.coroutines.flow.Flow

class DatabaseRepositoryImpl(private val imageDao: ImageDao) : DatabaseRepository {

    override fun getImages(): Flow<List<Image>> =
        imageDao.getImages()

    override suspend fun addImage(image: Image) {
        imageDao.addImage(image)
    }

    override suspend fun deleteImage(image: Image) {
        imageDao.deleteImage(image)
    }

}
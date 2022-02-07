package com.crownedjester.soft.evaluationtask.data.data_source

import androidx.room.*
import com.crownedjester.soft.evaluationtask.data.model.Image
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM image_table")
    fun getImages(): Flow<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

}

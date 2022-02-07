package com.crownedjester.soft.evaluationtask.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.crownedjester.soft.evaluationtask.data.model.Image

@Database(
    entities = [Image::class],
    version = 1
)
abstract class ImagesDatabase : RoomDatabase() {

    abstract val photoDao: ImageDao

}

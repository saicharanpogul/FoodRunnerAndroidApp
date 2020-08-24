package com.training.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RestaurantDetailsEntity::class], version = 1)
abstract class RestaurantDetailsDatabase: RoomDatabase() {
    abstract fun restaurantDetailsDao(): RestaurantDetailsDao
}
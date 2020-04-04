package com.example.shoppinglist

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ShoppingListRoomDB : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        private const val DATABASE_NAME = "PRODUCT_DATABASE"

        @Volatile
        private var productRoomDBInstance: ShoppingListRoomDB? = null

        fun getDatabase(context: Context): ShoppingListRoomDB? {
            if (productRoomDBInstance == null) {
                synchronized(ShoppingListRoomDB::class.java) {
                    if (productRoomDBInstance == null) {
                        productRoomDBInstance = Room.databaseBuilder(
                            context.applicationContext,
                            ShoppingListRoomDB::class.java, DATABASE_NAME
                        )
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return productRoomDBInstance
        }
    }
}
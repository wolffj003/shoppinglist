package com.example.shoppinglist

import android.content.Context

class ProductRepository(context: Context) {

    private val productDao: ProductDao

    init {
        val database = ShoppingListRoomDB.getDatabase(context)
        productDao = database!!.productDao()
    }

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    suspend fun getAllProducts(): List<Product> = productDao.getAllProducts()

    suspend fun deleteAllProducts() = productDao.deleteAllProducts()

}

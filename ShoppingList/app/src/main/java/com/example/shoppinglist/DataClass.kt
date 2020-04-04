package com.example.shoppinglist

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "productTable")
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")  // Optional as Room can derive it from variable 'id'
    var id: Long? = null,

    @ColumnInfo(name = "productName")
    val productText: String,

    @ColumnInfo(name = "productQuantity")
    val productQuantity: Int
) : Parcelable

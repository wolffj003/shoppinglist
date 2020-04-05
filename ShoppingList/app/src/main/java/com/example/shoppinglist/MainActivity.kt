package com.example.shoppinglist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.NumberFormatException
import kotlin.reflect.typeOf


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var productRepository: ProductRepository
    private var products = arrayListOf<Product>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createItemTouchHelper().attachToRecyclerView(rvProducts)

        productRepository = ProductRepository(this)

        viewManager = LinearLayoutManager(this)
        viewAdapter = ProductAdapter(products)

        recyclerView = findViewById<RecyclerView>(R.id.rvProducts).apply {
            setHasFixedSize(true)   // Performance tweak
            layoutManager = viewManager
            adapter = viewAdapter
        }

            initViews()
    }


    private fun initViews() {
        fabAddProduct.setOnClickListener { onAddProductClick() }
        getProductsFromDB()
    }


    private fun onAddProductClick() {
        val etProductText: String = etProduct.text.toString() // Looks nicer
        val etQuantityText: String = etQuantity.text.toString()

        if (etProductText.isNotBlank() and etQuantityText.isNotBlank()) {
            val product = Product(null, etProductText, etQuantityText.toInt())

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    productRepository.insertProduct(product)
                }
                getProductsFromDB()
            }

        } else Toast.makeText(this, "Please fill out both fields.", Toast.LENGTH_SHORT).show()
    }


    private fun getProductsFromDB() {
        CoroutineScope(Dispatchers.Main).launch {
            val shoppingList = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }
            this@MainActivity.products.clear()
            this@MainActivity.products.addAll(shoppingList)
            this@MainActivity.viewAdapter.notifyDataSetChanged()
        }
    }


    private fun createItemTouchHelper(): ItemTouchHelper {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productToDelete = products[position]

                CoroutineScope(Dispatchers.Main).launch {
                    withContext(Dispatchers.IO) {
                        productRepository.deleteProduct(productToDelete)
                    }
                    getProductsFromDB()
                }
            }
        }
        return ItemTouchHelper(callback)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        deleteAllProducts()

        return when (item.itemId) {
            R.id.menuDelete -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun deleteAllProducts() {
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                productRepository.deleteAllProducts()
            }
            getProductsFromDB()

        }
    }
}

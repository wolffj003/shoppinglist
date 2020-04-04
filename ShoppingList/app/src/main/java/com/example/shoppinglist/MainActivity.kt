package com.example.shoppinglist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.PortUnreachableException


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

        if (etProductText.isNotBlank() and etQuantityText.isNotBlank()) {  // Check if an integer is provided by user
            val product = Product(null, etProductText, etQuantityText.toInt())

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    productRepository.insertProduct(product)
                }
            }

            getProductsFromDB()

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


    override fun onCreateOptionsMenu(menu: Menu): Boolean {  // This one too
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {  // This can be removed
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

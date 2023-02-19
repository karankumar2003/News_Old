package com.example.flashnews

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.facebook.shimmer.ShimmerFrameLayout
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class MainActivity : AppCompatActivity(), RecyclerViewItemClickListener {
    val arrayList = ArrayList<News>()
    lateinit var newsAdapter: NewsAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var shimmerFrameLayout: ShimmerFrameLayout
    lateinit var  reloadButton : Button
    lateinit var  internetStatusTextView : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        shimmerFrameLayout = findViewById(R.id.shimmer)
        shimmerFrameLayout.startShimmer()


        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        newsAdapter = NewsAdapter(arrayList, this)
        recyclerView.adapter = newsAdapter
        checkInternetConnection()
        fetchData()


    }

    private fun fetchData() {
shimmerFrameLayout.visibility=View.VISIBLE

        val url =
            "https://newsdata.io/api/1/news?apikey=pub_1722619c153aa6e11254c32f6e530ea14798d&language=en&country=in"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            {
                val results = it.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val newsJsonObject = results.getJSONObject(i)


                    val dateString = newsJsonObject.getString("pubDate")
                    val previousFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val newFormat = SimpleDateFormat("MMM dd, h:mm a")
                    val previousDate = previousFormat.parse(dateString)
                    val newDate = newFormat.format(previousDate)


                    val news = News(
                        newsJsonObject.getString("title"),
                        newsJsonObject.getString("image_url"),
                        newsJsonObject.getString("link"),
                        newDate,
                        newsJsonObject.getString("description"),
                        newsJsonObject.getString("source_id")
                    )
                    arrayList.add(news)

                }
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility = View.GONE
                internetStatusTextView.visibility = View.GONE
                reloadButton.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                newsAdapter.notifyDataSetChanged()
            }, {
                val statusCode = it.networkResponse?.statusCode
                Log.e("MainActivity", "Error - status code: $statusCode")
                internetStatusTextView.visibility = View.VISIBLE
                reloadButton.visibility = View.VISIBLE
                shimmerFrameLayout.visibility = View.GONE
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    override fun onItemClick(position: Int) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(this, Uri.parse((arrayList[position].url)))

    }

    fun checkInternetConnection(){
        internetStatusTextView = findViewById(R.id.InternetStatusTextView)
         reloadButton = findViewById(R.id.ReloadButton)

        val connectivityManager : ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)==null){
            recyclerView.visibility = View.GONE
            internetStatusTextView.visibility = View.VISIBLE
            internetStatusTextView.text = "No Internet Connection!"
            reloadButton.visibility = View.VISIBLE

        }

        reloadButton.setOnClickListener{
            reloadButton.visibility = View.GONE
            internetStatusTextView.visibility =View.GONE

            fetchData()

        }



    }
}
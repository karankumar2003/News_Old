package com.example.flashnews

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.facebook.shimmer.ShimmerFrameLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(),RecyclerViewItemClickListener {
    val arrayList = ArrayList<News>()
    lateinit var newsAdapter : NewsAdapter
    lateinit var  recyclerView: RecyclerView
    lateinit var shimmerFrameLayout :ShimmerFrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shimmerFrameLayout = findViewById(R.id.shimmer)
        shimmerFrameLayout.startShimmer()


        recyclerView = findViewById(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

         newsAdapter = NewsAdapter(arrayList,this)
        recyclerView.adapter = newsAdapter

        fetchData()


    }

    private fun fetchData(){
        val url = "https://newsdata.io/api/1/news?apikey=pub_1722619c153aa6e11254c32f6e530ea14798d&language=en&country=in"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            {
                val results = it.getJSONArray("results")

                for (i in 0 until results.length()){
                    val newsJsonObject  = results.getJSONObject(i)


                    val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                    val dateString = newsJsonObject.getString("pubDate")

                    val date : Date = dateFormat.parse(dateString)
                    val dateToDisplay = DateUtils.getRelativeDateTimeString(this,
                                        date.time,
                                        Calendar.getInstance().timeInMillis,
                                        DateUtils.MINUTE_IN_MILLIS,
                                        DateUtils.FORMAT_ABBREV_ALL or  DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE
                                    ).toString()
                    Log.d("hello",dateToDisplay)

                    val news = News(
                   newsJsonObject.getString("title"),
                        newsJsonObject.getString("image_url"),
                    newsJsonObject.getString("link"),
                    dateToDisplay,
                        newsJsonObject.getString("description"),
                        newsJsonObject.getString("source_id")
                    )
                    arrayList.add(news)

                }
                shimmerFrameLayout.stopShimmer()
                shimmerFrameLayout.visibility=View.GONE
                recyclerView.visibility = View.VISIBLE
                newsAdapter.notifyDataSetChanged()
            },{
                val statusCode = it.networkResponse?.statusCode
                Log.e("MainActivity", "Error - status code: $statusCode")
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }

    override fun onItemClick(position: Int) {
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.intent.setPackage("com.android.chrome")
        customTabsIntent.launchUrl(this, Uri.parse((arrayList[position].url)))

    }
}
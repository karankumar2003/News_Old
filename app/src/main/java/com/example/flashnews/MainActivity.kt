package com.example.flashnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    val arrayList = ArrayList<News>()
    lateinit var newsAdapter : NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

         newsAdapter = NewsAdapter(arrayList)
        recyclerView.adapter = newsAdapter

        fetchData()


    }

    private fun fetchData(){
        val url = "https://newsdata.io/api/1/news?apikey=pub_1722619c153aa6e11254c32f6e530ea14798d&language=en"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET,url,null,
            {
                val results = it.getJSONArray("results")

                for (i in 0 until results.length()){
                    val newsJsonObject  = results.getJSONObject(i)
                    val news = News(
                   newsJsonObject.getString("title"),
                        newsJsonObject.getString("image_url")
                    )
                    arrayList.add(news)

                }
                newsAdapter.notifyDataSetChanged()
            },{
                val statusCode = it.networkResponse?.statusCode
                Log.e("MainActivity", "Error - status code: $statusCode")
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

    }
}
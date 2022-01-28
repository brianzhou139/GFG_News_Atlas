package com.edufree.damn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jacksonandroidnetworking.JacksonParserFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /*
    TODO : set the API_KEY variable to your api key
    */
    private static  String API_KEY="b971358de21d4af48ae24b5faf06bbfd";

    // setting the TAG for debugging purposes
    private static String TAG="MainActivity";

    // declaring the views
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    // declaring an ArrayList of articles
    private ArrayList<NewsArticle> mArticleList;


    private ArticleAdapter mArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  initializing the Fast Android Networking Library
        AndroidNetworking.initialize(getApplicationContext());

        // setting the JacksonParserFactory
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        // assigning views to their ids
        mProgressBar=(ProgressBar)findViewById(R.id.progressbar_id);
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerview_id);

        // setting the recyclerview layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // initializing the ArrayList of articles
        mArticleList=new ArrayList<>();

        // calling get_news_from_api()
        get_news_from_api();
    }

    public void get_news_from_api(){
        // clearing the articles list before adding news ones
        mArticleList.clear();

        /* Making a GET Request using Fast Android Networking Library
         the request returns a JSONObject containing news articles from the news api
        or it will return an error

        */

        AndroidNetworking.get("https://newsapi.org/v2/top-headlines")
                .addQueryParameter("country", "in")
                .addQueryParameter("apiKey",API_KEY)
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // disabling the progress bar
                        mProgressBar.setVisibility(View.GONE);

                        // handling the response
                        try {

                            // storing the response in a JSONArray
                            JSONArray articles=response.getJSONArray("articles");

                            // looping through all the articles to access them individually
                            for (int j=0;j<articles.length();j++)
                            {
                                // accessing each article object in the JSONArray
                                JSONObject article=articles.getJSONObject(j);

                                // initializing an empty ArticleModel
                                NewsArticle currentArticle=new NewsArticle();

                                // storing values of the article object properties
                                String author=article.getString("author");
                                String title=article.getString("title");
                                String description=article.getString("description");
                                String url=article.getString("url");
                                String urlToImage=article.getString("urlToImage");
                                String publishedAt=article.getString("publishedAt");
                                String content=article.getString("content");

                                // setting the values of the ArticleModel using the set methods
                                currentArticle.setAuthor(author);
                                currentArticle.setTitle(title);
                                currentArticle.setDescription(description);
                                currentArticle.setUrl(url);
                                currentArticle.setUrlToImage(urlToImage);
                                currentArticle.setPublishedAt(publishedAt);
                                currentArticle.setContent(content);

                                // adding an article to the articles List
                                mArticleList.add(currentArticle);
                            }

                            // setting the adapter
                            mArticleAdapter=new ArticleAdapter(getApplicationContext(),mArticleList);
                            mRecyclerView.setAdapter(mArticleAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            // logging the JSONException LogCat
                            Log.d(TAG,"Error : "+e.getMessage());
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // logging the error detail and response to LogCat
                        Log.d(TAG,"Error detail : "+error.getErrorDetail());
                        Log.d(TAG,"Error response : "+error.getResponse());
                    }
                });
    }



}
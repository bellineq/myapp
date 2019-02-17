package com.example.myapp;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Connection;
import org.jsoup.Connection.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


public class Search extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d("SEARCH", "HERE");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String queryStr = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(queryStr);
        }
    }

    private void doMySearch(String queryStr) {
        Toast.makeText(getApplicationContext(), queryStr, Toast.LENGTH_LONG).show();
        try {
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, queryStr);
//            startActivity(intent);

//            DefaultHttpClient client = new DefaultHttpClient();
//            HttpGet get = new HttpGet(url.toURI());
//            HttpResponse resp = client.execute(get);
//            String content = EntityUtils.toString(resp.getEntity());
//            Document doc = Jsoup.parse(content);
//            Elements ele = doc.select("div.classname");

            String wikiQueryStr = "https://en.m.wikipedia.org/wiki/" + queryStr;
//            String wikiQueryStr = "https://en.m.wikipedia.org/wiki/The_Starry_Night";

//            Connection.Response resp = Jsoup.connect(wikiQueryStr)
//                    .method(Method.GET)
//                    .execute();

//            Document doc = resp.parse();
//            Elements el = doc.select(".total_all > tbody > tr > td");
//            Double balance = Double.parseDouble(el.get(0).text().replace("$", "").replace(",", ""));

            WebView wikiWebView = findViewById(R.id.search_result);
            wikiWebView.loadUrl(wikiQueryStr);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}

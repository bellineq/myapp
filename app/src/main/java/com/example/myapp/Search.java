package com.example.myapp;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class Search extends AppCompatActivity {

    private WebView wvResultImage;
    private WebView wvResultTitle;
    private WebView wvResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        wvResultImage = findViewById(R.id.search_result_img);
        wvResultTitle = findViewById(R.id.search_result_title);
        wvResultText = findViewById(R.id.search_result_txt);
        handleIntent(getIntent());

/*        Button BtnCamera= findViewById(R.id.btn_camera);
        BtnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        });

        Button BtnSearch= findViewById(R.id.btn_search);
        BtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });*/
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String queryStr = intent.getStringExtra(SearchManager.QUERY);
            wikiSearch(queryStr);
//            simpleWikiSearch(queryStr);
        }
    }

    private void wikiSearch(String queryStr) {
        final String wikiQueryStr = "https://en.m.wikipedia.org/wiki/" + queryStr;
        new Thread(new Runnable() {
            public void run() {
                final String strImage;
                final String strTitle;
                final String strIntro;
                try {
                    final Document doc = Jsoup.connect(wikiQueryStr).get();

                    Element elemImage = doc.select("img").get(1);
                    strImage = elemImage.absUrl("src");

                    Element elemTitle = doc.select("#section_0").get(0);
                    strTitle = elemTitle.text();

                    Element elemText = doc.select("#mf-section-0 > p").get(1);
                    strIntro = elemText.text().replaceAll("\\[[0-9]\\]", "");

                    runOnUiThread(new Runnable() {
                        public void run() {
                            wvResultText.setWebViewClient(new WebViewClient());
                            wvResultTitle.setWebViewClient(new WebViewClient());
                            wvResultImage.setWebViewClient(new WebViewClient());
                            wvResultImage.loadUrl(strImage);
                            wvResultTitle.loadData(strTitle, "text/html", "charset=UTF-8");
                            wvResultText.loadData(strIntro, "text/html", "charset=UTF-8");
                        }
                    });
                } catch (IOException e) {
                }
            }
        }).start();
    }

    private void simpleWikiSearch(String queryStr) {
        Toast.makeText(getApplicationContext(), queryStr, Toast.LENGTH_LONG).show();
        String wikiQueryStr = "https://en.m.wikipedia.org/wiki/The_Starry_Night";
        wvResultText.setWebViewClient(new WebViewClient());
        wvResultImage.setWebViewClient(new WebViewClient());
        wvResultText.loadUrl(wikiQueryStr);
        wvResultImage.loadUrl("http:" + "//upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg/300px-Van_Gogh_-_Starry_Night_-_Google_Art_Project.jpg");
    }

}

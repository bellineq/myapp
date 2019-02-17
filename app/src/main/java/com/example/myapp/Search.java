package com.example.myapp;

import android.app.SearchManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Locale;

//public class Search extends AppCompatActivity{
public class Search extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private WebView wvResultImage;
    private WebView wvResultTitle;
    private WebView wvResultText;

    TextToSpeech mT2S = null;
    private final int ACT_CHECK_TTS_DATA = 1000;

    private EditText txtTextToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        wvResultImage = findViewById(R.id.search_result_img);
        wvResultTitle = findViewById(R.id.search_result_title);
        wvResultText = findViewById(R.id.search_result_txt);
        handleIntent(getIntent());

        txtTextToSpeech = findViewById(R.id.t2s_init);

        Button btnTextToSpeech = findViewById(R.id.btn_t2s);
        btnTextToSpeech.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                readIntro(txtTextToSpeech.getText().toString(), 1);
            }
        });

        // Check to see if we have TTS voice data
        Intent t2sIntent = new Intent();
        t2sIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(t2sIntent, ACT_CHECK_TTS_DATA);

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
        final String wikiQueryStr = "https://en.m.wikipedia.org/wiki/The_Starry_Night";
//        final String wikiQueryStr = "https://en.m.wikipedia.org/wiki/" + queryStr;
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
                    txtTextToSpeech.setText(strIntro);

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

    private void readIntro(String text, int qmode) {
        if (qmode == 1)
            mT2S.speak(text, TextToSpeech.QUEUE_ADD, null);
        else
            mT2S.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mT2S = new TextToSpeech(this, this);
            } else {
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mT2S != null) {
                int result = mT2S.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show();
                } else {
                    readIntro("", 0);
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (mT2S != null) {
            mT2S.stop();
            mT2S.shutdown();
        }
        super.onDestroy();
    }

}

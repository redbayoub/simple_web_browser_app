package com.list.nasro.webbrowser;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private AutoCompleteTextView urlInput;
    private ProgressBar pageLodingProgressBar;

    private DBConnection dbConnection;
    private ArrayAdapter<String> webPageArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        urlInput = (AutoCompleteTextView) findViewById(R.id.edtWebUrl);
        webView = (WebView) findViewById(R.id.myWebView);
        pageLodingProgressBar = findViewById(R.id.progress);
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);

        // init webView with google
        pageLodingProgressBar.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new Callback());
        webView.loadUrl("https://www.google.com");
        urlInput.setText("https://www.google.com");
        hideKeyboared();

       /* //Used to specify minimum number of
        //characters the user has to type in order to display the drop down hint.
        urlInput.setThreshold(1);*/

        dbConnection = new DBConnection(this);

        webPageArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        urlInput.setAdapter(webPageArrayAdapter);


        urlInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    showPage(null);
                    return true;
                }

                return false;
            }
        });


    }

    private void hideKeyboared() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // init webPageAdapter
        ArrayList<String> urls = new ArrayList<>();
        for (WebPage webPage : dbConnection.getData()) {
            if (!urls.contains(webPage.getUrl()))
                urls.add(webPage.getUrl());
        }
        webPageArrayAdapter.clear();
        webPageArrayAdapter.addAll(urls);
        webPageArrayAdapter.notifyDataSetChanged();

        // get url for intent data
        Uri uriFromIntent = getIntent().getData();
        if (uriFromIntent != null) {
            urlInput.setText(uriFromIntent.toString());
            webView.loadUrl(uriFromIntent.toString());
        }
    }

    public void showPage(View v) {
        hideKeyboared();
        webView.setWebViewClient(new Callback());
        webView.loadUrl(userInputToUrl(urlInput.getText().toString()));
        WebPage webPage = dbConnection.dataInsert(urlForDB(urlInput.getText().toString()));
        webPageArrayAdapter.add(webPage.getUrl());
        pageLodingProgressBar.setVisibility(View.VISIBLE);
    }

    private String urlForDB(String urlInput) {
        if (urlInput.startsWith("http://")) {
            return urlInput.replace("http://", "");
        } else if (urlInput.startsWith("https://")) {
            return urlInput.replace("https://", "");
        } else {
            return urlInput;
        }
    }

    private String userInputToUrl(String urlInput) {
        if (urlInput.startsWith("http://") || urlInput.startsWith("https://")) {
            return urlInput;
        } else {
            return "http://" + urlInput;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.history_mi) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class Callback extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            pageLodingProgressBar.setVisibility(View.GONE);
        }
    }
}

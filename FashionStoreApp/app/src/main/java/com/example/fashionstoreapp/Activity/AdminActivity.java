package com.example.fashionstoreapp.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionstoreapp.R;
import com.example.fashionstoreapp.Retrofit.RetrofitService;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Find the WebView by its unique ID
        WebView webView = findViewById(R.id.webview);

        // loading https://www.geeksforgeeks.org url in the WebView.
        webView.loadUrl("http://"+ RetrofitService.IPAddress +":8080/signin-admin");
        // this will enable the javascript.
        webView.getSettings().setJavaScriptEnabled(true);

        // WebViewClient allows you to handle
        // onPageFinished and override Url loading.
//        webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //Here check whether the url is after logged in
//                if(url.equals("http://192.168.68.24:8080/logout-admin")){
//                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
//                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("when you click on any interlink on webview that time you got url :-" + url);
                if(url.equals("http://"+ RetrofitService.IPAddress +":8080/redirect")){
                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
}

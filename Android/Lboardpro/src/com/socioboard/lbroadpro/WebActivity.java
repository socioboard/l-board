package com.socioboard.lbroadpro;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class WebActivity extends Activity{

	WebView webview;
	String weburl;
	ProgressBar progressbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_webview);
		webview=(WebView)findViewById(R.id.webview);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			weburl  = extras.getString("WEB_URL");
		}
		
		progressbar=(ProgressBar)findViewById(R.id.progressbar);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebViewClient(new WebViewKeep());
		webview.setInitialScale(1);
		webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		webview.getSettings().setBuiltInZoomControls(true); // Initialize zoom controls for your WebView component
		webview.getSettings().setUseWideViewPort(true); // Initializes double-tap zoom control
		webview.loadUrl(weburl);
		webview.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int newProgress) 
			{
				progressbar.setVisibility(View.VISIBLE);
				if(newProgress==100)
				{
					progressbar.setVisibility(View.INVISIBLE);
				}
				super.onProgressChanged(view, newProgress);
				
			}
		});
		
	}
	
	// Class name: WebViewKeep (Used to 
	private class WebViewKeep extends WebViewClient 
	{
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {

	        if (url.startsWith("ispmobile://")) {

	            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

	            // The following flags launch the application outside the current application
	            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

	            startActivity(intent);

	            return true;
	        }  

	        return false;
	    }
	    @Override
	    public void onReceivedSslError(WebView view, SslErrorHandler handler,
	    		SslError error) {
	    	handler.proceed();
	    }
	}
	
	@Override
	public void onBackPressed() {
	    if (webview.canGoBack()) {
	    	webview.goBack();
	    } else {
	        super.onBackPressed();
	    }
	}
}

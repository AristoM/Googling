package com.aristo.michael.googlegoogle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class HomeActivity extends Activity {

	WebView webView;
	ProgressBar progressBar;
	String url = "http://www.google.com";
	private boolean backPressedToExitOnce = false;
	public static int count = 1;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		webView = (WebView) findViewById(R.id.webView);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);

		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setScrollbarFadingEnabled(true);

		webView.loadUrl(url);

		webView.setOnTouchListener(new OnSwipeTouchListener() {

			@Override
			public boolean onSwipeLeft() {
				return true;
			}

			@Override
			public boolean onSwipeRight() {
				if (backPressedToExitOnce) {
					finish();
				} else {
					backPressedToExitOnce = true;
					Toast.makeText(HomeActivity.this, "Swipe again to exit",
							Toast.LENGTH_SHORT).show();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							backPressedToExitOnce = false;
						}
					}, 2000);
				}
				return true;
			}

			@Override
			public boolean onSwipeBottom() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean onSwipeTop() {
				// TODO Auto-generated method stub
				return true;
			}

		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (webView != null && webView.canGoBack()) {
				webView.goBack();
				return true;
			} else {
				finish();
				return true;
			}
		}

		return true;
	}

	private class MyWebChromeClient extends WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);

			progressBar.setProgress(newProgress);

			if (newProgress == 100)
				progressBar.setVisibility(View.GONE);

		}
	}

	private class MyWebViewClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);

			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			if (url.endsWith(".png") || url.endsWith(".jpg")
					|| url.endsWith(".tif") || url.endsWith(".gif")
					|| url.endsWith(".jpeg") || url.endsWith(".mp3")) {
				Uri source = Uri.parse(url);
				// Make a new request pointing to the .apk url
				DownloadManager.Request request = new DownloadManager.Request(
						source);
				// appears the same in Notification bar while downloading
				request.setDescription("Description for the DownloadManager Bar");
				request.setTitle("image" + count);
				count++;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					request.allowScanningByMediaScanner();
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
				}
				// save the file in the "Downloads" folder of SDCARD
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS, "SmartPigs.apk");
				// get download service and enqueue file
				DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				manager.enqueue(request);
			}

			view.loadUrl(url);
			return true;
		}
	}

}

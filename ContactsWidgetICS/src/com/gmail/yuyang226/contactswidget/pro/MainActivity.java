package com.gmail.yuyang226.contactswidget.pro;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends Activity {
	/**
	 * The webview to show the help html content.
	 */
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.setTitle(R.string.activity_title);
		mWebView = (WebView)this.findViewById(R.id.help_page);
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
	}

	@Override
	public void onStart() {
		super.onStart();
		String htmlFile = getString(R.string.help_file_name);;
		mWebView.loadUrl("file:///android_asset/" + htmlFile);
	}

}

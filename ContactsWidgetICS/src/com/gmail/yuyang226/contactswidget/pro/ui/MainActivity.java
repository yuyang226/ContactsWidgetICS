package com.gmail.yuyang226.contactswidget.pro.ui;

import com.gmail.yuyang226.contactswidget.pro.R;
import com.gmail.yuyang226.contactswidget.pro.R.id;
import com.gmail.yuyang226.contactswidget.pro.R.layout;
import com.gmail.yuyang226.contactswidget.pro.R.string;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MainActivity extends Activity {
	private static final String ENCODING_UTF8 = "utf-8"; //$NON-NLS-1$
	private static final String ASSET_FOLDER = "file:///android_asset/"; //$NON-NLS-1$
	
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
	}

	@Override
	public void onStart() {
		super.onStart();
		String htmlFile = getString(R.string.help_file_name);;
		mWebView.getSettings().setDefaultTextEncodingName(getString(R.string.help_file_encoding, ENCODING_UTF8));
		mWebView.loadUrl(ASSET_FOLDER + htmlFile);
	}

}

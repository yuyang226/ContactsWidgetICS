package com.gmail.yuyang226.contactswidget.pro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
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
	}

	@Override
	public void onStart() {
		super.onStart();
		AssetManager am = getAssets();
		InputStream is = null;
		BufferedReader reader = null;
		try {
			String htmlFile = getString(R.string.help_file_name);;
			is = am.open(htmlFile);
			reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			int ch = reader.read();
			while (ch != -1) {
				sb.append((char) ch);
				ch = reader.read();
			}
			mWebView.loadDataWithBaseURL(htmlFile, sb.toString(), "text/html", "utf-8", null); //$NON-NLS-1$//$NON-NLS-2$
			//mWebView.loadData(sb.toString(), "text/html", "utf-8"); //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
		} finally {
			if( is != null ) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

}

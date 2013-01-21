package com.gmail.yuyang226.contactswidget.pro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.gmail.yuyang226.contactswidget.pro.R;

public class MainActivity extends Activity {
	private static final String ENCODING_UTF8 = "utf-8"; //$NON-NLS-1$
	private static final String ASSET_FOLDER = "file:///android_asset/"; //$NON-NLS-1$
	private static final String APP_LINK = "https://play.google.com/store/apps/details?id=" + R.class.getPackage().getName();
	private static final Uri URI_APP_LINK = Uri.parse(APP_LINK);
	
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.ic_menus, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_item_about:
			PackageManager manager = getApplicationContext().getPackageManager();
			String version = "N/A";
			try {
				PackageInfo info = manager.getPackageInfo(
						getApplicationContext().getPackageName(), 0);
				version = info.versionName;
			} catch (NameNotFoundException e) {
				//ignore
			}
			String aboutContent = getApplicationContext().getString(R.string.about_content, version);
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(R.string.menu_item_about)
			.setMessage(aboutContent)
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					})
			.create().show();
			return true;
		case R.id.menu_item_rate:
			Intent marketIntent = new Intent(
					Intent.ACTION_VIEW, URI_APP_LINK);
			startActivity(marketIntent);
			Toast.makeText(getApplicationContext(), R.string.thanks_rating, Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

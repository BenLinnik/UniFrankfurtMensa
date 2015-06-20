package com.bennystech.unifrankfurtmensa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	private static Context mContext = null;

	public static String readRawTextFile(int id) {

		final InputStream inputStream = mContext.getResources()
				.openRawResource(id);

		final InputStreamReader in = new InputStreamReader(inputStream);
		final BufferedReader buf = new BufferedReader(in);

		String line;

		final StringBuilder text = new StringBuilder();
		try {

			while ((line = buf.readLine()) != null) {
				text.append(line);
			}
		} catch (final IOException e) {
			return null;

		}

		return text.toString();

	}

	public AboutDialog(Context context) {
		super(context);
		mContext = context;
	}

	/**
	 * 
	 * Standard Android on create method that gets called when the activity
	 * initialized.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.about);
		TextView tv = (TextView) findViewById(R.id.legal_text);
		tv.setText(readRawTextFile(R.raw.legal));
		tv = (TextView) findViewById(R.id.info_text);
		tv.setText(Html.fromHtml(readRawTextFile(R.raw.info)));
		tv.setLinkTextColor(Color.BLUE);
		Linkify.addLinks(tv, Linkify.ALL);
	}

}
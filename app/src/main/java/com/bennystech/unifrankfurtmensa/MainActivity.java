package com.bennystech.unifrankfurtmensa;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

public class MainActivity extends FragmentActivity implements
		ActionBar.OnNavigationListener {
	/** Called when the activity is first created. */

	// Uses AsyncTask to create a task away from the main UI thread. This task
	// takes a
	// URL string and uses it to create an HttpUrlConnection. Once the
	// connection
	// has been established, the AsyncTask downloads the contents of the webpage
	// as
	// an InputStream. Finally, the InputStream is converted into a string,
	// which is
	// displayed in the UI by the AsyncTask's onPostExecute method.
	private class DownloadWebpageTask extends AsyncTask<Context, Void, String> {

		private final TableLayout tableToChange;
		private final String url;

		public DownloadWebpageTask(String url, TableLayout table) {
			this.tableToChange = table;
			this.url = url;
		}

		@Override
		protected String doInBackground(Context... arg0) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(url);
			} catch (final IOException e) {
				return "Error";
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != "no internet connection") {
				ParseHTMLAnswer(result, tableToChange);
				title.setText(getFullDayName(GetNextEatDay()));
				title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
			} else {
				title.setText(R.string.no_connection);
				title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
			}
		}
	}

	// private Tracker mGaTracker;
	// private GoogleAnalytics mGaInstance;

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	@SuppressLint("ValidFragment")
	public class DummySectionFragment extends Fragment {
		private AdView adView;
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private static final String MY_AD_UNIT_ID = "ca-app-pub-2004122002724721/3813800091";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(
					R.layout.fragment_main_dummy, container, false);
			// TextView dummyTextView = (TextView) rootView
			// .findViewById(R.id.section_label);
			// dummyTextView.setText(Integer.toString(getArguments().getInt(
			// ARG_SECTION_NUMBER)));

			TableLayout tablePiXGaumen;
			TableLayout tableLevel;
			TableLayout tableDarwin;
			String stringPiXGaumenurl;
			String stringLevelurl;
			String stringDarwinsurl;

			final LinearLayout layout = new LinearLayout(getActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));

			final LinearLayout footer = new LinearLayout(getActivity());
			footer.setOrientation(LinearLayout.VERTICAL);
			final LinearLayout.LayoutParams footerParam = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			footer.setBackgroundColor(Color.parseColor("#005bc1"));

			// Create the adView
			adView = new AdView(getActivity(), AdSize.BANNER, MY_AD_UNIT_ID);
			final AdView.LayoutParams adViewParams = new AdView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			// adViewParams.addRule(RelativeLayout.CENTER_IN_PARENT, -1);
			// adView.setGravity(Gravity.CENTER_HORIZONTAL);

			footer.addView(adView, adViewParams);

			// the next line is the key to putting it on the bottom
			adViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			adView.loadAd(new AdRequest());

			switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
			case 1:
				stringPiXGaumenurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/pi-x-gaumen.html";
				stringLevelurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/level.html";
				stringDarwinsurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/darwins.html";
				break;
			case 2:
				stringPiXGaumenurl = "http://www.studentenwerkfrankfurt.de/essen-trinken/speiseplaene-neu/mensa-casino.html";
				stringLevelurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/anbau-casino.html";
				stringDarwinsurl = "http://www.studentenwerkfrankfurt.de/essen-trinken/speiseplaene-neu/dasein.html";
				break;
			default:
				stringPiXGaumenurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/cafeteria-bockenheim.html";
				stringLevelurl = "http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/hochform.html";
				stringDarwinsurl = "";
				break;
			}

			tablePiXGaumen = new TableLayout(getActivity());
			tablePiXGaumen.setStretchAllColumns(true);
			tablePiXGaumen.setShrinkAllColumns(true);

			tableLevel = new TableLayout(getActivity());
			tableLevel.setStretchAllColumns(true);
			tableLevel.setShrinkAllColumns(true);

			tableDarwin = new TableLayout(getActivity());
			tableDarwin.setStretchAllColumns(true);
			tableDarwin.setShrinkAllColumns(true);

			final TableRow rowTitle = new TableRow(getActivity());
			rowTitle.setGravity(Gravity.CENTER_HORIZONTAL);

			// title column/row
			title = new TextView(getActivity());
			title.setText(R.string.please_click);
			title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			title.setGravity(Gravity.CENTER);
			title.setTypeface(Typeface.SERIF, Typeface.BOLD);
			final TableRow.LayoutParams params = new TableRow.LayoutParams();
			params.span = 2;
			rowTitle.addView(title, params);
			tablePiXGaumen.addView(rowTitle);

			new DownloadWebpageTask(stringPiXGaumenurl, tablePiXGaumen)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			new DownloadWebpageTask(stringLevelurl, tableLevel)
					.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			if (stringDarwinsurl != "") {
				new DownloadWebpageTask(stringDarwinsurl, tableDarwin)
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}

			layout.addView(tablePiXGaumen);
			layout.addView(tableLevel);
			layout.addView(tableDarwin);

			layout.addView(footer, footerParam);

			final ScrollView scrollable = new ScrollView(getActivity());
			scrollable.addView(layout);

			container.addView(scrollable);

			setContentView(container);
			// Log.d("MENU", "FERTIG !");

			return rootView;
		}

	}

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	public static String getFullDayName(int day) {
		final Calendar c = Calendar.getInstance();
		// date doesn't matter - it has to be a Saturday
		c.set(2013, Calendar.OCTOBER, 5, 0, 0, 0);
		c.add(Calendar.DAY_OF_MONTH, day);
		return String.format("%tA", c);
	}

	private TextView title;

	public void AddFoodPriceRowToTable(String food, String price,
			TableLayout tableToChange) {
		final TableRow rowFoodPrice = new TableRow(this);

		// labels for columns
		final LayoutParams layout08 = new TableRow.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 0.85f);
		final LayoutParams layout02 = new TableRow.LayoutParams(0,
				LayoutParams.WRAP_CONTENT, 0.15f);

		final TextView FoodTextView = new TextView(this);
		FoodTextView.setText(Html.fromHtml(food));
		FoodTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		FoodTextView.setLayoutParams(layout08);
		// FoodTextView.setTypeface(Typeface.DEFAULT_BOLD);

		final TextView PriceTextView = new TextView(this);
		PriceTextView.setText(Html.fromHtml(price)); //" &#8364;"));
		PriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		PriceTextView.setTypeface(Typeface.DEFAULT_BOLD);
		PriceTextView.setLayoutParams(layout02);

		rowFoodPrice.addView(FoodTextView);
		rowFoodPrice.addView(PriceTextView);

		tableToChange.addView(rowFoodPrice);
	}

	public void AddMensaName(String name, TableLayout tableToChange) {
		final TableRow namerow = new TableRow(this);

		// labels for columns
		final TextView NameRowTextView = new TextView(this);
		NameRowTextView.setText(name);
		NameRowTextView.setTypeface(Typeface.DEFAULT_BOLD);
		NameRowTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		NameRowTextView.setTextColor(Color.parseColor("#005bc1"));

		final TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;
		namerow.addView(NameRowTextView, params);

		tableToChange.addView(namerow);
	}

	private String downloadUrl(String myurl) throws IOException {
		String myurlfile = myurl
				.replaceAll(
						"http://www.studentenwerkfrankfurt.de/nc/essen-trinken/speiseplaene-neu/",
						"");
		myurlfile = myurlfile
				.replaceAll(
						"http://www.studentenwerkfrankfurt.de/essen-trinken/speiseplaene-neu/",
						"");

		final File file1 = new File(getFilesDir() + "/" + myurlfile);
//		Log.d("MENU", "check2 " + file1.getPath());

		if (file1.exists()) {
//			Log.d("MENU", "exists");
			final long lastModifieddatems = file1.lastModified();

			final Calendar lastmonday = Calendar.getInstance();
			final int day = lastmonday.get(Calendar.DAY_OF_WEEK);
			lastmonday.add(Calendar.DAY_OF_MONTH, (day-2) * -1);
			final int hour = lastmonday.get(Calendar.HOUR_OF_DAY);
			lastmonday.add(Calendar.HOUR_OF_DAY, hour * -1);
			final int minute = lastmonday.get(Calendar.MINUTE);
			lastmonday.add(Calendar.MINUTE, minute * -1);
			final long lastmondayms = lastmonday.getTimeInMillis();
//			Log.d("MENU", "lastmondayms: " + lastmondayms );
//			Log.d("MENU", "lastModifieddatems: " + lastModifieddatems );

			if (lastModifieddatems > lastmondayms) {
//				Log.d("MENU", "not old");
				return readFromFile(myurlfile);
			}
		}

		try {
			final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {

//				Log.d("MENU", "old or new");
				final HttpClient httpClient = new DefaultHttpClient();
				final HttpContext localContext = new BasicHttpContext();
				final HttpGet httpGet = new HttpGet(myurl);
				final HttpResponse response = httpClient.execute(httpGet,
						localContext);
				String result = "";

				final BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "ISO-8859-1"));

				String line = null;
				while ((line = reader.readLine()) != null) {
					result += line + "\n";

					/* Convert the Bytes read to a String. */

					// Log.d("MENU", "line: " +line);
					// Toast.makeText(Connect.this, line.toString(),
					// Toast.LENGTH_LONG).show();

				}
				// Log.d("MENU", myurl);
				// 1. Instantiate an AlertDialog.Builder with its constructor
				// AlertDialog.Builder builder = new AlertDialog.Builder(this);
				//
				// // 2. Chain together various setter methods to set the dialog
				// characteristics
				// builder.setMessage(myurl)
				// .setTitle("");
				//
				// // 3. Get the AlertDialog from create()
				// AlertDialog dialog = builder.create();
				// dialog.show();
				writeToFile(result, myurlfile);
				return result;

				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} else
				return "no internet connection";
		} finally {

		}
	}

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			return getActionBar().getThemedContext();
		else
			return this;
	}

	@SuppressLint("SimpleDateFormat")
	private int GetNextEatDay() {
		final Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		final SimpleDateFormat hourformat = new SimpleDateFormat("HH");
		final int hour = Integer
				.parseInt(hourformat.format(calendar.getTime()));

		
		if (hour > 16) {
			day++;
		}
		if (day == 1 || day > 6) {
			day = 2;
		}
//		 Log.d("MENU", Integer.toString(hour) + " " +  Integer.toString(day));

		return day;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mGaInstance = GoogleAnalytics.getInstance(this);
		// mGaTracker = mGaInstance.getTracker("UA-26499820-3");

		setContentView(R.layout.activity_main);

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(getActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.

		final LinearLayout yourviewGroup = (LinearLayout) findViewById(R.id.container);
		yourviewGroup.removeAllViewsInLayout();

		final Fragment fragment = new DummySectionFragment();
		final Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);

		// mGaTracker.sendView("/Mensa" + Integer.toString(position+1));
		final Tracker v3EasyTracker = EasyTracker.getInstance(this);
		v3EasyTracker.set(Fields.SCREEN_NAME,
				"/Mensa" + Integer.toString(position + 1));

		v3EasyTracker.send(MapBuilder.createAppView().build());

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();

		final SharedPreferences sp = getSharedPreferences(
				"UniFraMensaSettings", Context.MODE_PRIVATE);
		final SharedPreferences.Editor editor = sp.edit();
		editor.putInt("last_location", position);
		editor.commit();

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			final AboutDialog about = new AboutDialog(this);
			about.setTitle(R.string.about_title);

			about.show();
			break;
		}
		return true;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.

		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
		// mGaTracker.sendView("/Start");

		final SharedPreferences sp = getSharedPreferences(
				"UniFraMensaSettings", Context.MODE_PRIVATE);
		final int last_location = sp.getInt("last_location", 0);
		getActionBar().setSelectedNavigationItem(last_location);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	// onPostExecute displays the results of the AsyncTask.
	protected void ParseHTMLAnswer(String result, TableLayout tableToChange) {

		final Vector<String> daylymenu = new Vector<String>();
		final Vector<String> food = new Vector<String>();
		final Vector<String> price = new Vector<String>();
		String foodStr;
		String priceStr;
		String mensaname;
		final Pattern patterndaymenu = Pattern
				.compile(
						"<table class=\\\"tx_hmwbsffmmenu_menu date\\\" style=\\\"width: [0-9]+\\\">(([\\w\\s\\<=\\\"()\\>,.\\/]|.)*?\\<\\/table>)",
						Pattern.MULTILINE);
		final Pattern patternfoodprice = Pattern
				.compile(
						"<div class=\\\"dish\\\">([\\w\\s\\<=\\\"()\\>,.\\/]|.)*?(&euro;|€)<\\/strong>",
						Pattern.MULTILINE);
		final Pattern patternmensaname = Pattern
				.compile(
						"<div class=\"csc-header csc-header-n1\"><h1 class=\"csc-firstHeader\">([\\w\\s]+?)</h1></div>",
						Pattern.MULTILINE);
        final Pattern patterndishname = Pattern
                .compile(
                        "<div class=\\\"dish\\\">([\\w\\s\\<=\\\"\\>\\/]|.)*?<\\/div>",
                        Pattern.MULTILINE);

        final Pattern moneydishname = Pattern
                .compile(
                        "<p class=\\\"price\\\"><strong>(.*?)(&euro;|€)<\\/strong><\\/p>",
                        Pattern.MULTILINE);

        final Pattern menudish = Pattern
                .compile(
                        "<tr class=\\\"menu\\\">([\\w\\s\\<=\\\"()\\>,.\\/]|.)*?<\\/tr>",
                        Pattern.MULTILINE);


		// If current day is Sunday, day=1
		int day;

		// Pattern p = Pattern.compile("(<nobr><b>)(.+?)(,)",
		// Pattern.MULTILINE);
		// final Pattern pattern =
		// Pattern.compile("<nobr><b>.+?, .+?</b></nobr>", Pattern.MULTILINE);
		final Matcher daylymenumatch = patterndaymenu.matcher(result);
		//esult = result.substring(8000, 10000);
        //Log.d("MENU", "result: " + result);

		while (daylymenumatch.find()) {
			daylymenu.add(daylymenumatch.group(1));
			// Log.d("MENU", "daylymenumatch: " + daylymenumatch.group(1));
			// textView.setText(Tagesmenu.group());
		}
		if (daylymenu.size() > 0) {
			final Matcher mensanamematch = patternmensaname.matcher(result);
			if (mensanamematch.find()) {
				// Log.d("MENU", "mensanamematch: " + mensanamematch.group(1));
				mensaname = mensanamematch.group(1);
				AddMensaName(mensaname, tableToChange);
			}
			day = GetNextEatDay();

            if (daylymenu.size()>day - 2) {
                String dayofinterestmenu = daylymenu.elementAt(day - 2);
                dayofinterestmenu = dayofinterestmenu.replaceAll(
                        "<span class=\\\"item\\\">([\\w\\s\\<=\\\"()\\>,.\\/]|.)*?<\\/div>\\s*<p>", "");
                //			dayofinterestmenu = dayofinterestmenu.replaceAll(
                //					"\\(((,)?([A-Z]|[0-9]))+?(,[0-9])*?\\)", "");

                final Matcher todaydish = menudish
                        .matcher(dayofinterestmenu);
                while (todaydish.find()) {
                    final Matcher todayfood = patterndishname
                            .matcher(todaydish.group(0));
                    final Matcher todayfoodprice = moneydishname
                            .matcher(todaydish.group(0));
                    String todayfoodtmp;
                    while (todayfood.find()) {
                        todayfoodtmp = todayfood.group(0).replaceAll("<img ([\\w\\s\\<=\\\"()\\>,.\\/]|.)*?>", "");
                        todayfoodtmp = todayfoodtmp.replaceAll("<\\/p>", "");
                        todayfoodtmp = todayfoodtmp.replaceAll("<p>", "");
                        todayfoodtmp = todayfoodtmp.replaceAll("<strong>", "");
                        todayfoodtmp = todayfoodtmp.replaceAll("\\s\\s", "");
                        //                    Log.d("MENU", "food today22: " + todayfoodtmp);
                        food.add(todayfoodtmp);
                        //				Log.d("MENU", "group 1: " + todayfoodprice.group(1));
                        if (todayfoodprice.find()) {
                            //                        Log.d("MENU", "price today: " + todayfoodprice.group(0));
                            price.add(todayfoodprice.group(0));
                        } else
                            price.add("");
                        //				Log.d("MENU", "group 2: " + todayfoodprice.group(2));
                    }
                }
                daylymenu.clear();

                final Iterator<String> itrfood = food.iterator();
                final Iterator<String> itrprice = price.iterator();
                while (itrfood.hasNext() && itrprice.hasNext()) {
                    foodStr = itrfood.next();
                    priceStr = itrprice.next();

                    AddFoodPriceRowToTable(foodStr, priceStr, tableToChange);

                }

            }
		}
		// textView.setText(result);
	}

	private String readFromFile(String filename) {

		String ret = "";

		try {
			final InputStream inputStream = openFileInput(filename);

			if (inputStream != null) {
				final InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				final BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				final StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (final FileNotFoundException e) {
//			Log.e("login activity", "File not found: " + e.toString());
		} catch (final IOException e) {
//			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return ret;
	}

	private void writeToFile(String data, String filename) {
		FileOutputStream outputStream;
		// Log.d("MENU", "trying to write to");

		try {
			outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
			outputStream.write(data.getBytes());
			outputStream.close();
		} catch (final Exception e) {
			// Log.d("MENU", "wrote to failed");
			e.printStackTrace();
		}
	}

}

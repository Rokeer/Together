package com.quardroiders;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class MainActivity extends TabActivity implements OnTabChangeListener {

	// private ListView allWordsList, personalWordsList;
	private final String DEBUG_TAG = "Together";
	private TabHost tabHost;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;
	private LocationManager mlocationManager;
	final Handler mAHandler = new Handler();
	final Handler mPHandler = new Handler();
	final Handler noticeHandler = new Handler();
	private String mStrMSG;
	private ListView mAWordsList, mPWordsList;
	private List<Map<String, Object>> mAlistStrings, mPlistStrings;
	private DataAccess dataaccess;
	private String mName;
	public static boolean flag = false;
	public static String sLng = "0";
	public static String sLat = "0";
	public String mNotice;
	private ExecutorService exec = Executors.newCachedThreadPool();
	private int myMenuRes[] = {
			R.drawable.tab1,
			R.drawable.tab2
	};

	final Runnable showNotice = new Runnable() {
		public void run() {
			updateNotice();
		}
	};

	private void updateNotice() {
		// update detail
		Toast.makeText(MainActivity.this, mNotice, Toast.LENGTH_SHORT).show();
	}

	final Runnable mUpdateAWL = new Runnable() {
		public void run() {
			updateAWL();
		}
	};

	private void updateAWL() {
		// update detail
		SimpleAdapter sAdapter = (SimpleAdapter) mAWordsList.getAdapter();
		sAdapter.notifyDataSetChanged();
	}

	final Runnable mUpdatePWL = new Runnable() {
		public void run() {
			updatePWL();
		}
	};

	private void updatePWL() {
		// update detail
		SimpleAdapter sAdapter = (SimpleAdapter) mPWordsList.getAdapter();
		sAdapter.notifyDataSetChanged();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		mName = LoginActivity.mName;

		dataaccess = new DataAccess(this);

		// get location from GPS and send

		String serviceName = Context.LOCATION_SERVICE;
		mlocationManager = (LocationManager) getSystemService(serviceName);
		String provider = LocationManager.GPS_PROVIDER;

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		provider = mlocationManager.getBestProvider(criteria, true);

		Location location = mlocationManager.getLastKnownLocation(provider);
		// updateWithNewLocation(location);
		mlocationManager.requestLocationUpdates(provider, 2000, 10,
				locationListener);

		// before this was added by Colin

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		Intent allWordsListIntent = new Intent();
		Bundle allWordsListBundle = new Bundle();
		allWordsListBundle.putString("type", "all");
		allWordsListIntent.setClass(MainActivity.this,
				AllWordsListActivity.class);
		allWordsListIntent.putExtras(allWordsListBundle);

		// startActivity(allWordsListIntent);
		tabHost.addTab(tabHost.newTabSpec("allWordsListTab")
				.setIndicator("Talk", getResources().getDrawable(
						myMenuRes[0])).setContent(allWordsListIntent));
		Intent personalWordsListIntent = new Intent();
		Bundle personalWordsListBundle = new Bundle();
		personalWordsListBundle.putString("type", "personal");
		personalWordsListIntent.setClass(MainActivity.this,
				PersonalWordsListActivity.class);
		personalWordsListIntent.putExtras(personalWordsListBundle);
		// startActivity(personalWordsListIntent);
		tabHost.addTab(tabHost
				.newTabSpec("personalWordsListTab")
				.setIndicator("Whisper",
						getResources().getDrawable(
								myMenuRes[1]))
				.setContent(personalWordsListIntent));
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(0);
		// tabHost.setBackgroundColor(Color.GREEN);

		// mHandler = new Handler();
		// mHandler.post(new ServerHandler(mSocket, mBufferedReader,
		// mPrintWriter));
		// create a thread to handler msg from server
		// exec.execute(new ServerHandler(mSocket, mBufferedReader,
		// mPrintWriter));
		mAWordsList = AllWordsListActivity.wordsList;
		mAlistStrings = AllWordsListActivity.listStrings;
		mPWordsList = PersonalWordsListActivity.wordsList;
		mPlistStrings = PersonalWordsListActivity.listStrings;
		ServerHandler();

		dataaccess.clearBlock();

		mPrintWriter.print("/getblock\n");
		mPrintWriter.flush();

		exec.execute(new HBSender(mSocket, mBufferedReader, mPrintWriter));

	}

	/*
	 * private Runnable update = new Runnable() { public void run() {
	 * updateUI(); mHandler.postDelayed(update, 5); } };
	 */
	protected void ServerHandler() {

		Thread t = new Thread() {

			public void run() {
				Looper.prepare();
				try {
					while (true) {
						mStrMSG = mBufferedReader.readLine();
						if (mStrMSG.trim().startsWith("/all ")) {
							mStrMSG = mStrMSG.trim().substring(5);
							String[] tmp = new String[10];
							tmp = mStrMSG.split(":");
							Map<String, Object> map = new HashMap<String, Object>();
							Time time = new Time();
							time.setToNow();
							map.put("nickName", tmp[0]);
							map.put("time", time.hour + ":" + time.minute + ":"
									+ time.second);
							map.put("word",
									mStrMSG.substring(tmp[0].length() + 1));
							mAlistStrings.add(0, map);
							// high speed ui thread to handle the update
							mAHandler.post(mUpdateAWL);

						} else if (mStrMSG.trim().startsWith("/p ")) {
							mStrMSG = mStrMSG.trim().substring(3);
							String[] tmp = new String[10];
							tmp = mStrMSG.split(":");
							Map<String, Object> map = new HashMap<String, Object>();
							Time time = new Time();
							time.setToNow();
							map.put("nickName", tmp[0]);
							map.put("time", time.hour + ":" + time.minute + ":"
									+ time.second);
							map.put("word",
									mStrMSG.substring(tmp[0].length() + 1));
							mPlistStrings.add(0, map);
							// high speed ui thread to handle the update
							mPHandler.post(mUpdatePWL);

						} else if (mStrMSG.trim().startsWith("/block ")) {
							mStrMSG = mStrMSG.trim().substring(7);
							dataaccess.addBlock(mStrMSG);
						} else if (mStrMSG.trim().startsWith("/friend ")) {
							mStrMSG = mStrMSG.trim().substring(9);
							dataaccess.addFriend(mStrMSG);
						} else if (mStrMSG.trim().startsWith("/latitude ")) {
							sLat = mStrMSG.trim().substring(10);
							Log.v("hey", sLat);
						} else if (mStrMSG.trim().startsWith("/longitude ")) {
							sLng = mStrMSG.trim().substring(11);
							Log.v("hey", sLng);
						} else if (mStrMSG.trim().equals("/finish")) {
							flag = true;
						} else if (mStrMSG.trim().startsWith("/error ")) {
							// Looper.prepare();
							mNotice = mStrMSG.trim().substring(7);
							mPHandler.post(showNotice);

						} else if (mStrMSG.trim().startsWith("/quit ")) {
							// Looper.prepare();
							mNotice = mStrMSG.trim().substring(6);
							mPHandler.post(showNotice);
							LoginActivity.mSocket.close();

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					Log.v("Together", e.toString());
				}

			}
		};
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "Friends List");
		menu.add(1, 2, 2, "Black List");
		menu.add(2, 3, 3, "Send Message");
		// menu.add(2, 4, 4, "Change Account");
		menu.add(2, 4, 4, "Log out");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getTitle().equals("Friends List")) {
			flag = false;
			dataaccess.clearFriend();
			mPrintWriter.print("/getfriend\n");
			mPrintWriter.flush();
			while (!flag)
				;
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, FriendsListActivity.class);
			MainActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Send Message")) {
			// sender to prepare socket
			// MSocket ms = new MSocket();
			// MSocket ms = new MSocket(mSocket, mBufferedReader, mPrintWriter);
			// ms.setSocket(mSocket);
			/*
			 * ms.setBufferedReader(mBufferedReader);
			 * ms.setPrintWriter(mPrintWriter);
			 */

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("name", "");
			intent.setClass(MainActivity.this, InputMSGActivity.class);
			// intent.putExtra("MSocket", ms);
			intent.putExtras(bundle);
			MainActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Black List")) {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, BlockedsListActivity.class);
			MainActivity.this.startActivity(intent);
		}
		/*
		 * else if (menuItem.getTitle().equals("Change Account")) { Intent
		 * intent = new Intent(); intent.setClass(MainActivity.this,
		 * ChangeAccountActivity.class);
		 * MainActivity.this.startActivity(intent); }
		 */
		else if (menuItem.getTitle().equals("Log out")) {

			mPrintWriter.print("/exit\n");
			mPrintWriter.flush();
			Intent it = new Intent();
			setResult(2, it);
			this.finish();
		}

		return super.onOptionsItemSelected(menuItem);
	}

	@Override
	public void onTabChanged(String arg0) {
		// TODO Auto-generated method stub

	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private void updateWithNewLocation(Location location) {

		if (location != null) {
			// get string from edittextbox
			String ulat = "/latitude " + location.getLatitude() + "\n";
			String ulng = "/longitude " + location.getLongitude() + "\n";
			// send to server
			mPrintWriter.print(ulat);
			mPrintWriter.flush();
			mPrintWriter.print(ulng);
			mPrintWriter.flush();
		}

	}

}

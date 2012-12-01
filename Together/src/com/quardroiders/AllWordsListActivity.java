package com.quardroiders;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class AllWordsListActivity extends Activity {

	public String type = new String(), receiver = new String();
	public SimpleAdapter adapter;
	public static List<Map<String, Object>> listStrings = new ArrayList<Map<String, Object>>();
	// public static ListView mWordsList;
	public static ListView wordsList;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;
	private DataAccess dataaccess;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wordslist);
		// mWordsList = wordsList;

		dataaccess = new DataAccess(this);
		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		type = bundle.getString("type");

		wordsList = (ListView) findViewById(R.id.wordsList);

		// listStrings = getWords();
		adapter = new SimpleAdapter(this, listStrings, R.layout.wordslist_item,
				new String[] { "nickName", "time", "word" }, new int[] {
						R.id.nickName, R.id.time, R.id.word });

		wordsList.setAdapter(adapter);

		wordsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				receiver = wordsList.getItemAtPosition(position).toString();
				int end = receiver.indexOf(",");
				receiver = receiver.substring(10, end);
				if (!receiver.equals(LoginActivity.mName)){
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("name", receiver);
					intent.setClass(AllWordsListActivity.this,
							InputMSGActivity.class);
					intent.putExtras(bundle);
					AllWordsListActivity.this.startActivity(intent);
				}
			}
		});
		
		wordsList.setOnItemLongClickListener(new OnItemLongClickListener());

		
		

	}
	
	class OnItemLongClickListener implements android.widget.AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			// TODO Auto-generated method stub
			receiver = wordsList.getItemAtPosition(arg2).toString();
			int end = receiver.indexOf(",");
			receiver = receiver.substring(10, end);
			// after long click to one item on the listview, a context menu
			// would be created.
			if (receiver.equals(LoginActivity.mName)){
				wordsList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("Menu");
						menu.add(0, 1, 0, "Show Location");
					}
				});
			} else {
				wordsList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("Menu");
						menu.add(0, 0, 0, "Whisper");
						menu.add(0, 1, 0, "Show Location");
						menu.add(0, 2, 0, "Add a Friend");
						menu.add(0, 3, 0, "Block!");
					}
				});
			}
			
			return false;
		}

		

	}

	// The action after click one item on the context menu.
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {

		// to find which item being long clicked on the list view.
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		receiver = wordsList.getItemAtPosition(menuInfo.position).toString();

		int end = receiver.indexOf(",");
		receiver = receiver.substring(10, end);

		if (menuItem.getTitle().equals("Show Location")) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			mPrintWriter.print("/getlocation " + receiver + "\n");
			mPrintWriter.flush();
			MainActivity.flag = false;
			while(!MainActivity.flag);
			bundle.putString("lat", MainActivity.sLat);
			bundle.putString("lng", MainActivity.sLng);
			intent.setClass(AllWordsListActivity.this, MapMarkerActivity.class);
			intent.putExtras(bundle);
			AllWordsListActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Whisper")) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("name", receiver);
			intent.setClass(AllWordsListActivity.this, InputMSGActivity.class);
			intent.putExtras(bundle);
			AllWordsListActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Add a Friend")) {
			mPrintWriter.print("/addfriend " + receiver + "\n");
			mPrintWriter.flush();
		} else if (menuItem.getTitle().equals("Block!")) {
			mPrintWriter.print("/addblock " + receiver + "\n");
			mPrintWriter.flush();
			dataaccess.addBlock(receiver);
		}
		return super.onContextItemSelected(menuItem);
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "Refresh");
		return super.onCreateOptionsMenu(menu);
	}

	// It is used to update the listview automatically.
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {

		if (menuItem.getTitle().equals("Refresh")) {
			Map<String, Object> map = new HashMap<String, Object>();
			Time time = new Time();
			time.setToNow();
			map.put("nickName", "tesing");
			map.put("time", time.hour + ":" + time.minute + ":" + time.second);
			map.put("word", "Can't u hear me?");
			listStrings.add(0, map);

			SimpleAdapter sAdapter = (SimpleAdapter) wordsList.getAdapter();
			sAdapter.notifyDataSetChanged();
		}
		return super.onOptionsItemSelected(menuItem);
	}
	 */
	
	/*
	 * public List<Map<String, Object>> getWords() { List<Map<String, Object>>
	 * list = new ArrayList<Map<String, Object>>(); Map<String, Object> map =
	 * null; Time time = new Time();
	 * 
	 * for (int i = 1; i <= 2; i++) { map = new HashMap<String, Object>(); if (i
	 * == 1) { time.setToNow(); map.put("nickName", "Bruce"); map.put("time",
	 * time.hour + ":" + time.minute + ":" + time.second); map.put("word",
	 * "Hey, Man! Where are u? I wanna see u. Could u come and meet me?"); }
	 * else { time.setToNow(); map.put("nickName", "Colin"); map.put("time",
	 * time.hour + ":" + time.minute + ":" + time.second); map.put("word",
	 * "Yes?"); } list.add(0, map); }
	 * 
	 * return list; }
	 */
}

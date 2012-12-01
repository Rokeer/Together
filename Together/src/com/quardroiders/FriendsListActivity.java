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
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class FriendsListActivity extends Activity {

	private ListView friendsList;
	private String selectedFriend = new String();
	private SimpleAdapter adapter;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private DataAccess dataaccess;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendslist);
		dataaccess = new DataAccess(this);

		friendsList = (ListView) findViewById(R.id.friendsList);
		// List<String> listStrings = new ArrayList<String>();

		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		list = getWords();
		adapter = new SimpleAdapter(this, list, R.layout.friendslist_item,
				new String[] { "friendName", "friendPosition" }, new int[] {
						R.id.friendName, R.id.friendPosition });
		// listStrings.add("Bruce");
		// listStrings.add("Colin");
		// listStrings.add("Kidd");
		// listStrings.add("Seven");

		friendsList.setAdapter(adapter);

		friendsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				selectedFriend = friendsList.getItemAtPosition(position)
						.toString();
				int end = selectedFriend.indexOf(",");
				selectedFriend = selectedFriend.substring(12, end);
				bundle.putString("name", selectedFriend);
				intent.setClass(FriendsListActivity.this,
						InputMSGActivity.class);
				intent.putExtras(bundle);
				FriendsListActivity.this.startActivity(intent);
			}
		});

		// after long click to one item on the listview, a context menu
		// would be created.
		friendsList
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("Menu");
						menu.add(0, 0, 0, "Send Message");
						menu.add(0, 1, 0, "Show Location");
						menu.add(0, 2, 0, "Unfriend");
					}
				});

	}

	public List<Map<String, Object>> getWords() {
		Map<String, Object> map = null;
		Madd madd = new Madd();

		try {
			Cursor cur = dataaccess.getFriend();
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				map = new HashMap<String, Object>();
				map.put("friendName", cur.getString(0));
				if (cur.getString(1).equals("0")) {
					map.put("friendPosition", madd.geocodeAddr(
							cur.getString(3), cur.getString(2)));
				} else {
					map.put("friendPosition", "Offline");
				}
				list.add(map);
				cur.moveToNext();
			}
			dataaccess.close();
		} catch (Exception e) {
			Log.v("Together", e.toString());
		}
		return list;

	}

	// The action after click one item on the context menu.
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {

		// to find which item being long clicked on the list view.
		AdapterView.AdapterContextMenuInfo menuInfo;
		menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
		selectedFriend = friendsList.getItemAtPosition(menuInfo.position)
				.toString();

		int end = selectedFriend.indexOf(",");
		selectedFriend = selectedFriend.substring(12, end);

		if (menuItem.getTitle().equals("Show Location")) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("lat", dataaccess.getLat(selectedFriend));
			bundle.putString("lng", dataaccess.getLng(selectedFriend));
			intent.setClass(FriendsListActivity.this, MapMarkerActivity.class);
			intent.putExtras(bundle);
			FriendsListActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Send Message")) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("name", selectedFriend);
			intent.setClass(FriendsListActivity.this, InputMSGActivity.class);
			intent.putExtras(bundle);
			FriendsListActivity.this.startActivity(intent);
		} else if (menuItem.getTitle().equals("Unfriend")) {
			mPrintWriter.print("/delfriend " + selectedFriend + "\n");
			mPrintWriter.flush();
			dataaccess.delFriend(selectedFriend);
			list.remove(friendsList.getItemAtPosition(menuInfo.position));
			SimpleAdapter sAdapter = (SimpleAdapter) friendsList.getAdapter();
			sAdapter.notifyDataSetChanged();
		}
		return super.onContextItemSelected(menuItem);
	}

}

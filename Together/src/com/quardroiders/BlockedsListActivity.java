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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class BlockedsListActivity extends Activity{
	
	private ListView blockedsList;
	private String selectedBlocked = new String();
	private SimpleAdapter adapter;
	private DataAccess dataaccess;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;
	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blockedslist);
		dataaccess = new DataAccess(this);

		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		blockedsList = (ListView) findViewById(R.id.blockedsList);
		list = getWords();
		
		adapter = new SimpleAdapter(this,list,R.layout.blockedslist_item,
				new String[]{"blockedName"},
				new int[]{R.id.blockedName});
		
		blockedsList.setAdapter(adapter);
		/*
		blockedsList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				selectedBlocked = blockedsList.getItemAtPosition(position).toString();
				bundle.putString("name", selectedBlocked);
				intent.setClass(BlockedsListActivity.this, InputMSGActivity.class);
				intent.putExtras(bundle);
				BlockedsListActivity.this.startActivity(intent);
			}
		});
		*/
		//after long click to one item on the listview, a context menu
		//would be created.
		blockedsList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle("Menu");
				menu.add(0, 0, 0, "Unblock");
			}
		});
	}
	
	public List<Map<String, Object>> getWords() {
		
		Map<String, Object> map = null;
		
    	try {
		Cursor cur = dataaccess.getBlock();  
        cur.moveToFirst();  
        while (!cur.isAfterLast()) {  
        	map = new HashMap<String, Object>();
    		map.put("blockedName", cur.getString(0));

    		list.add(map);
    		cur.moveToNext();
        }
        dataaccess.close();
    	} catch (Exception e) {
    	Log.v("Together",e.toString());
    	}
		return list;
	}
	
	//The action after click one item on the context menu.
	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		
		//to find which item being long clicked on the list view.
		AdapterView.AdapterContextMenuInfo menuInfo;
        menuInfo =(AdapterView.AdapterContextMenuInfo)menuItem.getMenuInfo();
		selectedBlocked = blockedsList.getItemAtPosition(menuInfo.position).toString();
		
		if (menuItem.getTitle().equals("Unblock")) {
			selectedBlocked = selectedBlocked.substring(13, selectedBlocked.length()-1);
			mPrintWriter.print("/delblock " + selectedBlocked + "\n");
			mPrintWriter.flush();
			dataaccess.delBlock(selectedBlocked);
			list.remove(blockedsList.getItemAtPosition(menuInfo.position));
			SimpleAdapter sAdapter = (SimpleAdapter) blockedsList.getAdapter();
			sAdapter.notifyDataSetChanged();
			//Log.v("Together",selectedBlocked);
		}
		
		return super.onContextItemSelected(menuItem);  
	}
}

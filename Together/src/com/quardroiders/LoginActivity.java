package com.quardroiders;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	/** Called when the activity is first created. */

	private EditText idText, pwdText;
	private Button loginButton, signUpButton, introButton;

	// debug tag
	private final String DEBUG_TAG = "Together";
	// Server IP and port
	private static final String SERVERIP = "10.0.2.2";
	//private static final String SERVERIP = "192.168.137.1";
	private static final int SERVERPORT = 54321;
	public static Socket mSocket = null;
	public static BufferedReader mBufferedReader = null;
	public static PrintWriter mPrintWriter = null;
	private String mStrMSG = null;
	private DataAccess dataaccess;
	public static String mName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		// connect to server when the app run
		try {
			// connect to server
			mSocket = new Socket(SERVERIP, SERVERPORT);
			// open input & output stream
			mBufferedReader = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
			mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
		} catch (Exception e) {
			// TODO: handle exception
			Log.v(DEBUG_TAG, e.toString());
		}

		idText = (EditText) findViewById(R.id.idText);
		pwdText = (EditText) findViewById(R.id.pwdText);
		dataaccess = new DataAccess(this);

		Cursor cur = dataaccess.getDefault();

		if (cur.moveToNext()) {
			cur.moveToFirst();
			while (!cur.isAfterLast()) {
				idText.setText(cur.getString(0));
				pwdText.setText(cur.getString(1));
				cur.moveToNext();
			}

		}
		dataaccess.close();

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new LoginButtonListener());
		signUpButton = (Button) findViewById(R.id.signUpButton);
		signUpButton.setOnClickListener(new SignUpButtonListener());
		introButton = (Button) findViewById(R.id.introButton);
		introButton.setOnClickListener(new IntroButtonListener());

	}

	class LoginButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			try {
				// get string from id text box
				mName = idText.getText().toString();
				String str = "/username " + mName + "\n";
				// send id to server
				mPrintWriter.print(str);
				mPrintWriter.flush();

				// get string from password text box
				str = "/pwd " + pwdText.getText().toString() + "\n";
				// send id to server
				mPrintWriter.print(str);
				mPrintWriter.flush();

				mStrMSG = mBufferedReader.readLine();
				if (mStrMSG.trim().startsWith("/success ")) {
					mStrMSG = mStrMSG.trim().substring(9);

					dataaccess.setDefault(idText.getText().toString(), pwdText
							.getText().toString());



					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);

					Toast.makeText(LoginActivity.this, mStrMSG,
							Toast.LENGTH_SHORT).show();
					LoginActivity.this.startActivityForResult(intent,2);
					//LoginActivity.this.startActivity(intent);
				} else {
					mStrMSG = mStrMSG.trim().substring(7);
					Toast.makeText(LoginActivity.this, mStrMSG,
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				// TODO: handle exception
				Log.v(DEBUG_TAG, e.toString());
			}
		}

	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	try{
    		int nPid = android.os.Process.myPid();
    		  android.os.Process.killProcess(nPid);
    	}catch(Exception e){
    		
    	}
    	
     }

	class SignUpButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, SignUpActivity.class);
			LoginActivity.this.startActivityForResult(intent,2);
		}

	}

	class IntroButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, IntroActivity.class);
			LoginActivity.this.startActivity(intent);
		}

	}
}
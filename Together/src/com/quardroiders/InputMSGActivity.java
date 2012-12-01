package com.quardroiders;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InputMSGActivity extends Activity {

	private final String DEBUG_TAG = "Together";
	private String receiver = new String();
	private EditText text;
	private Button submitButton;
	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.input_msg);

		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		// get socket, br, and pw from login activity
		// Intent intent = getIntent();
		// MSocket ms = (MSocket) intent.getSerializableExtra("MSocket");
		// mSocket = ms.getSocket();
		/*
		 * mBufferedReader = ms.getBufferedReader(); mPrintWriter =
		 * ms.getPrintWriter();
		 */

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		receiver = bundle.getString("name");

		text = (EditText) findViewById(R.id.inputMSG);
		submitButton = (Button) findViewById(R.id.submitButton);
		if (!receiver.equals(""))
			text.setText("To " + receiver + ": ");

		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String str = null;
				try {
					// get string from edittextbox
					if (!text.getText().toString().equals("")) {
						if (receiver.equals("")) {

							str = "/all " + text.getText().toString() + "\n";
							// send to server
							mPrintWriter.print(str);
							mPrintWriter.flush();

						} else {

							str = "/chatwith " + receiver + "\n";
							mPrintWriter.print(str);
							mPrintWriter.flush();

							str = "/tosomeone " + text.getText().toString()
									+ "\n";
							mPrintWriter.print(str);
							mPrintWriter.flush();

						}
					}
					finish();

				} catch (Exception e) {
					// TODO: handle exception
					Log.v(DEBUG_TAG, e.toString());
				}
			}
		});
	}

}

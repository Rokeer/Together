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
import android.widget.Toast;

public class SignUpActivity extends Activity{

	private EditText idText, pwdText, verpwdText;
	private Button submitButton;

	private BufferedReader mBufferedReader = null;
	private PrintWriter mPrintWriter = null;
	private Socket mSocket = null;
	private String mStrMSG = null;
	private DataAccess dataaccess = null;
	private final String DEBUG_TAG = "Together";
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
	
		mSocket = LoginActivity.mSocket;
		mBufferedReader = LoginActivity.mBufferedReader;
		mPrintWriter = LoginActivity.mPrintWriter;
		
		dataaccess = new DataAccess(this);
        
        idText = (EditText) findViewById(R.id.idText);
        pwdText = (EditText) findViewById(R.id.pwdText);
        verpwdText = (EditText) findViewById(R.id.verpwdText);
        
        
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new SubmitButtonListener());
	}
	
	class SubmitButtonListener implements OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		try {
				// get string from id text box
    			if (pwdText.getText().toString().equals(verpwdText.getText().toString())){
    				mStrMSG = "/signup "+idText.getText().toString()+"&"+pwdText.getText().toString()+"\n";
    				// send id to server
    				mPrintWriter.print(mStrMSG);
    				mPrintWriter.flush();
    				
    				mStrMSG = mBufferedReader.readLine();
    				if (mStrMSG.trim().startsWith("/success ")) {
    					
    					mStrMSG = mStrMSG.trim().substring(9);
    					Toast.makeText(SignUpActivity.this, mStrMSG,
    							Toast.LENGTH_SHORT).show();
    					LoginActivity.mName = idText.getText().toString();
    					String str = "/username " + LoginActivity.mName + "\n";
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
        					intent.setClass(SignUpActivity.this, MainActivity.class);

        					Toast.makeText(SignUpActivity.this, mStrMSG,
        							Toast.LENGTH_SHORT).show();
        					SignUpActivity.this.startActivityForResult(intent,2);
        					//LoginActivity.this.startActivity(intent);
        				} else {
        					mStrMSG = mStrMSG.trim().substring(7);
        					Toast.makeText(SignUpActivity.this, mStrMSG,
        							Toast.LENGTH_SHORT).show();
        				}
    					
    					
    				} else {
    					mStrMSG = mStrMSG.trim().substring(7);
    					Toast.makeText(SignUpActivity.this, mStrMSG,
    							Toast.LENGTH_SHORT).show();
    				}
    				
    			} else {
    				Toast.makeText(SignUpActivity.this, "Please input the same password",
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
    		Intent it = new Intent();
			setResult(2, it);
			this.finish();
    	}catch(Exception e){
    		
    	}
    	
     }
}

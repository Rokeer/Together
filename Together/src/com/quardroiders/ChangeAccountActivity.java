package com.quardroiders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeAccountActivity extends Activity{
	
	private EditText idText, pwdText;
	private Button submitButton;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
	
        idText = (EditText) findViewById(R.id.idText);
        pwdText = (EditText) findViewById(R.id.pwdText);
        
        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new SubmitButtonListener());
	}
	
	class SubmitButtonListener implements OnClickListener {
    	
    	@Override
    	public void onClick(View v) {
    		Intent intent = new Intent();
			intent.setClass(ChangeAccountActivity.this, MainActivity.class);
			ChangeAccountActivity.this.startActivity(intent);
    	}
    
    }
}

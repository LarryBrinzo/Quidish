package com.quidish.anshgupta.login.Network;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.quidish.anshgupta.login.R;

public class No_InternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no__internet);

        Button but=findViewById(R.id.button);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = ConnectivityReceiver.isConnected();
                showDialog(isConnected);
            }
        });

    }

    private void showDialog(boolean isConnected)
    {
        if (isConnected) {
            super.onBackPressed();
        }

    }

}

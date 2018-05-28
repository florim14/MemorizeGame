package com.hamiti.florim.memorizegame.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.hamiti.florim.memorizegame.R;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private Button newGame, exitGame, displayScore, optionsMenu, multiplayerGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        Initialize();

        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleOrNot = new Intent(getApplicationContext(), GameActivity.class);
                singleOrNot.putExtra("SingleOrNot", "Single");
                if (isNetworkAvailable())
                    startActivity(singleOrNot);
                else
                    Toast.makeText(MainActivity.this, "Please connect to internet if you want to play!", Toast.LENGTH_SHORT).show();
            }
        });

        multiplayerGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleOrNot = new Intent(getApplicationContext(), GameActivity.class);
                singleOrNot.putExtra("SingleOrNot", "Multi");
                if (isNetworkAvailable())
                    startActivity(singleOrNot);
                else
                    Toast.makeText(MainActivity.this, "Please connect to internet if you want to play!", Toast.LENGTH_SHORT).show();
            }
        });

        displayScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scorAndOptionsIntent = new Intent(getApplicationContext(), ScoreAndOptionsActivity.class);
                scorAndOptionsIntent.putExtra("Type", "Score");
                startActivity(scorAndOptionsIntent);
            }
        });

        optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scorAndOptionsIntent = new Intent(getApplicationContext(), ScoreAndOptionsActivity.class);
                scorAndOptionsIntent.putExtra("Type", "Options");
                startActivity(scorAndOptionsIntent);
            }
        });

        exitGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void Initialize() {
        newGame = (Button) findViewById(R.id.start_game);
        exitGame = (Button) findViewById(R.id.exit);
        displayScore = (Button) findViewById(R.id.high_score);
        optionsMenu = (Button) findViewById(R.id.options);
        multiplayerGame = (Button) findViewById(R.id.multiplayer);
    }
}
package com.hamiti.florim.memorizegame.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.hamiti.florim.memorizegame.R;
import com.hamiti.florim.memorizegame.adapters.CustomAdapterListView;
import com.hamiti.florim.memorizegame.utils.DataModelForListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScoreAndOptionsActivity extends AppCompatActivity {

    ArrayList<DataModelForListView> dataModels;
    ListView listView;
    private static CustomAdapterListView adapter;
    String[] dateOfPoints = null;
    String[] points = null;
    HashMap<String,String> map = new HashMap<String, String>();

    final String PREF_NAME = "high_score";
    final String PREF_NAME_2 = "options";

    private Spinner timeSpinner, levelSpinner, typeSpinner;
    private Button saveOptions;
    private LinearLayout optionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_and_options);

        Initialize();

        String fromWhichOptions ="";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                fromWhichOptions= null;
            } else {
                fromWhichOptions= extras.getString("Type");
            }
        } else {
            fromWhichOptions= (String) savedInstanceState.getSerializable("Type");
        }

        if (fromWhichOptions.equals("Score")) {
            optionsList.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            scoreList();
        } else if (fromWhichOptions.equals("Options")){
            listView.setVisibility(View.GONE);
            optionsList.setVisibility(View.VISIBLE);
            spinnerOptions();
            saveOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref = getSharedPreferences(PREF_NAME_2, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putString("type", typeSpinner.getSelectedItem().toString());
                    editor.putString("time", timeSpinner.getSelectedItem().toString());
                    editor.putString("level",levelSpinner.getSelectedItem().toString());
                    editor.commit();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            });
        }

    }

    public void scoreList(){
        dataModels= new ArrayList<>();

        SharedPreferences sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        dateOfPoints = (sharedPref.getString("date", "")).split("##");
        points = (sharedPref.getString("points", "")).split("##");
        String output = "";
        for (int i = 0; i<dateOfPoints.length; i++) {
            map.put(dateOfPoints[i], points[i]);
            output += dateOfPoints[i] + " " + points[i];
        }

        Log.d("piket", output + "\npoints: " + sharedPref.getString("points", "") + "\ndate: " + sharedPref.getString("date",""));

        map = (HashMap<String, String>) sortByValue(map);
        for ( Map.Entry<String, String> entry : map.entrySet())
            dataModels.add(new DataModelForListView(entry.getKey().toString(), entry.getValue().toString()));

        adapter= new CustomAdapterListView(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
    }

    public void spinnerOptions(){
        String[] items = new String[]{"Easy", "Medium", "Hard"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        timeSpinner.setAdapter(adapter);

        items = new String[]{"4x4", "4x5"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        levelSpinner.setAdapter(adapter);

        items = new String[]{"Cars", "Dogs", "Cities"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        typeSpinner.setAdapter(adapter);
    }

    private Map<String, String> sortByValue(Map<String, String> unsortMap) {

        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>(unsortMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (Integer.valueOf(o2.getValue())).compareTo(Integer.valueOf(o1.getValue()));
            }
        });

        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }


        return sortedMap;
    }

    private void Initialize() {
        listView = (ListView) findViewById(R.id.display_score_list);

        timeSpinner = (Spinner) findViewById(R.id.spinner_time);
        levelSpinner = (Spinner) findViewById(R.id.spinner_level);
        typeSpinner = (Spinner) findViewById(R.id.spinner_type);

        saveOptions = (Button)findViewById(R.id.save_options);

        optionsList = (LinearLayout) findViewById(R.id.options_list);
    }
}

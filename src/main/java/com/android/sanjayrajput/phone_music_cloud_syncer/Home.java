package com.android.sanjayrajput.phone_music_cloud_syncer;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Home extends ActionBarActivity {
    private static final Logger logger = Logger.getLogger("Home");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SDCardAdapter adapter = new SDCardAdapter();
        List<File> allAudioFiles = SDCardAdapter.getAllAudioFiles();
        if (!allAudioFiles.isEmpty()) {
            logger.info("total audio files: " + allAudioFiles.size());
        }
        final TextView picList = (TextView) findViewById(R.id.audioCount);
        picList.setText("Total Audio Files: " + allAudioFiles.size());
        final ListView filesListView = (ListView) findViewById(R.id.files);
        List<String> fileNames = new ArrayList<>();
        for (File f : allAudioFiles) {
            fileNames.add(f.getAbsolutePath());
        }
        ArrayAdapter<String> aaAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileNames);
        filesListView.setAdapter(aaAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

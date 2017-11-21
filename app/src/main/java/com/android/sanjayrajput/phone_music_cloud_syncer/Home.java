package com.android.sanjayrajput.phone_music_cloud_syncer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class Home extends AppCompatActivity {
    private static final Logger logger = Logger.getLogger("Home");

    private static final int COMPLETE_AUTHORIZATION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        List<File> localFiles = fetchLocalMusic();
        fetchCloudMusic(localFiles);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case COMPLETE_AUTHORIZATION_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    // App is authorized, you can go back to sending the API request
                    Toast.makeText(this, "authorized!!!", Toast.LENGTH_LONG).show();
                } else {
                    // User denied access, show him the account chooser again
                    Toast.makeText(this, "access denied!!!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void fetchCloudMusic(List<File> localFiles) {
        int hasAccountPermission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
        if (hasAccountPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.GET_ACCOUNTS}, 0);
        }

        GoogleDriveAdapter googleDriveAdapter = new GoogleDriveAdapter(this,
                getApplicationContext(),
                localFiles,
                "sanjay.rajputcse@gmail.com",
                (TextView) findViewById(R.id.cloudAudioCount),
                (ListView) findViewById(R.id.cloudFiles),
                (TextView) findViewById(R.id.newCloudAudioCount),
                (ListView) findViewById(R.id.newCloudFiles),
                (TextView) findViewById(R.id.newLocalAudioCount),
                (ListView) findViewById(R.id.newLocalFiles));
        googleDriveAdapter.execute();
    }

    private List<File> fetchLocalMusic() {
        int hasStoragePermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        List<File> phoneAudioFiles = SDCardAdapter.getAllAudioFiles();
        if (!phoneAudioFiles.isEmpty()) {
            logger.info("total audio files: " + phoneAudioFiles.size());
        }
        final TextView picList = (TextView) findViewById(R.id.phoneAudioCount);
        final ListView filesListView = (ListView) findViewById(R.id.phoneFiles);

        List<String> fileNames = new ArrayList<>();
        long size = 0;
        for (File f : phoneAudioFiles) {
            fileNames.add("[" + Utils.getFileSize(f.length()) + "] " + f.getName());
            size += f.length();
        }

        picList.setText("phone audio files: " + phoneAudioFiles.size() + ", size: " + Utils.getFileSize(size));
        ArrayAdapter<String> aaAdapter = new ArrayAdapter<String>(this,
                R.layout.song_details, fileNames);
        filesListView.setAdapter(aaAdapter);
        return phoneAudioFiles;
    }
}

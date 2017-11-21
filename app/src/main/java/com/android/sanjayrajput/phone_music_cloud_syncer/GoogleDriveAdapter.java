package com.android.sanjayrajput.phone_music_cloud_syncer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sanjay.rajput on 02/10/17.
 */
public class GoogleDriveAdapter extends AsyncTask<String, Void, List<File>> {

    private static final Logger logger = Logger.getLogger("GoogleDriveAdapter");
    private static final String MY_MUSIC_FOLDER_ID = "0B9mh_-WKGdSYMjloQVBlMUVmM00";

    private Context activityContext;
    private Context applicationContext;
    private String account;
    private TextView cloudFilesTextView;
    private ListView cloudFilesListView;
    private List<java.io.File> localFiles;
    private TextView newCloudFilesTextView;
    private ListView newCloudFilesListView;
    private TextView newLocalFilesTextView;
    private ListView newLocalFilesListView;

    private Drive drive;

    public GoogleDriveAdapter(Context activityContext,
                              Context applicationContext,
                              List<java.io.File> localFiles,
                              String account,
                              TextView cloudFilesTextView,
                              ListView cloudFilesListView,
                              TextView newCloudFilesTextView,
                              ListView newCloudFilesListView,
                              TextView newLocalFilesTextView,
                              ListView newLocalFilesListView) {
        this.activityContext = activityContext;
        this.applicationContext = applicationContext;
        this.account = account;
        this.localFiles = localFiles;
        this.cloudFilesTextView = cloudFilesTextView;
        this.cloudFilesListView = cloudFilesListView;
        this.newCloudFilesTextView = newCloudFilesTextView;
        this.newCloudFilesListView = newCloudFilesListView;
        this.newLocalFilesTextView = newLocalFilesTextView;
        this.newLocalFilesListView = newLocalFilesListView;
    }

    @Override
    protected List<File> doInBackground(String[] params) {
        return authorize(account);
    }

    private List<com.google.api.services.drive.model.File> authorize(String accountName) {
        try {
            GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(activityContext, Collections.singletonList(DriveScopes.DRIVE));
            credential.setSelectedAccountName(accountName);
            drive = new com.google.api.services.drive.Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                    .setApplicationName("My Music on Cloud")
                    .build();
            return readAllFiles();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private List<File> readAllFiles() throws IOException {
        FileList result = drive.files().list()
                .setPageSize(1000)
                .setQ("'" + MY_MUSIC_FOLDER_ID +"' in parents")
                .setFields("nextPageToken, files(id, name, originalFilename, fullFileExtension, fileExtension, size, description, mimeType)")
                .execute();
        return result.getFiles();
    }

    @Override
    protected void onPostExecute(List<File> cloudAudioFiles) {
        List<String> fileNames = new ArrayList<>();
        long size = 0;
        for (com.google.api.services.drive.model.File f : cloudAudioFiles) {
            fileNames.add("[" + Utils.getFileSize(f.getSize()) + "] " + f.getName());
            size += f.getSize();
        }
        cloudFilesTextView.setText("drive audio files: " + cloudAudioFiles.size() + " , size: " + Utils.getFileSize(size));
        ArrayAdapter<String> aaAdapter = new ArrayAdapter<String>(applicationContext,
                R.layout.song_details,
                fileNames);
        cloudFilesListView.setAdapter(aaAdapter);

        //---------- cloud files --------
        List<String> newCloudFiles = new ArrayList<>();
        size = 0;
        for (File caf : cloudAudioFiles) {
            boolean exist = false;
            for (java.io.File lf : localFiles) {
                if (caf.getName().equalsIgnoreCase(lf.getName())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                newCloudFiles.add("[" + Utils.getFileSize(caf.getSize()) + "] " + caf.getName());
                size += caf.getSize();
            }
        }
        newCloudFilesTextView.setText("new cloud files: " + newCloudFiles.size() + " , size: " + Utils.getFileSize(size));
        ArrayAdapter<String> cAdapter = new ArrayAdapter<String>(applicationContext,
                R.layout.song_details,
                newCloudFiles);
        newCloudFilesListView.setAdapter(cAdapter);

        //---------- local --------
        List<String> newLocalFiles = new ArrayList<>();
        size = 0;
        for (java.io.File lf : localFiles) {
            boolean exist = false;
            for (File caf : cloudAudioFiles) {
                if (caf.getName().equalsIgnoreCase(lf.getName())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                newLocalFiles.add("[" + Utils.getFileSize(lf.length()) + "] " + lf.getName());
                size += lf.length();
            }
        }
        newLocalFilesTextView.setText("new local files: " + newLocalFiles.size() + " , size: " + Utils.getFileSize(size));
        ArrayAdapter<String> lAdapter = new ArrayAdapter<String>(applicationContext,
                R.layout.song_details,
                newLocalFiles);
        newLocalFilesListView.setAdapter(lAdapter);
    }
}

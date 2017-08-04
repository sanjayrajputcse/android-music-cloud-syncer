package com.android.sanjayrajput.phone_music_cloud_syncer;

import android.media.MediaMetadataRetriever;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sanjay.rajput on 05/08/17.
 */
public class SDCardAdapter {

    private static final Logger logger = Logger.getLogger("SDCardAdapter");
    private static List<String> musicFolders;
    private static List<File> allAudioFiles;
    private static File sdCardDirectory;
    private static final List<String> AUDIO_FILES_EXTENSIONS = Arrays.asList(".mp3", ".mp4", ".wav");

    static {
        try {
            musicFolders = Arrays.asList("/SHAREit/audios/Music");
            sdCardDirectory = Environment.getExternalStorageDirectory();
            allAudioFiles = new ArrayList<>();
            for (String folder : musicFolders) {
                String absoluteFolderPath = sdCardDirectory.getAbsolutePath() + folder;
                logger.info("abPath: " + absoluteFolderPath);
                List<File> audioFiles = getAudioFilesInFolder(absoluteFolderPath);
                if (!audioFiles.isEmpty()) {
                    allAudioFiles.addAll(audioFiles);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception: " + e.getMessage());
        }
    }

    private static List<File> getAudioFilesInFolder(String filePath) {
        logger.info("file: " + filePath);
        final File file = new File(filePath);
        if (file.isDirectory()) {
            List<File> audioFiles = new ArrayList<>();
            File[] subFilesAndFolders = file.listFiles();
            if (subFilesAndFolders != null) {
                for (File f : subFilesAndFolders) {
                    List<File> childFiles = getAudioFilesInFolder(f.getAbsolutePath());
                    if (!childFiles.isEmpty()) {
                        audioFiles.addAll(childFiles);
                    }
                }
            }
            return audioFiles;
        } else {
            int colonIndex = filePath.lastIndexOf(".");
            String extension = filePath.substring(colonIndex);
            if (!file.isHidden() && AUDIO_FILES_EXTENSIONS.contains(extension)) {
                return new ArrayList<File>(){{add(file);}};
            }
        }
        return new ArrayList<>();
    }

    public static List<File> getAllAudioFiles() {
        return allAudioFiles;
    }
}

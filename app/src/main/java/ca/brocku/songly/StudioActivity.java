package ca.brocku.songly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StudioActivity extends AppCompatActivity implements MediaPlayer.OnTimedTextListener{

    MediaPlayer player; //For instrumental plavback
    MediaPlayer rPlayer; //For playing recordings

    //For Recording
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    MediaRecorder recorder = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    //For TimedText
    int currTrackIndex;
    private static Handler handler = new Handler();
    TextView lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);
        lyrics = (TextView) findViewById(R.id.lyrics);
        player = MediaPlayer.create(this, R.raw.neverinst);

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/recording.3gp";

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);

        // Check if user has already allowed audio recording
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                //If not, request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        200);

        } else {
            // Permission has already been granted
        }
        //For selecting and assigning subtitles
        try {
            player.addTimedTextSource(getSubtitleFile(R.raw.neverlyrics), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            int textTrackIndex = findTrackIndexFor(
                    MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, player.getTrackInfo());
            if (textTrackIndex >= 0) {
                currTrackIndex = textTrackIndex;
                player.selectTrack(textTrackIndex);
            }
            player.setOnTimedTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //For handling permission request response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ){
            lyrics.setText("Please approve recording permission before continuing");
            finish();
        }

    }
    //Finds index needed to pair TimedText with
    private int findTrackIndexFor(int mediaTrackType, MediaPlayer.TrackInfo[] trackInfo) {
        int index = -1;
        for (int i = 0; i < trackInfo.length; i++) {
            if (trackInfo[i].getTrackType() == mediaTrackType) {
                return i;
            }
        }
        return index;
    }
    //Gets file path for subtitles (copies it from res to local if not done already)
    private String getSubtitleFile(int resId) {
        String fileName = getResources().getResourceEntryName(resId);
        File subtitleFile = getFileStreamPath(fileName);
        if (subtitleFile.exists()) {
            return subtitleFile.getAbsolutePath();
        }

        // Copy the file from the res/raw folder to your app folder on the device
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = getResources().openRawResource(resId);
            outputStream = new FileOutputStream(subtitleFile, false);
            copyFile(inputStream, outputStream);
            return subtitleFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(inputStream, outputStream);
        }
        return "";
    }

    //A method for copying files
    private void copyFile(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    // A handy method used to close all the streams
    private void closeStreams(Closeable... closeables) {
        if (closeables != null) {
            for (Closeable stream : closeables) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    //The method for updating karaoke lyrics
    @Override
    public void onTimedText(MediaPlayer mp, TimedText text) {
        String l = text.getText();
        lyrics.setText(l);
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
        if (rPlayer != null){
            rPlayer.release();
            rPlayer = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
        stopRecording();
    }

    //Studio Mode recording methods
    public void toggleRecording(View v) {
        if(recorder==null) {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("AudioRecordTest", "prepare() failed");
            }

            recorder.start();
            if (player != null) {
                player.start();
            }
        }
        else{
            stopRecording();
            stopPlayer();
        }
    }

    private void stopRecording() {
        if(recorder!=null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    public void toggleRecordedAudio(View v){
        if(rPlayer!=null){
            rPlayer.stop();
            rPlayer.release();
            rPlayer = null;
        }
        else {
            rPlayer = new MediaPlayer();
            try {
                rPlayer.setDataSource(fileName);
                rPlayer.prepare();
                rPlayer.start();
            } catch (IOException e) { //Does nothing
            }
        }

    }
}

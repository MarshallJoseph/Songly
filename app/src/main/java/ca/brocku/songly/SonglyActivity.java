package ca.brocku.songly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SonglyActivity extends AppCompatActivity implements MediaPlayer.OnTimedTextListener{

    private static Handler handler = new Handler();
    TextView lyrics;
    ImageView sButt,plButt,paButt,rButt;
    static MediaPlayer player;

    int currTrackIndex;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songly);

        lyrics = (TextView) findViewById(R.id.lyrics);
        //For carrying over lyrics when switching to landscape
        String carryOver = "";
        if (savedInstanceState!=null)
            carryOver = savedInstanceState.getString("currentDisplay");
        lyrics.setText(carryOver);

        // Hides the action bar
        getSupportActionBar().hide();

        // Hides the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if(player==null) {
            player = MediaPlayer.create(this, R.raw.neverinst);
        }
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
        sButt = findViewById(R.id.stop);
        plButt = findViewById(R.id.play);
        rButt = findViewById(R.id.restart);
    }

    @Override
    protected void onSaveInstanceState(Bundle stuff) {
        stuff.putString("currentDisplay",(String)lyrics.getText());
        super.onSaveInstanceState(stuff);
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

    public void play (View v) {
        if (player != null) {
            player.start();
        }
    }

    public void stop (View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void restart(View v){
        if (player != null)
            stopPlayer();
        player = MediaPlayer.create(this, R.raw.never);
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
        player.start();

    }
}

package ca.brocku.songly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SonglyActivity extends AppCompatActivity implements MediaPlayer.OnTimedTextListener{

    TextView lyrics;
    ImageView sButt,plButt,rButt;
    static MediaPlayer mp;
    ImageButton songlySongSelect;
    final String[] songNames = {"'Africa' - by Toto", "'All Star' - by Smash Mouth", "'Baby I'm Yours' - by Breakbot", "'Jukebox Hero' - by Foreigner", "'Never Gonna Give You Up' - by Rick Astley"};
    final int[] resID = {R.raw.africainst, R.raw.allstarinst, R.raw.babyimyoursinst, R.raw.jukeboxinst, R.raw.neverinst};
    final int[] lyricsID = {R.raw.africalyrics, R.raw.allstarlyrics, R.raw.babyimyourslyrics, R.raw.jukeboxlyrics, R.raw.neverlyrics};

    int globalPosition;
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

        songlySongSelect = (ImageButton) findViewById(R.id.songly_song_select);
        songlySongSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSong();
            }
        });

        sButt = findViewById(R.id.stop);
        plButt = findViewById(R.id.studio_play);
        rButt = findViewById(R.id.restart);
    }

    private void selectSong() {
        // Initialize dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.song_list);
        dialog.setTitle("Select A Song");

        // Initialize adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songNames);

        // Initializes ListView for song list
        ListView songList = (ListView) dialog.findViewById(R.id.song_list_visualizer);

        // Set listeners for the song list
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mp != null) {
                    stopPlayer();
                }
                if (mp == null) {
                    mp = MediaPlayer.create(view.getContext(), resID[position]);
                }
                try {
                    mp.addTimedTextSource(getSubtitleFile(lyricsID[position]), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
                    int textTrackIndex = findTrackIndexFor(
                            MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mp.getTrackInfo());
                    if (textTrackIndex >= 0) {
                        currTrackIndex = textTrackIndex;
                        mp.selectTrack(textTrackIndex);
                    }
                    mp.setOnTimedTextListener((MediaPlayer.OnTimedTextListener) view.getContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                globalPosition = position;
                dialog.dismiss();
            }

        });

        // Adds adapter to song list
        songList.setAdapter(adapter);

        // Display dialog
        dialog.show();
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
        if (mp != null) {
            mp.start();
        }
    }

    public void stop (View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (mp != null) {
            mp.release();
            mp = null;
        }
    }

    public void restart(View v){
        if (mp != null)
            stopPlayer();
        mp = MediaPlayer.create(this, resID[globalPosition]);
        try {
            mp.addTimedTextSource(getSubtitleFile(lyricsID[globalPosition]), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            int textTrackIndex = findTrackIndexFor(
                    MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mp.getTrackInfo());
            if (textTrackIndex >= 0) {
                currTrackIndex = textTrackIndex;
                mp.selectTrack(textTrackIndex);
            }
            mp.setOnTimedTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mp.start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
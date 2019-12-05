package ca.brocku.songly;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.TimedText;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
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

public class StudioActivity extends AppCompatActivity implements MediaPlayer.OnTimedTextListener{

    MediaPlayer mp; //For instrumental plavback
    MediaPlayer mpRecord; //For playing recordings

    private static String fileName = null;
    MediaRecorder recorder = null;

    //For TimedText
    int currTrackIndex;
    TextView lyrics;
    ImageButton studioSongSelect;
    final String[] songNames = {"'Africa' - by Toto", "'All Star' - by Smash Mouth", "'Baby I'm Yours' - by Breakbot", "'Jukebox Hero' - by Foreigner", "'Never Gonna Give You Up' - by Rick Astley"};
    final int[] resID = {R.raw.africainst, R.raw.allstarinst, R.raw.babyimyoursinst, R.raw.jukeboxinst, R.raw.neverinst};
    final int[] lyricsID = {R.raw.africalyrics, R.raw.allstarlyrics, R.raw.babyimyourslyrics, R.raw.jukeboxlyrics, R.raw.neverlyrics};
    int globalPosition;
    ImageView stop, play;
    CheckBox bassboost, equalizer, loudness, acoustic;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        // Hides the action bar
        getSupportActionBar().hide();

        // Hides the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        lyrics = (TextView) findViewById(R.id.lyrics);
        mp = MediaPlayer.create(this, R.raw.africainst);
        globalPosition = 0;

        // initialize first song lyrics before song selected
        try {
            mp.addTimedTextSource(getSubtitleFile(lyricsID[0]), MediaPlayer.MEDIA_MIMETYPE_TEXT_SUBRIP);
            int textTrackIndex = findTrackIndexFor(
                    MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT, mp.getTrackInfo());
            if (textTrackIndex >= 0) {
                currTrackIndex = textTrackIndex;
                mp.selectTrack(textTrackIndex);
            }
            mp.setOnTimedTextListener((MediaPlayer.OnTimedTextListener) this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Record to the external cache directory for visibility
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/recording.3gp";

        stop = (ImageView) findViewById(R.id.studio_stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayer();
            }
        });

        play = (ImageView) findViewById(R.id.studio_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        studioSongSelect = (ImageButton) findViewById(R.id.studio_song_select);
        studioSongSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSong();
            }
        });

        bassboost = (CheckBox) findViewById(R.id.switch_bassboost);
        equalizer = (CheckBox) findViewById(R.id.switch_equalizer);
        loudness = (CheckBox) findViewById(R.id.switch_loudness);
        acoustic = (CheckBox) findViewById(R.id.switch_acoustic);
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
                globalPosition = position;
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
                dialog.dismiss();
            }

        });

        // Adds adapter to song list
        songList.setAdapter(adapter);

        // Display dialog
        dialog.show();
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
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        if (mp != null) {
            mp.release();
            mp = MediaPlayer.create(this, resID[globalPosition]);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
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
            if (mp != null) {
                mp.start();
            }
        }
        else{
            stopPlayer();
        }
    }


    public void playAudio() {

        if (mpRecord != null){
            mpRecord.stop();
            mpRecord.release();
            mpRecord = null;
        }

        mpRecord = new MediaPlayer();

        try {
            mpRecord.setDataSource(fileName);
            mpRecord.setVolume(1.0f,1.0f);
            mp.setVolume(0.3f, 0.3f);
            mpRecord.prepare();
        } catch (IOException e) {}

        if (bassboost.isChecked()) {
            BassBoost bassBoost = new BassBoost(0, resID[globalPosition]);
            short strength = 500;
            bassBoost.setStrength(strength);
            bassBoost.setEnabled(true);
        }

        if (equalizer.isChecked()) {
            Equalizer equalizer = new Equalizer(0, resID[globalPosition]);
            equalizer.setEnabled(true);
        }

        if (loudness.isChecked()) {
            LoudnessEnhancer loudnessEnhancer = new LoudnessEnhancer(resID[globalPosition]);
            mp.setVolume(0.7f, 0.7f);
            loudnessEnhancer.setTargetGain(30);
            loudnessEnhancer.setEnabled(true);
        }

        if (acoustic.isChecked()) {
            // No implementation
        }

        mp.start();
        mpRecord.start();
    }


}
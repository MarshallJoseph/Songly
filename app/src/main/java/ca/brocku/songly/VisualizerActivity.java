package ca.brocku.songly;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.brocku.songly.vis.type.Bar;

public class VisualizerActivity extends AppCompatActivity {

    public static final int AUDIO_PERMISSION_REQUEST_CODE = 102;

    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
            Manifest.permission.RECORD_AUDIO
    };

    MediaPlayer mp;
    final String[] listContent = {"Colorado", "You Got It"};
    final int[] resID = {R.raw.colorado, R.raw.you_got_it};
    Button selectSongButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content to activity_visualizer.xml
        setContentView(R.layout.activity_visualizer);

        // Hides the action bar
        getSupportActionBar().hide();

        // Hides the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        initialize();
        final Bar bar = findViewById(R.id.visualizer);
        bar.visualizerSetup(mp.getAudioSessionId());

        selectSongButton = findViewById(R.id.select_song_button_visualizer);
        selectSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSong(bar);
            }
        });

    }

    public void initialize() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(WRITE_EXTERNAL_STORAGE_PERMS, AUDIO_PERMISSION_REQUEST_CODE);
        } else {
            mp = MediaPlayer.create(this, R.raw.colorado);
        }
    }

    public void selectSong (final Bar bar) {

        // Initialize dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.song_list);
        dialog.setTitle("Select A Song");

        // Initialize adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContent);

        // Initializes ListView for song list
        ListView songList = (ListView) dialog.findViewById(R.id.song_list_visualizer);

        // Set listeners for the song list
        songList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mp != null) {
                    stopPlayer();
                }
                mp = MediaPlayer.create(getApplicationContext(), resID[position]);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopPlayer();
                    }
                });
                bar.release();
                bar.visualizerSetup(mp.getAudioSessionId());
                mp.start();
                dialog.dismiss();
            }
        });

        // Adds adapter to song list
        songList.setAdapter(adapter);

        // Display dialog
        dialog.show();

    }


    public void play (View v) {
        if (mp != null) {
            mp.start();
        }
    }

    public void pause (View v) {
        if (mp != null) {
            mp.pause();
        }
    }

    public void stop (View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (mp != null) {
            mp.release();
            mp = null;
            Toast.makeText(this, "MediaPlayer Released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}

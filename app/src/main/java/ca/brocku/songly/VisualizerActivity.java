package ca.brocku.songly;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VisualizerActivity extends AppCompatActivity {

    ListView songList;
    MediaPlayer mp;
    final String[] listContent = {"Colorado", "You Got It"};
    final int[] resID = {R.raw.colorado, R.raw.you_got_it};

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

        // Initializes ListView for song list
        songList = (ListView) findViewById(R.id.song_list_visualizer);

        // Initialize adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContent);

        // Adds adapter to song list
        songList.setAdapter(adapter);

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
                mp.start();
            }
        });

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

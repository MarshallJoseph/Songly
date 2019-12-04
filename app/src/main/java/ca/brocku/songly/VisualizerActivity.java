package ca.brocku.songly;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.brocku.songly.vis.BarVisualizer;

public class VisualizerActivity extends AppCompatActivity {

    BarVisualizer bar;

    MediaPlayer mp;
    final String[] songNames = {"'Africa' - by Toto", "'All Star' - by Smash Mouth", "'Baby I'm Yours' - by Breakbot", "'Jukebox Hero' - by Foreigner", "'Never Gonna Give You Up' - by Rick Astley"};
    final int[] resID = {R.raw.africa, R.raw.allstar, R.raw.babyimyours, R.raw.jukebox, R.raw.never};
    Button selectSongButton;
    Button colourBlack, colourWhite, colourRed, colourYellow, colourGreen, colourBlue, colourPurple, colourPink;
    Button moreBars, lessBars, moreGap, lessGap;
    Button visBar, visCircle, visLine;


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

        initColourButtons();
        initSizeButtons();
        initVisButtons();

        initialize();
        bar = findViewById(R.id.visualizer);
        bar.visualizerSetup(mp.getAudioSessionId());

        selectSongButton = findViewById(R.id.select_song_button_visualizer);
        selectSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSong(bar);
            }
        });

    }

    private void initVisButtons() {
        visBar = (Button) findViewById(R.id.button_vis_bar);
        visBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisualizerType(true,false,false);
            }
        });
        visCircle = (Button) findViewById(R.id.button_vis_circle);
        visCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisualizerType(false,true,false);
            }
        });
        visLine = (Button) findViewById(R.id.button_vis_line);
        visLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisualizerType(false,false,true);
            }
        });
    }

    public void initColourButtons() {
        colourBlack = (Button) findViewById(R.id.button_colour_black);
        colourBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#000000"));
            }
        });
        colourWhite = (Button) findViewById(R.id.button_colour_white);
        colourWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#FFFFFF"));
            }
        });
        colourRed = (Button) findViewById(R.id.button_colour_red);
        colourRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#FF0000"));
            }
        });
        colourYellow = (Button) findViewById(R.id.button_colour_yellow);
        colourYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#E5FF32"));
            }
        });
        colourGreen = (Button) findViewById(R.id.button_colour_green);
        colourGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#00FF00"));
            }
        });
        colourBlue = (Button) findViewById(R.id.button_colour_blue);
        colourBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#0011FF"));
            }
        });
        colourPurple = (Button) findViewById(R.id.button_colour_purple);
        colourPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#CC00FF"));
            }
        });
        colourPink = (Button) findViewById(R.id.button_colour_pink);
        colourPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setPaintColour(new Color().parseColor("#FF00A1"));
            }
        });
    }

    public void initSizeButtons() {
        moreBars = (Button) findViewById(R.id.button_more_bars);
        moreBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.moreBars();
            }
        });
        lessBars = (Button) findViewById(R.id.button_less_bars);
        lessBars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.lessBars();
            }
        });
        moreGap = (Button) findViewById(R.id.button_more_gap);
        moreGap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.moreGap();
            }
        });
        lessGap = (Button) findViewById(R.id.button_less_gap);
        lessGap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.lessGap();
            }
        });
    }

    public void initialize() {
        mp = MediaPlayer.create(this, R.raw.africa);
    }

    public void selectSong (final BarVisualizer bar) {

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
            // Toast.makeText(this, "MediaPlayer Released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}

package ca.brocku.songly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button visualizer, songly, studio;
    // make change

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content to activity_main.xml
        setContentView(R.layout.activity_main);

        // Hides the action bar
        getSupportActionBar().hide();

        // Hides the status bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Initialize the three menu buttons
        visualizer = (Button) findViewById(R.id.visualizer_button_main);
        songly = (Button) findViewById(R.id.songly_button_main);
        studio = (Button) findViewById(R.id.studio_button_main);

        // Set on click listeners for the three menu buttons
        visualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), VisualizerActivity.class);
                startActivity(intent);
            }
        });
        songly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SonglyActivity.class);
                startActivity(intent);
            }
        });
        studio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StudioActivity.class);
                startActivity(intent);
            }
        });

    }

}

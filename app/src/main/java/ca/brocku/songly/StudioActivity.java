package ca.brocku.songly;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content to activity_songly.xml
        setContentView(R.layout.activity_studio);
    }

}

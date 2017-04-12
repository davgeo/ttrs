package davgeo.github.tickettoridescoreboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when user clicks Start Game button */
    public void startGame(View view) {
        Intent intent = new Intent(this, PlayerActionActivity.class);

        // Get data from screen
        EditText noPlayersEditTxt = (EditText) findViewById(R.id.noPlayersTxtEdit);
        EditText noStationsEditTxt = (EditText) findViewById(R.id.startingStationsTxtEdit);
        EditText noTrainsEditTxt = (EditText) findViewById(R.id.startingTrainsTxtEdit);

        // Set global configuration
        int noPlayersInt = Integer.parseInt(noPlayersEditTxt.getText().toString());
        int noStartingStationsInt = Integer.parseInt(noStationsEditTxt.getText().toString());
        int noStartingTrainsInt = Integer.parseInt(noTrainsEditTxt.getText().toString());
        intent.putExtra("noPlayers", noPlayersInt);
        intent.putExtra("nextPlayer", 1);

        // Create player-specific settings
        int [][] scoreboardArray = new int [noPlayersInt][3];

        // Configure array
        for(int i=0; i < noPlayersInt; i++) {
            scoreboardArray[i][0] = 0; // Player Score
            scoreboardArray[i][1] = noStartingTrainsInt; // Remaining Trains
            scoreboardArray[i][2] = noStartingStationsInt; // Remaining Stations
        }

        // Add array to intent as bundle
        Bundle scoreboardBundle = new Bundle();
        scoreboardBundle.putSerializable("scoreboardArray", scoreboardArray);
        intent.putExtras(scoreboardBundle);

        // Start next activity
        startActivity(intent);
    }
}

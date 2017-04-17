package davgeo.github.tickettoridescoreboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when user clicks Start Game button */
    public void startGame(View view) {
        Intent intent = new Intent(this, PlayerActionActivity.class);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Get data from screen
        EditText noPlayersEditTxt = (EditText) findViewById(R.id.noPlayersTxtEdit);
        EditText noTrainsEditTxt = (EditText) findViewById(R.id.startingTrainsTxtEdit);
        EditText noStationsEditTxt = (EditText) findViewById(R.id.startingStationsTxtEdit);

        String noPlayersString = noPlayersEditTxt.getText().toString();
        String noTrainsString = noTrainsEditTxt.getText().toString();
        String noStationsString = noStationsEditTxt.getText().toString();

        // Check string entry is not empty
        if(TextUtils.isEmpty(noPlayersString))
        {
            Toast.makeText(this, "Enter number of players", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(noTrainsString))
        {
            Toast.makeText(this, "Enter number of trains", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(noStationsString))
        {
            Toast.makeText(this, "Enter number of stations", Toast.LENGTH_SHORT).show();
            return;
        }


        int noPlayersInt = Integer.parseInt(noPlayersString);
        int noStartingStationsInt = Integer.parseInt(noStationsString);
        int noStartingTrainsInt = Integer.parseInt(noTrainsString);

        // Sanity check integer values
        if(noPlayersInt < 1) {
            Toast.makeText(this, "Number of players must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if(noStartingTrainsInt < 1) {
            Toast.makeText(this, "Number of trains must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if(noStartingStationsInt < 0) {
            Toast.makeText(this, "Number of stations cannot be negative", Toast.LENGTH_SHORT).show();
        }

        intent.putExtra("noPlayers", noPlayersInt);
        intent.putExtra("nextPlayer", 1);

        // Create player-specific settings
        int [][] scoreboardArray = new int [noPlayersInt][3];
        String [] playerNameArray = new String[noPlayersInt];

        // Configure array
        for(int i=0; i < noPlayersInt; i++) {
            scoreboardArray[i][0] = 0; // Player Score
            scoreboardArray[i][1] = noStartingTrainsInt; // Remaining Trains
            scoreboardArray[i][2] = noStartingStationsInt; // Remaining Stations
            playerNameArray[i]    = String.format(Locale.getDefault(), "PLAYER %d", i+1);
        }

        // Add array to intent as bundle
        Bundle scoreboardBundle = new Bundle();
        scoreboardBundle.putSerializable("scoreboardArray", scoreboardArray);
        scoreboardBundle.putStringArray("playerNameArray", playerNameArray);
        intent.putExtras(scoreboardBundle);

        // Start next activity
        startActivity(intent);
    }
}

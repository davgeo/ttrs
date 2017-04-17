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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    /** Called when user clicks Start Game button */
    public void startGame(View view) {
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

        int noPlayers = Integer.parseInt(noPlayersString);
        int noStartingTrains = Integer.parseInt(noTrainsString);
        int noStartingStations = Integer.parseInt(noStationsString);

        // Sanity check integer values
        if(noPlayers < 1) {
            Toast.makeText(this, "Number of players must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if(noStartingTrains < 1) {
            Toast.makeText(this, "Number of trains must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if(noStartingStations < 0) {
            Toast.makeText(this, "Number of stations cannot be negative", Toast.LENGTH_SHORT).show();
        }

        // Create player-specific settings
        int [] trainScoreArray = new int [noPlayers];
        int [] trainCountArray = new int [noPlayers];
        int [] stationCountArray = new int [noPlayers];
        int [] cardScoreArray = new int [noPlayers];
        String [] playerNameArray = new String[noPlayers];

        // Configure array
        for(int i=0; i < noPlayers; i++) {
            trainScoreArray[i]   = 0;
            trainCountArray[i]   = noStartingTrains;
            stationCountArray[i] = noStartingStations;
            cardScoreArray[i]    = 0;
            playerNameArray[i]   = String.format(Locale.getDefault(), "PLAYER %d", i+1);
        }

        // Add configuration to bundle
        Bundle bundle = new Bundle();

        bundle.putInt("playerNum", 1);
        bundle.putInt("noPlayers", noPlayers);
        bundle.putInt("turnNo", 1);
        bundle.putIntArray("trainScoreArray", trainScoreArray);
        bundle.putIntArray("trainCountArray", trainCountArray);
        bundle.putIntArray("stationCountArray", stationCountArray);
        bundle.putIntArray("cardScoreArray", cardScoreArray);
        bundle.putStringArray("playerNameArray", playerNameArray);

        // Add bundle to intent
        Intent intent = new Intent(this, PlayerActionActivity.class);
        intent.putExtras(bundle);

        // Start next activity
        startActivity(intent);
    }
}

package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class PlayerActionActivity extends AppCompatActivity {
    int m_PlayerNum;
    int m_noPlayers;
    int m_turnNo;
    String [] m_playerNameArray;
    int [][] m_scoreboardArray;

    // TODO : Save this state if app is killed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_action);

        Intent thisIntent = getIntent();

        // Player settings
        m_PlayerNum = thisIntent.getIntExtra("nextPlayer", 1);
        m_noPlayers = thisIntent.getIntExtra("noPlayers", 1);
        m_turnNo = thisIntent.getIntExtra("turnNo", 0);

        Bundle scoreboardBundle = thisIntent.getExtras();
        m_scoreboardArray = (int[][]) scoreboardBundle.getSerializable("scoreboardArray");
        m_playerNameArray = (String[]) scoreboardBundle.getSerializable("playerNameArray");

        // TODO : Sanity check settings (legal values)

        displayPlayerStats();
    }

    /** Call to display player stats */
    protected void displayPlayerStats() {
        // Player score - scoreboardArray[playerNum-1][0]
        // Remaining trains - scoreboardArray[playerNum-1][1]
        // Remaining stations - scoreboardArray[playerNum-1][2]
        int idx = m_PlayerNum - 1;

        TextView nameTxt = (TextView) findViewById(R.id.playerName);
        nameTxt.setText(m_playerNameArray[idx]);

        TextView scoreTxt = (TextView) findViewById(R.id.scoreValueTxt);
        scoreTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][0]));

        TextView trainTxt = (TextView) findViewById(R.id.trainsValueTxt);
        trainTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][1]));

        TextView stationTxt = (TextView) findViewById(R.id.stationsValueTxt);
        stationTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][2]));

        Resources res = getResources();
        String actionString = res.getString(R.string.selectActionTxt, m_PlayerNum);

        TextView actionTxt = (TextView) findViewById(R.id.selectActionTxt);
        actionTxt.setText(actionString);

        // Increment turn number if on last player
        if(m_PlayerNum == m_noPlayers) {
            m_turnNo++;
        }
    }

    /** Load next player **/
    protected void goToNextPlayer() {
        m_PlayerNum++;

        if(m_PlayerNum > m_noPlayers) {
            m_PlayerNum = 1;
        }

        displayPlayerStats();
    }

    /** Called when user clicks a pickup route/train cards button */
    public void pickupCards(View view) {
        goToNextPlayer();
    }

    /** Called when user clicks played trains button **/
    public void playedTrains(View view) {
        m_scoreboardArray[m_PlayerNum-1][0] += 1;
        m_scoreboardArray[m_PlayerNum-1][1] -= 1;
        goToNextPlayer();
    }

    /** Called when user clicks played stations button **/
    public void playedStations(View view) {
        m_scoreboardArray[m_PlayerNum-1][2] -= 1;
        goToNextPlayer();
    }

    /** Called when the End Game button is pressed **/
    public void endGame(View view) {
        goToNextPlayer();
    }

    /** Edit player name **/
    public void editName(View view) {
        final EditText alertEditTxt = new EditText(this);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Edit player name");
        alertBuilder.setCancelable(true);
        alertBuilder.setView(alertEditTxt);

        alertBuilder.setPositiveButton(
                "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        m_playerNameArray[m_PlayerNum-1] = alertEditTxt.getText().toString();
                        TextView nameTxt = (TextView) findViewById(R.id.playerName);
                        nameTxt.setText(m_playerNameArray[m_PlayerNum-1]);
                        dialog.cancel();
                    }
                });

        alertBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = alertBuilder.create();
        alert11.show();
    }
}

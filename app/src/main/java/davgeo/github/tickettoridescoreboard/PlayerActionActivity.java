package davgeo.github.tickettoridescoreboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class PlayerActionActivity extends AppCompatActivity {
    int m_PlayerNum;
    int m_noPlayers;
    int m_turnNo;
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
        nameTxt.setText(String.format(Locale.getDefault(), "PLAYER %d", m_PlayerNum));

        TextView scoreTxt = (TextView) findViewById(R.id.scoreValueTxt);
        scoreTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][0]));

        TextView trainTxt = (TextView) findViewById(R.id.trainsValueTxt);
        trainTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][1]));

        TextView stationTxt = (TextView) findViewById(R.id.stationsValueTxt);
        stationTxt.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][2]));

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
}

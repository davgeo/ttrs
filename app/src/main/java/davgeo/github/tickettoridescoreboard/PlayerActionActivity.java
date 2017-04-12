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
        // Turn no. - scoreboardArray[playerNum-1][3]

        // Do display
        TextView testTxt = (TextView) findViewById(R.id.testTxt);
        testTxt.setText(String.format(Locale.getDefault(), "%d/%d - %d - %d",
                m_PlayerNum, m_noPlayers, m_scoreboardArray[m_PlayerNum-1][0], m_turnNo));

        // Increment turn number and keep intent synchronised
        if(m_PlayerNum == m_noPlayers) {
            m_turnNo++;
        }
    }

    /** Called when user clicks a pickup route/train cards button */
    public void pickupCards(View view) {
        m_PlayerNum++;

        if(m_PlayerNum > m_noPlayers) {
            m_PlayerNum = 1;
        }

        displayPlayerStats();
    }
}

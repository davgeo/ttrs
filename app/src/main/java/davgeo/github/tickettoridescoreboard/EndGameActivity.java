package davgeo.github.tickettoridescoreboard;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class EndGameActivity extends BaseGameActivity {
    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_end_game);
        super.onCreate(savedInstanceState);
    }

    /** Add extra activity specific behaviour to onCreate method **/
    @Override
    protected void doActivitySetup() {
        m_game_complete = true;

        Spinner spinner = (Spinner) findViewById(R.id.endGameSpinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, m_playerNameArray);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                m_playerNum = position + 1;
                displayPlayerStats();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                m_playerNum = 0;
            }
        });
    }

    /** Call to display player stats **/
    @Override
    protected void displayPlayerStats() {
        // Player score - scoreboardArray[playerNum-1][0]
        // Remaining trains - scoreboardArray[playerNum-1][1]
        // Remaining stations - scoreboardArray[playerNum-1][2]
        int idx = m_playerNum - 1;

        Spinner spinner = (Spinner) findViewById(R.id.endGameSpinner);
        spinner.setSelection(idx);

        TextView trainScoreTxtView = (TextView) findViewById(R.id.endGameTableTrainScoreValue);
        trainScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_trainScoreArray[idx]));

        TextView stationScoreTxtView = (TextView) findViewById(R.id.endGameTableStationScoreValue);
        stationScoreTxtView.setText(String.format(Locale.getDefault(), "%d", getStationScore(m_playerNum)));

        TextView routeScoreTxtView = (TextView) findViewById(R.id.endGameTableRouteScoreValue);
        routeScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_cardScoreArray[idx]));

        TextView totalScoreTxtView = (TextView) findViewById(R.id.endGameTableTotalScoreValue);
        totalScoreTxtView.setText(String.format(Locale.getDefault(), "%d", getTotalScore(m_playerNum)));
    }

    /** Call when either complete or incomplete route button is clicked **/
    public void doRouteScore(View view) {
        final EditText routeScoreEditText = (EditText) findViewById(R.id.endGameRouteScoreEditTxt);
        String routeScoreString = routeScoreEditText.getText().toString();

        if(TextUtils.isEmpty(routeScoreString))
        {
            Toast.makeText(this, "Enter a route card value", Toast.LENGTH_SHORT).show();
            return;
        }

        int routeScore = Integer.parseInt(routeScoreString);
        int buttonTag = Integer.parseInt(view.getTag().toString());
        int idx = m_playerNum - 1;

        if(buttonTag == 1) {
            m_cardScoreArray[idx] += routeScore;
        } else {
            m_cardScoreArray[idx] -= routeScore;
        }

        routeScoreEditText.setText("");

        TextView routeScoreTxtView = (TextView) findViewById(R.id.endGameTableRouteScoreValue);
        routeScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_cardScoreArray[idx]));

        TextView totalScoreTxtView = (TextView) findViewById(R.id.endGameTableTotalScoreValue);
        totalScoreTxtView.setText(String.format(Locale.getDefault(), "%d", getTotalScore(m_playerNum)));
    }

    /** Called when next player button clicked **/
    public void doNextPlayer(View view) {
        goToNextPlayer();
    }
}

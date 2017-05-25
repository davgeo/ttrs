package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        Spinner spinner = (Spinner) findViewById(R.id.endGameSpinner);
        spinner.setSelection(m_playerNum - 1);
        updateScoreTable();
    }

    /** Call to implement setting change updates **/
    @Override
    protected void doSettingUpdate() {
        updateScoreTable();
    }

    /** Update score table **/
    public void updateScoreTable() {
        TextView trainScoreTxtView = (TextView) findViewById(R.id.endGameTableTrainScoreValue);
        trainScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_trainScoreArray[m_playerNum - 1]));

        TextView stationScoreTxtView = (TextView) findViewById(R.id.endGameTableStationScoreValue);
        stationScoreTxtView.setText(String.format(Locale.getDefault(), "%d", getStationScore(m_playerNum)));

        TextView routeScoreTxtView = (TextView) findViewById(R.id.endGameTableRouteScoreValue);
        routeScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_cardScoreArray[m_playerNum - 1]));

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

        saveUndoState();

        int routeScore = Integer.parseInt(routeScoreString);
        int buttonTag = Integer.parseInt(view.getTag().toString());
        int idx = m_playerNum - 1;

        if(buttonTag == 1) {
            m_cardScoreArray[idx] += routeScore;
        } else {
            m_cardScoreArray[idx] -= routeScore;
        }

        routeScoreEditText.setText("");

        updateScoreTable();
    }

    /** Called when next player button clicked **/
    public void doNextPlayer(View view) {
        goToNextPlayer();
    }

    /** Called when the Show Final Score Sheet button is clicked **/
    public void showFinalScores(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getResources().getString(R.string.endGameActivityFinishedDialog));
        alertBuilder.setCancelable(true);

        alertBuilder.setPositiveButton(
                "Show Final Scores",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(EndGameActivity.this, ScoreSheetActivity.class);
                        Bundle bundle = new Bundle();
                        saveBundleState(bundle);
                        intent.putExtras(bundle);
                        dialog.cancel();
                        startActivity(intent);
                    }
                });

        alertBuilder.setNegativeButton(
                "Back",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog newGameDialog = alertBuilder.create();
        newGameDialog.show();
    }

    /** Called when the Played Trains button is pressed **/
    public void playedTrains(View view) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        // Load dialog from custom layout xml
        LayoutInflater inflater = this.getLayoutInflater();
        alertBuilder.setView(inflater.inflate(R.layout.played_trains_dialog, null))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog playedTrainsDialog = alertBuilder.create();
        playedTrainsDialog.show();

        // Configure each button
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn1), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn2), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn3), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn4), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn5), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn6), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn7), playedTrainsDialog);
        setPlayedTrainButtonTxt((Button) playedTrainsDialog.findViewById(R.id.playedTrainsBtn8), playedTrainsDialog);
    }

    /** Called when the Played Stations button is pressed **/
    public void playedStations(View view) {
        saveUndoState();
        m_stationCountArray[m_playerNum-1] -= 1;
        updateScoreTable();
    }

    /** Call to configure buttons in played trains dialog **/
    protected void setPlayedTrainButtonTxt(Button button, final AlertDialog dialog) {
        Resources res = getResources();
        int[] routeScoreArray = res.getIntArray(R.array.trainScores);
        int buttonTag = Integer.parseInt(button.getTag().toString());

        // Update button text
        button.setText(res.getString(R.string.playedTrainsDialogBtn,
                buttonTag, routeScoreArray[buttonTag-1]));

        // Update onClick method to call scoreTrains function and then dismiss dialog
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scoreTrains(view);
                dialog.dismiss();
            }
        });
    }

    /** Called when a button is clicked from played trains dialog **/
    public void scoreTrains(View view) {
        int trainCount = Integer.parseInt(view.getTag().toString());
        int[] routeScoreArray = getResources().getIntArray(R.array.trainScores);

        saveUndoState();

        m_trainScoreArray[m_playerNum-1] += routeScoreArray[trainCount-1];
        m_trainCountArray[m_playerNum-1] -= trainCount;

        updateScoreTable();
    }

}

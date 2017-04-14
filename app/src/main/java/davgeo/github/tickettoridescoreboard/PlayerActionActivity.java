package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

        String actionString = getResources().getString(R.string.selectActionTxt, m_PlayerNum);

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

    /** Called when user clicks a pickup route/train cards button */
    public void pickupCards(View view) {
        goToNextPlayer();
    }

    /** Called when user clicks played trains button **/
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

    /** Called when user clicks played stations button **/
    public void playedStations(View view) {
        m_scoreboardArray[m_PlayerNum-1][2] -= 1;
        goToNextPlayer();
    }

    /** Called when the End Game button is pressed **/
    public void endGame(View view) {
        goToNextPlayer();

        // TODO : Create end game activity
    }

    // TODO : Add settings button and activity

    /** Edit player name **/
    public void editName(View view) {
        final EditText alertEditTxt = new EditText(this);
        alertEditTxt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage(getResources().getString(R.string.playerNameDialog));
        alertBuilder.setCancelable(true);

        FrameLayout alertEditTxtContainer = new FrameLayout(this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

        alertEditTxt.setLayoutParams(params);

        alertEditTxtContainer.addView(alertEditTxt);
        alertBuilder.setView(alertEditTxtContainer);

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

        AlertDialog editPlayerNameDialog = alertBuilder.create();
        editPlayerNameDialog.show();
    }

    /** Called when a button is clicked from played trains dialog **/
    public void scoreTrains(View view) {
        int trainCount = Integer.parseInt(view.getTag().toString());
        int[] routeScoreArray = getResources().getIntArray(R.array.trainScores);

        m_scoreboardArray[m_PlayerNum-1][0] += routeScoreArray[trainCount-1];
        m_scoreboardArray[m_PlayerNum-1][1] -= trainCount;

        goToNextPlayer();
    }
}

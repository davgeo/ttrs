package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class PlayerActionActivity extends BaseGameActivity {
    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player_action);
        super.onCreate(savedInstanceState);
    }

    /** Add extra activity specific behaviour to onCreate method **/
    @Override
    protected void doActivitySetup() {
        m_game_complete = false;
        doSettingUpdate();
    }

    /** Call to display player stats */
    @Override
    protected void displayPlayerStats() {
        int idx = m_playerNum - 1;

        TextView nameTxt = (TextView) findViewById(R.id.playerName);
        nameTxt.setText(m_playerNameArray[idx]);

        TextView scoreTxt = (TextView) findViewById(R.id.scoreValueTxt);
        scoreTxt.setText(String.format(Locale.getDefault(), "%d", m_trainScoreArray[idx]));

        TextView trainTxt = (TextView) findViewById(R.id.trainsValueTxt);
        trainTxt.setText(String.format(Locale.getDefault(), "%d", m_trainCountArray[idx]));

        TextView stationTxt = (TextView) findViewById(R.id.stationsValueTxt);
        stationTxt.setText(String.format(Locale.getDefault(), "%d", m_stationCountArray[idx]));

        String actionString = getResources().getString(R.string.selectActionTxt, m_playerNum);

        TextView actionTxt = (TextView) findViewById(R.id.selectActionTxt);
        actionTxt.setText(actionString);
    }

    /** Call to implement setting change updates **/
    @Override
    protected void doSettingUpdate() {
        Button pickedTrainCardBtn = (Button) findViewById(R.id.pickupTrainsBtn);
        Button pickedRouteCardBtn = (Button) findViewById(R.id.pickupRoutesBtn);

        if(m_preferences.getBoolean("Settings.CardPickup", true)) {
            pickedTrainCardBtn.setText(R.string.pickupTrainsBtn);
            pickedRouteCardBtn.setVisibility(View.VISIBLE);
        } else {
            pickedTrainCardBtn.setText(R.string.pickupBtnAlternate);
            pickedRouteCardBtn.setVisibility(View.GONE);
        }
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

    /** Called when either the Pickup Route/Train Cards buttons are pressed */
    public void pickupCards(View view) {
        saveUndoState();
        goToNextPlayer();
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

        if(m_preferences.getBoolean("Settings.AutoNextPlayer", true)) {
            goToNextPlayer();
        } else {
            displayPlayerStats();
        }
    }

    /** Called when the End Game button is pressed **/
    public void endGame(View view) {
        Intent intent = new Intent(this, EndGameActivity.class);
        Bundle bundle = new Bundle();
        saveBundleState(bundle);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /** Called when the Edit Player Name button is pressed **/
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
                        String newName = alertEditTxt.getText().toString();

                        if(newName.equals("")) {
                            Toast.makeText(PlayerActionActivity.this, "New player name cannot be blank", Toast.LENGTH_SHORT).show();
                        } else {
                            m_playerNameArray[m_playerNum - 1] = newName;
                            TextView nameTxt = (TextView) findViewById(R.id.playerName);
                            nameTxt.setText(m_playerNameArray[m_playerNum - 1]);
                            dialog.cancel();
                        }
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

        saveUndoState();

        // Update scores and train count
        m_trainScoreArray[m_playerNum-1] += routeScoreArray[trainCount-1];
        m_trainCountArray[m_playerNum-1] -= trainCount;

        // Check if train count goes below threshold value
        int trainThreshold = m_preferences.getInt("Settings.EndGameTrainThreshold",
                getResources().getInteger(R.integer.endGameTrainThreshold));

        if(m_trainCountArray[m_playerNum-1] <= trainThreshold) {
            Toast.makeText(this, String.format(Locale.getDefault(),
                    "End game approaching - %s has %d or fewer trains remaining",
                    m_playerNameArray[m_playerNum-1], trainThreshold), Toast.LENGTH_SHORT).show();
            Button endGameBtn = (Button) findViewById(R.id.endGameBtn);
            endGameBtn.setTypeface(null, Typeface.BOLD);
        }

        // Update display
        if(m_preferences.getBoolean("Settings.AutoNextPlayer", true)) {
            goToNextPlayer();
        } else {
            displayPlayerStats();
        }
    }

    // TODO : Add player colours
}

package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.LinkedList;
import java.util.Locale;

public abstract class BaseGameActivity extends AppCompatActivity {
    int m_playerNum;
    int m_noPlayers;
    int [] m_trainScoreArray;
    int [] m_trainCountArray;
    int [] m_stationCountArray;
    int [] m_cardScoreArray;
    int [] m_playerColourIndexArray;
    String [] m_playerNameArray;
    boolean m_game_complete;

    LinkedList<Bundle> m_undoBundleQ;
    LinkedList<Bundle> m_redoBundleQ;

    SharedPreferences m_preferences;

    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load shared preferences
        m_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Add toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Load state from intent or previous saved instance
        if(savedInstanceState != null) {
            loadBundleState((savedInstanceState));
        } else {
            Intent thisIntent = getIntent();
            Bundle intentBundle = thisIntent.getExtras();
            loadBundleState(intentBundle);
        }

        // Do activity setup (expanded for subclasses)
        doActivitySetup();

        // Update activity display
        displayPlayerStats();
    }

    /** Save state when activity is destroyed **/
    @Override
    public void onSaveInstanceState(Bundle saveState) {
        saveBundleState(saveState);
        super.onSaveInstanceState(saveState);
    }

    /** Add menu options to toolbar **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_action_menu, menu);
        return true;
    }

    /** Configure options menu **/
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem undoMenuItem = menu.findItem(R.id.actionBarUndo);
        undoMenuItem.setEnabled(false);
        if (m_undoBundleQ != null) {
            if(m_undoBundleQ.size() > 0) {
                undoMenuItem.setEnabled(true);
            }
        }

        MenuItem redoMenuItem = menu.findItem(R.id.actionBarRedo);
        redoMenuItem.setEnabled(false);
        if (m_redoBundleQ != null) {
            if(m_redoBundleQ.size() > 0) {
                redoMenuItem.setEnabled(true);
            }
        }
        return true;
    }

    /** Catch menu selection **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionBarUndo:
                doUndo();
                return true;

            case R.id.actionBarRedo:
                doRedo();
                return true;

            case R.id.actionBarEditPlayer:
                doEditNameDialog();
                return true;

            case R.id.actionBarSummary:
                doSummaryDialog();
                return true;

            case R.id.actionBarSettings:
                doSettingsDialog();
                return true;

            case R.id.actionBarNewGame:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage(getResources().getString(R.string.actionBarNewGameDialog));
                alertBuilder.setCancelable(true);

                alertBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(BaseGameActivity.this, MainActivity.class);
                                startActivity(intent);
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

                AlertDialog newGameDialog = alertBuilder.create();
                newGameDialog.show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    /** Call to return all state in a bundle **/
    protected void saveBundleState(Bundle bundle) {
        bundle.putInt("playerNum", m_playerNum);
        bundle.putInt("noPlayers", m_noPlayers);
        bundle.putIntArray("trainScoreArray", m_trainScoreArray.clone());
        bundle.putIntArray("trainCountArray", m_trainCountArray.clone());
        bundle.putIntArray("stationCountArray", m_stationCountArray.clone());
        bundle.putIntArray("cardScoreArray", m_cardScoreArray.clone());
        bundle.putIntArray("playerColourIndexArray", m_playerColourIndexArray.clone());
        bundle.putStringArray("playerNameArray", m_playerNameArray.clone());
    }

    /** Call to return all state in a bundle **/
    protected void loadBundleState(Bundle bundle) {
        m_playerNum = bundle.getInt("playerNum");
        m_noPlayers = bundle.getInt("noPlayers");
        m_playerNameArray = bundle.getStringArray("playerNameArray");
        m_trainScoreArray = bundle.getIntArray("trainScoreArray");
        m_trainCountArray = bundle.getIntArray("trainCountArray");
        m_cardScoreArray = bundle.getIntArray("cardScoreArray");
        m_playerColourIndexArray = bundle.getIntArray("playerColourIndexArray");
        m_stationCountArray = bundle.getIntArray("stationCountArray");
    }

    /** Load next player **/
    protected void goToNextPlayer() {
        m_playerNum++;

        if(m_playerNum > m_noPlayers) {
            m_playerNum = 1;
        }

        displayPlayerStats();
    }

    /** Calculate station score from station count **/
    protected int getStationScore(int playerNo) {
        int perStationScore = m_preferences.getInt("Settings.StationValue", R.integer.perStationScore);
        return m_stationCountArray[playerNo-1]*perStationScore;
    }

    /** Get total score for player **/
    protected int getTotalScore(int playerNo) {
        return m_trainScoreArray[playerNo-1] + getStationScore(playerNo) + m_cardScoreArray[playerNo-1];
    }

    /** Add score to table **/
    protected void addValueToTable(int value, TableRow row) {
        TextView txtView = new TextView(this);
        txtView.setText(String.format(Locale.getDefault(), "%d", value));
        txtView.setGravity(Gravity.END);
        row.addView(txtView);
    }

    /** Inflate setting dialog **/
    protected void doSettingsDialog() {
        // Load dialog from custom layout xml
        LayoutInflater inflater = this.getLayoutInflater();

        final View inflatedView = inflater.inflate(R.layout.settings, null);

        boolean cardPickupToggleEnabledPref = m_preferences.getBoolean("Settings.CardPickup", true);
        boolean autoNextPlayerToggleEnabledPref = m_preferences.getBoolean("Settings.AutoNextPlayer", true);
        boolean endGamePlayedToggleEnabledPref = m_preferences.getBoolean("Settings.EndGamePlayedButtons", true);
        int stationValuePref = m_preferences.getInt("Settings.StationValue", getResources().getInteger(R.integer.perStationScore));
        int trainThresholdPref = m_preferences.getInt("Settings.EndGameTrainThreshold", getResources().getInteger(R.integer.endGameTrainThreshold));

        final ToggleButton cardPickupToggle = (ToggleButton) inflatedView.findViewById(R.id.settingsCardPickupToggle);
        final ToggleButton autoNextPlayerToggle = (ToggleButton) inflatedView.findViewById(R.id.settingsChangePlayerToggle);
        final ToggleButton endGamePlayedToggle = (ToggleButton) inflatedView.findViewById(R.id.settingsEndGamePlayedToggle);
        final EditText stationValueTxt = (EditText) inflatedView.findViewById(R.id.settingsStationScoreEditTxt);
        final EditText trainThresholdTxt = (EditText) inflatedView.findViewById(R.id.settingsTrainThresholdEditTxt);

        cardPickupToggle.setChecked(cardPickupToggleEnabledPref);
        autoNextPlayerToggle.setChecked(autoNextPlayerToggleEnabledPref);
        endGamePlayedToggle.setChecked(endGamePlayedToggleEnabledPref);
        stationValueTxt.setText(String.format(Locale.getDefault(), "%d", stationValuePref));
        trainThresholdTxt.setText(String.format(Locale.getDefault(), "%d", trainThresholdPref));

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(inflatedView);

        alertBuilder.setPositiveButton(
                "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean cardPickupToggleEnabled = cardPickupToggle.isChecked();
                        boolean autoNextPlayerToggleEnabled = autoNextPlayerToggle.isChecked();
                        boolean endGamePlayedToggleEnabled = endGamePlayedToggle.isChecked();

                        int stationValue;
                        String stationValueString = stationValueTxt.getText().toString();

                        if(TextUtils.isEmpty(stationValueString)) {
                            stationValue = getResources().getInteger(R.integer.perStationScore);
                        } else {
                            stationValue = Integer.parseInt(stationValueString);
                        }

                        int trainThresholdValue;
                        String trainThresholdString = trainThresholdTxt.getText().toString();

                        if(TextUtils.isEmpty(trainThresholdString)) {
                            trainThresholdValue = getResources().getInteger(R.integer.endGameTrainThreshold);
                        } else {
                            trainThresholdValue = Integer.parseInt(trainThresholdString);
                        }

                        SharedPreferences.Editor editor = m_preferences.edit();
                        editor.putBoolean("Settings.CardPickup", cardPickupToggleEnabled);
                        editor.putBoolean("Settings.AutoNextPlayer", autoNextPlayerToggleEnabled);
                        editor.putBoolean("Settings.EndGamePlayedButtons", endGamePlayedToggleEnabled);
                        editor.putInt("Settings.StationValue", stationValue);
                        editor.putInt("Settings.EndGameTrainThreshold", trainThresholdValue);
                        editor.apply();
                        doSettingUpdate();
                        dialog.cancel();
                    }
                });


        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    /** Inflate summary and add new table rows for each player **/
    protected void doSummaryDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        // Load dialog from custom layout xml
        LayoutInflater inflater = this.getLayoutInflater();

        View inflatedView = inflater.inflate(R.layout.summary, null);
        TableLayout dialogTableLayout = (TableLayout) inflatedView.findViewById(R.id.summaryDialogTable);

        for(int i=0; i < m_noPlayers; i++) {
            TableRow dialogTableRow = new TableRow(this);

            TextView nameTxtView = new TextView(this);
            nameTxtView.setText(m_playerNameArray[i]);
            nameTxtView.setLayoutParams(new TableRow.LayoutParams(1));
            dialogTableRow.addView(nameTxtView);

            if(m_game_complete) {
                addValueToTable(getTotalScore(i+1), dialogTableRow);
            } else {
                addValueToTable(m_trainScoreArray[i], dialogTableRow);
            }
            addValueToTable(m_trainCountArray[i], dialogTableRow);
            addValueToTable(m_stationCountArray[i], dialogTableRow);

            dialogTableLayout.addView(dialogTableRow);
        }

        alertBuilder.setView(inflatedView);
        alertBuilder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        final AlertDialog summaryDialog = alertBuilder.create();
        summaryDialog.show();
    }

    /** Called when the Edit Player Name button is pressed **/
    protected void doEditNameDialog() {
        // Load dialog from custom layout xml
        LayoutInflater inflater = this.getLayoutInflater();
        final View inflatedView = inflater.inflate(R.layout.edit_player_info, null);

        // Configure player select spinner
        final Spinner playerColourSpinner = (Spinner) inflatedView.findViewById(R.id.editPlayerDialogChangeColourSpinner);
        final String [] spinnerColourOptionArray = getResources().getStringArray(R.array.editPlayerSpinnerColours);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, spinnerColourOptionArray);
        playerColourSpinner.setAdapter(spinnerArrayAdapter);
        playerColourSpinner.setSelection(m_playerColourIndexArray[m_playerNum-1]);

        // Get handle to player name edit text
        final EditText changePlayerNameEditText = (EditText) inflatedView.findViewById(R.id.editPlayerDialogChangeName);

        // Setup alert dialog
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(inflatedView);

        alertBuilder.setPositiveButton(
                "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String newName = changePlayerNameEditText.getText().toString();

                        if(newName.equals("")) {
                            Toast.makeText(BaseGameActivity.this, "New player name cannot be blank", Toast.LENGTH_SHORT).show();
                        } else {
                            m_playerNameArray[m_playerNum - 1] = newName;
                        }

                        m_playerColourIndexArray[m_playerNum-1] = playerColourSpinner.getSelectedItemPosition();
                        displayPlayerStats();
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

    /** Convert colour index array values to actual colour values **/
    protected int getPlayerColour(int playerNum) {
        switch (m_playerColourIndexArray[playerNum-1]) {
            case 0: // None
                return Color.GRAY;
            case 1: // Blue
                return Color.BLUE;
            case 2: // Red
                return Color.RED;
            case 3: // Green
                return Color.GREEN;
            case 4: // Yellow
                return Color.YELLOW;
            case 5: // Black
                return Color.BLACK;
            default:
                return Color.GRAY;

        }
    }

    /** Save state for undo action **/
    protected void saveUndoState() {
        // Create undo queue if it doesn't exist
        if(m_undoBundleQ == null)
            m_undoBundleQ = new LinkedList<>();

        // Only keep 10 undo entries
        if(m_undoBundleQ.size() > 10)
            m_undoBundleQ.remove();

        // Clear redo queue if it exists
        if(m_redoBundleQ != null)
            m_redoBundleQ.clear();

        // Save state to undo queue
        Bundle undoBundle = new Bundle();
        saveBundleState(undoBundle);
        m_undoBundleQ.add(undoBundle);
    }

    /** Save state for redo action **/
    protected void saveRedoState() {
        // Create redo queue if it doesn't exist
        if(m_redoBundleQ == null)
            m_redoBundleQ = new LinkedList<>();

        // Save state to redo queue
        Bundle redoBundle = new Bundle();
        saveBundleState(redoBundle);
        m_redoBundleQ.add(redoBundle);
    }

    /** Do undo action **/
    protected void doUndo() {
        if(m_undoBundleQ != null) {
            Bundle undoBundle = m_undoBundleQ.pollLast();

            if (undoBundle != null) {
                saveRedoState();
                loadBundleState(undoBundle);
                displayPlayerStats();
            }
        }
    }

    /** Do redo action **/
    protected void doRedo() {
        if(m_redoBundleQ != null) {
            Bundle redoBundle = m_redoBundleQ.pollLast();

            if (redoBundle != null) {
                // Save state to undo queue
                Bundle undoBundle = new Bundle();
                saveBundleState(undoBundle);
                m_undoBundleQ.add(undoBundle);

                // Load redo state
                loadBundleState(redoBundle);
                displayPlayerStats();
            }
        }
    }

    /** Call to display player stats **/
    abstract protected void displayPlayerStats();

    /** Call to implement setting change updates **/
    abstract protected void doSettingUpdate();

    /** Call to add extra activity specific behaviour to onCreate method **/
    abstract protected void doActivitySetup();
}

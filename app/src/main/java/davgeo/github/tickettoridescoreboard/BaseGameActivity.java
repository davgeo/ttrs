package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

public abstract class BaseGameActivity extends AppCompatActivity {
    int m_playerNum;
    int m_noPlayers;
    int [] m_trainScoreArray;
    int [] m_trainCountArray;
    int [] m_stationCountArray;
    int [] m_cardScoreArray;
    String [] m_playerNameArray;
    boolean m_game_complete;

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

    /** Catch menu selection **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionBarSettings:
                doSettingsDialog();
                return true;

            case R.id.actionBarSummary:
                doSummaryDialog();
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
        bundle.putIntArray("trainScoreArray", m_trainScoreArray);
        bundle.putIntArray("trainCountArray", m_trainCountArray);
        bundle.putIntArray("stationCountArray", m_stationCountArray);
        bundle.putIntArray("cardScoreArray", m_cardScoreArray);
        bundle.putStringArray("playerNameArray", m_playerNameArray);
    }

    /** Call to return all state in a bundle **/
    protected void loadBundleState(Bundle bundle) {
        m_playerNum = bundle.getInt("playerNum");
        m_noPlayers = bundle.getInt("noPlayers");
        m_playerNameArray = bundle.getStringArray("playerNameArray");
        m_trainScoreArray = bundle.getIntArray("trainScoreArray");
        m_trainCountArray = bundle.getIntArray("trainCountArray");
        m_cardScoreArray = bundle.getIntArray("cardScoreArray");
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
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        // Load dialog from custom layout xml
        LayoutInflater inflater = this.getLayoutInflater();

        final View inflatedView = inflater.inflate(R.layout.settings, null);

        boolean cardPickupToggleEnabledPref = m_preferences.getBoolean("Settings.CardPickup", true);
        boolean autoNextPlayerToggleEnabledPref = m_preferences.getBoolean("Settings.AutoNextPlayer", true);
        int stationValuePref = m_preferences.getInt("Settings.StationValue", getResources().getInteger(R.integer.perStationScore));

        final ToggleButton cardPickupToggle = (ToggleButton) inflatedView.findViewById(R.id.settingsCardPickupToggle);
        final ToggleButton autoNextPlayerToggle = (ToggleButton) inflatedView.findViewById(R.id.settingsChangePlayerToggle);
        final EditText stationValueTxt = (EditText) inflatedView.findViewById(R.id.settingsStationScoreEditTxt);

        cardPickupToggle.setChecked(cardPickupToggleEnabledPref);
        autoNextPlayerToggle.setChecked(autoNextPlayerToggleEnabledPref);
        stationValueTxt.setText(String.format(Locale.getDefault(), "%d", stationValuePref));

        alertBuilder.setView(inflatedView);

        alertBuilder.setPositiveButton(
                "Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        boolean cardPickupToggleEnabled = cardPickupToggle.isChecked();
                        boolean autoNextPlayerToggleEnabled = autoNextPlayerToggle.isChecked();

                        int stationValue;
                        String stationValueString = stationValueTxt.getText().toString();

                        if(TextUtils.isEmpty(stationValueString)) {
                            stationValue = getResources().getInteger(R.integer.perStationScore);
                        } else {
                            stationValue = Integer.parseInt(stationValueString);
                        }

                        SharedPreferences.Editor editor = m_preferences.edit();
                        editor.putBoolean("Settings.CardPickup", cardPickupToggleEnabled);
                        editor.putBoolean("Settings.AutoNextPlayer", autoNextPlayerToggleEnabled);
                        editor.putInt("Settings.StationValue", stationValue);
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

    /** Call to implement setting change updates **/
    abstract protected void doSettingUpdate();

    /** Call to add extra activity specific behaviour to onCreate method **/
    abstract protected void doActivitySetup();

    /** Call to display player stats **/
    abstract protected void displayPlayerStats();
}

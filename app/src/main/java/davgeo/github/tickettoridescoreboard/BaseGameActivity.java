package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

public abstract class BaseGameActivity extends AppCompatActivity {
    int m_playerNum;
    int m_noPlayers;
    int m_turnNo;
    int [] m_trainScoreArray;
    int [] m_trainCountArray;
    int [] m_stationCountArray;
    int [] m_cardScoreArray;
    String [] m_playerNameArray;
    boolean m_game_complete;

    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            case R.id.actionBarSummary:
                doSummaryDialog();
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.actionBarNewGame:
                // User chose the "New Game" action.

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
        bundle.putInt("turnNo", m_turnNo);
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
        m_turnNo = bundle.getInt("turnNo");
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
        return m_stationCountArray[playerNo-1]*getResources().getInteger(R.integer.perStationScore);
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

    /** Call to add extra activity specific behaviour to onCreate method **/
    abstract protected void doActivitySetup();

    /** Call to display player stats **/
    abstract protected void displayPlayerStats();
}

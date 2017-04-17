package davgeo.github.tickettoridescoreboard;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class EndGameActivity extends AppCompatActivity {
    int m_playerNum;
    int m_noPlayers;
    int m_turnNo;
    String [] m_playerNameArray;
    int [][] m_scoreboardArray;
    int [] m_routeScoreArray;

    // TODO : Create a common base class and inherit common behaviour

    /** Called when activity is created **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if(savedInstanceState != null) {
            loadBundleState((savedInstanceState));
        } else {
            Intent thisIntent = getIntent();
            Bundle intentBundle = thisIntent.getExtras();
            loadBundleState(intentBundle);
        }

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

        // TODO : Fix this (save state)
        m_routeScoreArray = new int[m_noPlayers];
        for(int i=0; i < m_routeScoreArray.length; i++) {
            m_routeScoreArray[i] = 0;
        }

        displayPlayerStats();
    }

    /** Call to return all state in a bundle **/
    protected void saveBundleState(Bundle bundle) {
        bundle.putInt("playerNum", m_playerNum);
        bundle.putInt("noPlayers", m_noPlayers);
        bundle.putInt("turnNo", m_turnNo);
        bundle.putStringArray("playerNameArray", m_playerNameArray);
        bundle.putSerializable("scoreboardArray", m_scoreboardArray);
    }

    /** Call to return all state in a bundle **/
    protected void loadBundleState(Bundle bundle) {
        m_playerNum = bundle.getInt("playerNum");
        m_noPlayers = bundle.getInt("noPlayers");
        m_turnNo = bundle.getInt("turnNo");
        m_playerNameArray = bundle.getStringArray("playerNameArray");
        m_scoreboardArray = (int[][]) bundle.getSerializable("scoreboardArray");
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

            for(int j=0; j < m_scoreboardArray[i].length; j++) {
                TextView sbTxtView = new TextView(this);
                sbTxtView.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[i][j]));
                sbTxtView.setGravity(Gravity.END);
                dialogTableRow.addView(sbTxtView);
            }

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
                                Intent intent = new Intent(EndGameActivity.this, MainActivity.class);
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

    /** Call to display player stats **/
    protected void displayPlayerStats() {
        // Player score - scoreboardArray[playerNum-1][0]
        // Remaining trains - scoreboardArray[playerNum-1][1]
        // Remaining stations - scoreboardArray[playerNum-1][2]
        int idx = m_playerNum - 1;

        Spinner spinner = (Spinner) findViewById(R.id.endGameSpinner);
        spinner.setSelection(idx);

        TextView trainScoreTxtView = (TextView) findViewById(R.id.endGameTableTrainScoreValue);
        trainScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][0]));

        TextView stationScoreTxtView = (TextView) findViewById(R.id.endGameTableStationScoreValue);
        stationScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_scoreboardArray[idx][2]*3));

        TextView routeScoreTxtView = (TextView) findViewById(R.id.endGameTableRouteScoreValue);
        routeScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_routeScoreArray[idx]));

        TextView totalScoreTxtView = (TextView) findViewById(R.id.endGameTableTotalScoreValue);
        totalScoreTxtView.setText(String.format(Locale.getDefault(), "%d",
                m_scoreboardArray[idx][0] + m_scoreboardArray[idx][2]*3 + m_routeScoreArray[idx]));
    }

    /** Load next player **/
    protected void goToNextPlayer() {
        m_playerNum++;

        if(m_playerNum > m_noPlayers) {
            m_playerNum = 1;
        }

        displayPlayerStats();
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
            m_routeScoreArray[idx] += routeScore;
        } else {
            m_routeScoreArray[idx] -= routeScore;
        }

        routeScoreEditText.setText("");

        TextView routeScoreTxtView = (TextView) findViewById(R.id.endGameTableRouteScoreValue);
        routeScoreTxtView.setText(String.format(Locale.getDefault(), "%d", m_routeScoreArray[idx]));

        TextView totalScoreTxtView = (TextView) findViewById(R.id.endGameTableTotalScoreValue);
        totalScoreTxtView.setText(String.format(Locale.getDefault(), "%d",
                m_scoreboardArray[idx][0] + m_scoreboardArray[idx][2]*3 + m_routeScoreArray[idx]));
    }

    /** Called when next player button clicked **/
    public void doNextPlayer(View view) {
        goToNextPlayer();
    }
}

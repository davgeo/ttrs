package davgeo.github.tickettoridescoreboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;

public class ScoreSheetActivity extends BaseGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_score_sheet);
        super.onCreate(savedInstanceState);
    }

    /** Add extra activity specific behaviour to onCreate method **/
    @Override
    protected void doActivitySetup() {
        m_game_complete = true;
    }

    /** Call to display player stats **/
    @Override
    protected void displayPlayerStats() {
        doScoreSheetTable();
    }

    /** Update score sheet table **/
    protected void doScoreSheetTable() {
        // Create sortable array including total score and array index
        Integer [][] totalScoreArray = new Integer [m_noPlayers][2];
        for(int i=0; i < m_noPlayers; i++) {
            totalScoreArray[i][0] = i;
            totalScoreArray[i][1] = getTotalScore(i+1);
        }

        // Descending sort on total score
        Arrays.sort(totalScoreArray, new Comparator<Integer[]>(){
            @Override
            public int compare(Integer[] x, Integer[] y) {
                return y[1].compareTo(x[1]);
            }
        });

        // Add table rows in score order (highest score first)
        TableLayout tableLayout = (TableLayout) findViewById(R.id.scoreSheetTable);

        for (Integer[] i:totalScoreArray) {
            TableRow dialogTableRow = new TableRow(this);

            TextView nameTxtView = new TextView(this);
            nameTxtView.setText(m_playerNameArray[i[0]]);
            nameTxtView.setLayoutParams(new TableRow.LayoutParams(1));
            dialogTableRow.addView(nameTxtView);

            addValueToTable(m_trainScoreArray[i[0]], dialogTableRow);
            addValueToTable(getStationScore(i[0]+1), dialogTableRow);
            addValueToTable(m_cardScoreArray[i[0]], dialogTableRow);
            addValueToTable(i[1], dialogTableRow);

            tableLayout.addView(dialogTableRow);
        }
    }
}

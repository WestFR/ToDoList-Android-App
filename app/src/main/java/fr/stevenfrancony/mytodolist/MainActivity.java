package fr.stevenfrancony.mytodolist;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<Comment> mTweets;

    private TextView mTextStatus;

    private FloatingActionButton addButton;
    private FloatingActionButton clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Binding variables to UI elements
        mTextStatus = (TextView) findViewById(R.id.list_status);
        mListView = (ListView) findViewById(R.id.list);
        addButton = (FloatingActionButton)  findViewById(R.id.addButton);
        clearButton = (FloatingActionButton) findViewById(R.id.clearButton);

        mTweets = generateData();
        refreshList();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDialog();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDialog();
            }
        });

        mListView.setLongClickable(true);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                deleteOne(pos);
                return true;
            }
        });
    }

    private void deleteOne(int pos) {
        final int position = pos;
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Supprimer élément.");
        alert.setMessage("Voulez-vous vraiment supprimer l'élement sélectionné ?");

        alert.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mTweets.remove(position);
                refreshList();
            }
        });

        alert.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void clearDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Supprimer liste.");
        alert.setMessage("Voulez-vous supprimer toute la liste de tâches ?");

        alert.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mTweets = new ArrayList<Comment>();
                refreshList();
            }
        });

        alert.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void addDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ajouter tâche.");
        alert.setMessage("Compléter les informations pour ajouter une tâche à la liste.");

        // Create TextView
        final EditText name = new EditText (this);
        name.setHint("Prénom...");

        final EditText text = new EditText(this);
        text.setHint("Tâche...");

        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.addView(name);
        layout.addView(text);

        alert.setView(layout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Random color & add to list
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                if(name.length() > 0 || text.length() > 0) {
                    mTweets.add(new Comment(color, name.getText().toString(), text.getText().toString()));
                    refreshList();
                }
            }
        });

        alert.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void refreshList() {
        RowAdapter adapter = new RowAdapter(MainActivity.this, mTweets);
        mListView.setAdapter(adapter);

        if(mTweets.size() > 0 ) {
            mTextStatus.setText("Voici votre liste de tâches :");
        }
        else {
            mTextStatus.setText("La liste ne contient aucun élément !");
        }
    }

    private List<Comment> generateData(){

        mTweets = new ArrayList<>();
        mTweets.add(new Comment(Color.BLACK, "Florent", "Aller chercher du café !"));
        return mTweets;
    }
}

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private List<Comment> mTweets;
    private Comment mComment;

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
                deleteOnePos(position);
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
                deleteAll();
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
                    mComment = new Comment(color, name.getText().toString(), text.getText().toString());
                    AddItem(mComment);
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

    private List<Comment> generateData() {
        mTweets = new ArrayList<>();
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData",null);

        if(myData != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(myData);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String data  = jsonArray.getString(i);
                    String[] splitData = data.split("\\.");
                    mTweets.add(new Comment(Integer.parseInt(splitData[0]), splitData[1], splitData[2]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            mComment = new Comment(Color.BLACK, "Florent", "Aller chercher du café !");
            AddItem(mComment);
        }

        return mTweets;
    }

    private void deleteAll() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        JSONArray jsonArray = new JSONArray();
        mTweets = new ArrayList<Comment>();

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray.toString());
        editor.apply();
    }

    private void AddItem(Comment e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        if(myData == null) {
            jsonArray = new JSONArray();
            jsonArray.put(e.getColor() + "." + e.getPseudo() + "." + e.getText());
            mTweets.add(e);
        }
        else {
            try {
                jsonArray = new JSONArray(myData);

                jsonArray.put(e.getColor() + "." + e.getPseudo() + "." + e.getText());
                mTweets.add(e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray.toString());
        editor.apply();
    }

    private void deleteOnePos(int pos) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(myData);

            jsonArray.remove(pos);
            mTweets.remove(pos);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray.toString());
        editor.apply();
    }

}

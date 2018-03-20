/**package co.schrodingertech.safeveremotemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajwad on 03-03-2018.
 */
/**public class DBmanage extends AppCompatActivity {
    ArrayList<String> dblistItems = new ArrayList<String>();
    ArrayAdapter<String> dbadapter;
    private ListView dbListView;
    final Context context = this;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_db);
        StringBuilder smsBuilder = new StringBuilder();
        final DBHandler db = new DBHandler(this);
        List<Users> users = db.getAllUsers();
        dbadapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                dblistItems);
        setListAdapter(dbadapter);
        if (dbListView == null) {
            dbListView = (ListView) findViewById(R.id.dbList);
        }

        for(Users u: users) {
            try {
                smsBuilder = new StringBuilder();
                Log.d("Build", u.getAddress());
                smsBuilder.append(u.getId()+"\t");
                smsBuilder.append(u.getName()+"\t");
                smsBuilder.append(u.getAddress()+"\n");
                String final1 = smsBuilder.toString();
                Log.d("Final1", final1);
                dblistItems.add(final1);
                dbadapter.notifyDataSetChanged();
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }
        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.addFab);

        dbListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String tempint = Integer.toString(position);
                String item = ((TextView) view).getText().toString();
                Log.d("position", tempint);
                Toast.makeText(getBaseContext(), item, Toast.LENGTH_LONG).show();
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.add_prompt, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);

                final EditText nameInput = (EditText) promptsView.findViewById(R.id.nameBlank);
                final EditText numberInput = (EditText) promptsView.findViewById(R.id.numberBlank);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        // get user input and set it to result
                                        // edit text
                                        String nameIP = (nameInput.getText()).toString();
                                        String numberIP = (numberInput.getText().toString());
                                        int finid = db.getUserCount() + 1 ;
                                        String finno = "+91"+numberIP;
                                        db.addUser(new Users(finid, nameIP, finno));
                                        db.close();
                                        refresh();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });
    }

    protected ListView getListView() {
        if (dbListView == null) {
            dbListView = (ListView) findViewById(R.id.dbList);
        }
        return dbListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    public void refresh() {
        ArrayList<String> dblistItems = new ArrayList<String>();
        ArrayAdapter<String> dbadapter;
        ListView dbListView = (ListView) findViewById(R.id.dbList);
        StringBuilder smsBuilder;
        final DBHandler db = new DBHandler(this);
        List<Users> users = db.getAllUsers();
        dbadapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                dblistItems);
        setListAdapter(dbadapter);
        for(Users u: users) {
            try {
                smsBuilder = new StringBuilder();
                Log.d("Build", u.getAddress());
                smsBuilder.append(u.getId()+"\t");
                smsBuilder.append(u.getName()+"\t");
                smsBuilder.append(u.getAddress()+"\n");
                String final1 = smsBuilder.toString();
                Log.d("Final1", final1);
                dblistItems.add(final1);
                dbadapter.notifyDataSetChanged();
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.db_refresh_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
*/

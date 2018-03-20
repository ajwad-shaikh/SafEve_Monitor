package co.schrodingertech.safeveremotemonitor;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class Monitor extends AppCompatActivity {

    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    int clickCounter = 1;
    private ListView mListView;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        DBHandler db = new DBHandler(this);
        List<Users> users = db.getAllUsers();
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_row,
                listItems);
        if (checkAndRequestPermissions()) {
            setListAdapter(adapter);// carry on the normal flow, as the case of  permissions  granted.
        }
        FloatingActionButton mButton = (FloatingActionButton) findViewById(R.id.fab);
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.listDemo);
        }
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));

        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        StringBuilder smsBuilder = new StringBuilder();
        int msgCount = 0;
        for(Users u: users) {
            String tref = u.getAddress();
            try {
                Uri uri = Uri.parse(SMS_URI_INBOX);
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String tquery = "address='" + tref + "'";
                Cursor cur = getContentResolver().query(uri, projection, tquery, null, "date desc");
                if (cur.moveToFirst()) {
                    do {
                        msgCount++;
                    } while (cur.moveToNext());
                    clickCounter = msgCount;
                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                }
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }


            try {
                Uri uri = Uri.parse(SMS_URI_INBOX);
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                //String tquery = "address='"+ tref + "'";
                Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");
                if (cur.moveToFirst()) {
                    int index_Address = cur.getColumnIndex("address");
                    int index_Person = cur.getColumnIndex("person");
                    int index_Body = cur.getColumnIndex("body");
                    int index_Date = cur.getColumnIndex("date");
                    int index_Type = cur.getColumnIndex("type");
                    do {
                        if(msgCount==0)
                            break;
                        smsBuilder = new StringBuilder();
                        String strAddress = cur.getString(index_Address);
                        //Log.d("Address", strAddress);
                        for(Users u:users) {
                            if (strAddress.equals(u.getAddress())) {
                                int intPerson = cur.getInt(index_Person);
                                String strbody = cur.getString(index_Body);
                                long longDate = cur.getLong(index_Date);
                                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
                                Date resultdate = new Date(longDate);
                                int int_Type = cur.getInt(index_Type);

                                smsBuilder.append(msgCount-- + ". \t ");
                                //smsBuilder.append("[ ");
                                smsBuilder.append(u.getName() + " \t ");
                                //smsBuilder.append(intPerson + ", ");
                                smsBuilder.append(u.getAddress() + " \t ");
                                smsBuilder.append("\n  \t\t   Timestamp: "+sdf.format(resultdate) + " \t ");
                                smsBuilder.append("\n"+strbody);
                                //smsBuilder.append(int_Type);
                                //smsBuilder.append(" ]\n\n");
                                String final1 = smsBuilder.toString();
                                listItems.add(final1);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }



                    } while (cur.moveToNext());
                    //clickCounter = msgCount;
                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                } else {
                    smsBuilder.append("no result!");
                }
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }
    //METHOD WHICH WILL HANDLE DYNAMIC INSERTION
    public void addToList(View v) {
        listItems.add(0, "Alert : " + clickCounter++);
        adapter.notifyDataSetChanged();
    }

    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.listDemo);
        }
        return mListView;
    }

    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }

    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter) adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                listItems.add(0, clickCounter++ + " " + message);
                adapter.notifyDataSetChanged();
                mBuilder.setSmallIcon(R.drawable.icon);
                mBuilder.setContentTitle("SafEve Alert Received!");
                mBuilder.setContentText(message);
                mBuilder.setAutoCancel(true);
                Intent resultIntent = new Intent(Monitor.this, Monitor.class);
                stackBuilder.addParentStack(Monitor.class);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(0, mBuilder.build());
            }
        }
    };

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    public void refresh(View v) {
        mListView.setAdapter(null);
        listItems = new ArrayList<String>();
        clickCounter = 1;
        DBHandler db = new DBHandler(this);
        List<Users> users = db.getAllUsers();
        adapter = new ArrayAdapter<String>(this,
                R.layout.list_row,
                listItems);
        if (checkAndRequestPermissions()) {
            setListAdapter(adapter);// carry on the normal flow, as the case of  permissions  granted.
        }
        FloatingActionButton mButton = (FloatingActionButton) findViewById(R.id.fab);
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.listDemo);
        }
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_ALL = "content://sms/";
        StringBuilder smsBuilder = new StringBuilder();
        int msgCount = 0;
        for(Users u: users) {
            String tref = u.getAddress();
            try {
                Uri uri = Uri.parse(SMS_URI_INBOX);
                String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
                String tquery = "address='" + tref + "'";
                Cursor cur = getContentResolver().query(uri, projection, tquery, null, "date desc");
                if (cur.moveToFirst()) {
                    do {
                        msgCount++;
                    } while (cur.moveToNext());
                    clickCounter = msgCount;
                    if (!cur.isClosed()) {
                        cur.close();
                        cur = null;
                    }
                }
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }

        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            //String tquery = "address='"+ tref + "'";
            Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc");
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {
                    if(msgCount==0)
                        break;
                    smsBuilder = new StringBuilder();
                    String strAddress = cur.getString(index_Address);
                    //Log.d("Address", strAddress);
                    for(Users u:users) {
                        if (strAddress.equals(u.getAddress())) {
                            int intPerson = cur.getInt(index_Person);
                            String strbody = cur.getString(index_Body);
                            long longDate = cur.getLong(index_Date);
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm");
                            Date resultdate = new Date(longDate);
                            int int_Type = cur.getInt(index_Type);

                            smsBuilder.append(msgCount-- + ". \t ");
                            //smsBuilder.append("[ ");
                            smsBuilder.append(u.getName() + " \t ");
                            //smsBuilder.append(intPerson + ", ");
                            smsBuilder.append(u.getAddress() + " \t ");
                            smsBuilder.append("\n  \t\t   Timestamp: "+sdf.format(resultdate) + " \t ");
                            //smsBuilder.append(int_Type);
                            //smsBuilder.append(" ]\n\n");
                            smsBuilder.append("\n"+strbody);
                            String final1 = smsBuilder.toString();
                            listItems.add(final1);
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }

                } while (cur.moveToNext());
                //clickCounter = msgCount;
                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }
        } catch (SQLiteException ex) {
            Log.d("SQLiteException", ex.getMessage());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.monitor_menu, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent intent1 = new Intent(this, DataBaseManager.class);
                this.startActivity(intent1);
                return true;
            case R.id.item2:
                LayoutInflater li = LayoutInflater.from(this);
                final DBHandler db = new DBHandler(this);
                View promptsView = li.inflate(R.layout.add_prompt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
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
                                        Toast.makeText(getBaseContext(), "User Added Successfully!", Toast.LENGTH_SHORT).show();
                                        refresh(getCurrentFocus());
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
                return true;
            case R.id.item3:
                Intent intent = new Intent(this, AboutPage.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}





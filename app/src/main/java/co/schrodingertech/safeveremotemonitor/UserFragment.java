package co.schrodingertech.safeveremotemonitor;

import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajwad on 04-03-2018.
 */
public class UserFragment extends ListFragment implements View.OnClickListener {
    ArrayList<String> dblistItems = new ArrayList<String>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        StringBuilder smsBuilder;
        final DBHandler db = new DBHandler(getActivity());
        List<Users> users = db.getAllUsers();
        for(Users u: users) {
            try {
                smsBuilder = new StringBuilder();
                int tot = db.getUserCount();
                int [] aj = new int[tot];
                Log.d("Build", u.getAddress());
                smsBuilder.append(u.getId()+"\t");
                smsBuilder.append(u.getName()+"\t");
                smsBuilder.append(u.getAddress() + "\n");
                String final1 = smsBuilder.toString();
                Log.d("Final1", final1);
                dblistItems.add(final1);
            } catch (SQLiteException ex) {
                Log.d("SQLiteException", ex.getMessage());
            }
        }
        setListAdapter(new PopupAdapter(dblistItems));
    }

    @Override
    public void onListItemClick(ListView listView, View v, int position, long id) {
        String item = (String) listView.getItemAtPosition(position);

        // Show a toast if the user clicks on an item
        Toast.makeText(getActivity(), "Item Clicked: " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(final View view) {
        // We need to post a Runnable to show the popup to make sure that the PopupMenu is
        // correctly positioned. The reason being that the view may change position before the
        // PopupMenu is shown.
        view.post(new Runnable() {
            @Override
            public void run() {
                showPopupMenu(view);
            }
        });
    }

    private void showPopupMenu(View view) {
        final PopupAdapter adapter = (PopupAdapter) getListAdapter();

        // Retrieve the clicked item from view's tag
        final String item = (String) view.getTag();

        // Create a PopupMenu, giving it the clicked view for an anchor
        PopupMenu popup = new PopupMenu(getActivity(), view);

        // Inflate our menu resource into the PopupMenu's Menu
        popup.getMenuInflater().inflate(R.menu.del_popup, popup.getMenu());

        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        String casein = item.toString();
                        StringBuilder try1 = new StringBuilder();
                        //casein = casein.replaceAll("\\D+", "");
                        for (int i = 0; i < casein.length(); i++) {
                            if (Character.digit(casein.charAt(i), 10) < 0) break;
                            try1.append(casein.charAt(i));
                        }
                        String fin = try1.toString();
                        long fint = Long.parseLong(fin);
                        // Remove the item from the adapter
                        Log.d("casein", fin);
                        adapter.remove(item);
                        DBHandler db = new DBHandler(getActivity());
                        List<Users> users = db.getAllUsers();
                        for (Users u : users) {
                            if (u.getId() == fint) {
                                db.deleteUser(u);
                                break;
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        // Finally show the PopupMenu
        popup.show();
    }

    class PopupAdapter extends ArrayAdapter<String> {

        PopupAdapter(ArrayList<String> items) {
            super(getActivity(), R.layout.user_db, android.R.id.text1, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // Let ArrayAdapter inflate the layout and set the text
            View view = super.getView(position, convertView, container);

            // BEGIN_INCLUDE(button_popup)
            // Retrieve the popup button from the inflated view
            View popupButton = view.findViewById(R.id.button_popup);

            // Set the item as the button's tag so it can be retrieved later
            popupButton.setTag(getItem(position));

            // Set the fragment instance as the OnClickListener
            popupButton.setOnClickListener(UserFragment.this);
            // END_INCLUDE(button_popup)

            // Finally return the view to be displayed
            return view;
        }
    }

}

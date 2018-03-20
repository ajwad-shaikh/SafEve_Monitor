package co.schrodingertech.safeveremotemonitor;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.AccessibleObject;

/**
 * Created by Ajwad on 04-03-2018.
 */
public class DataBaseManager extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set content view (which contains a PopupListFragment)
        setContentView(R.layout.user_fragment);
    }
}

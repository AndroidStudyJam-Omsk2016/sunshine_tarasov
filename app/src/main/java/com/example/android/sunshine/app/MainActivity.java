package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();

        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id==R.id.view_map){

            SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
            String zipCode = preferences.getString(getString(R.string.location_key), getString(R.string.location_default));
            Uri location = Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q", zipCode).build();
            Intent intentMap = new Intent(Intent.ACTION_VIEW);
            intentMap.setData(location);

            //Если нет приложений которые могут использовать Map
            if (intentMap.resolveActivity(getPackageManager())!=null){
                startActivity(intentMap);
                return true;
            }
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

}



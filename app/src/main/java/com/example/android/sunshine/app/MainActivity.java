package com.example.android.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private String mLocation;
    private final String FORECASTFRAGMENT_TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();

        }
    }

    @Override
    public void onResume(){
        super.onResume();

        String location = Utility.getPreferredLocation(this);

        if (location != null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if (null != ff) ff.onLocationChanged();
        }

        mLocation =location;

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

            String zipCode = Utility.getPreferredLocation(this);
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



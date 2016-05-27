package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.sync.SunshineSyncAdapter;

public class DetailActivity extends ActionBarActivity {

//    @Override
//    public void onResume(){
//        super.onResume();
//        updateWeather();
//    }



    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(this);
    }

    public static final String DATE_KEY = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

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

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecast;
        private ShareActionProvider mShareActionProvider;
        private static final int DETAIL_LOADER = 0;
        static final String DETAIL_URI = "URI";
        private Uri mUri;

        private ImageView iconView;
        private TextView descriptionView;
        private TextView dateView;
        private TextView frendlyDateView;
        private TextView highTempView;
        private TextView lowTempView;
        private TextView humidityView;
        private TextView windView;
        private TextView pressureView;

        // Strings

        private static final String[] DETAIL_COLUMNS = {

                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATE,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
                WeatherContract.WeatherEntry.COLUMN_DEGREES,
                WeatherContract.WeatherEntry.COLUMN_PRESSURE,
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        };

        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;
        static final int COL_HUMIDITY = 5;
        static final int COL_WIND_SPEED = 6;
        static final int COL_DEGREES = 7;
        static final int COL_PRESSURE = 8;
        static final int COL_WEATHER_ICON_ID = 9;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            }

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            iconView = (ImageView) rootView.findViewById(R.id.detail_icon);
            descriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);


            frendlyDateView = (TextView) rootView.findViewById(R.id.detail_frendly_date_textview);
            dateView = (TextView) rootView.findViewById(R.id.detail_date_textview);

            highTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);

            humidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
            windView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
            pressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

            return rootView;
        }

        void onLocationChanged(String newLocation) {
            // replace the uri, since the location has changed
            Uri uri = mUri;
            if (null != uri) {
                long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
                Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
                mUri = updatedUri;
                getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {

            // Inflate menu resource file.
            menuInflater.inflate(R.menu.detail_share, menu);

            // Locate MenuItem with ShareActionProvider
            MenuItem item = menu.findItem(R.id.action_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

            if (mForecast != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }

        private Intent createShareForecastIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    mForecast + FORECAST_SHARE_HASHTAG);
            return shareIntent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if ( null != mUri ) {
            return new CursorLoader(getActivity(), mUri,
                    DETAIL_COLUMNS, null, null, null);
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


            if (data != null && data.moveToFirst()) {
                //get Date
                String frendlyDateString = Utility.getDayName(getActivity(),data.getLong(COL_WEATHER_DATE));
                dateView.setText(frendlyDateString);
                String dateString = Utility.getFormattedMonthDay(getActivity(),data.getLong(COL_WEATHER_DATE));
                frendlyDateView.setText(dateString);

                //get Temperature
                double high = data.getDouble(COL_WEATHER_MAX_TEMP);
                boolean isMetric = Utility.isMetric(getActivity());
                String highString = Utility.formatTemperature(getActivity(), high);
                highTempView.setText(highString);
                double low = data.getDouble(COL_WEATHER_MIN_TEMP);
                String lowString = Utility.formatTemperature(getActivity(), low);
                lowTempView.setText(lowString);

                //get Humidity, Wind and Pressure
                float humidity = data.getFloat(COL_HUMIDITY);
                humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));
                float speedWind = data.getFloat(COL_WIND_SPEED);
                float degrese = data.getFloat(COL_DEGREES);
                String convertWindDegrese = Utility.getFormattedWind(getActivity(), speedWind, degrese);
                windView.setText(convertWindDegrese);
                float pressure =data.getFloat(COL_PRESSURE);
                pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

                //get Icon and Description Weather
                iconView.setImageResource(Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_ICON_ID)));
                String weatherDescription = data.getString(COL_WEATHER_DESC);
                descriptionView.setText(weatherDescription);

                mForecast = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

                // If onCreateOptionsMenu has already happened, we need to update the share intent now.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                }
            }


        }



        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }
    }


}
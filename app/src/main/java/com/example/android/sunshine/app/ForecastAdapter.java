package com.example.android.sunshine.app;

/**
 * Created by Flame on 09.05.2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high) + "/" + Utility.formatTemperature(mContext, low);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
//    private String convertCursorRowToUXFormat(Cursor cursor) {
//        // get row indices for our cursor
//
//        String highAndLow = formatHighLows(
//                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
//                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
//
//        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
//                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
//                " - " + highAndLow;
//    }
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private boolean mUseTodayLayout = true;

    public void specialDay (boolean useToday){
       mUseTodayLayout = useToday;
    }
    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_forecast;
                break;
            }
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;

    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        final ViewHolder viewHolder = new ViewHolder(view);

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
//        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        int viewType = getItemViewType(cursor.getPosition());
        if (viewType==0){
            viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }
        else {
            viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID)));
        }


        // TODO Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
//        TextView datetext = (TextView) view.findViewById(R.id.list_item_date_textview);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, date));

        // TODO Read weather forecast from cursor
        String weatherDescription = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(weatherDescription);

        viewHolder.iconView.setContentDescription(weatherDescription);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
//        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high));

        // TODO Read low temperature from cursor
        double min = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
//        TextView minView = (TextView) view.findViewById(R.id.list_item_low_textview);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, min));

    }

}

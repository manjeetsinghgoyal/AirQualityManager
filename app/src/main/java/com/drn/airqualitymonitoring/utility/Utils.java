package com.drn.airqualitymonitoring.utility;

import android.content.Context;
import com.drn.airqualitymonitoring.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    public static String timeAgo(long millisecond) {
        long milliSecPerMinute = 60 * 1000; //Milliseconds Per Minute
        long milliSecPerHour = milliSecPerMinute * 60; //Milliseconds Per Hour
        long msExpired = System.currentTimeMillis() - millisecond;
        if (msExpired < milliSecPerMinute) {
            return  "A few seconds ago";
        }
        else if (msExpired < milliSecPerHour) {
            return  "A minute ago";
        }
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millisecond);
            return formatter.format(calendar.getTime());
        }
    }
    public static int getColorCode(Context context, String aqr){
        double d = Double.parseDouble(aqr);
        if(d >=0 && d<51)
            return context.getResources().getColor(R.color.good);
        else if(d>=51 && d<101)
            return context.getResources().getColor(R.color.satisfactory);
        else if(d>=101 && d<201)
            return context.getResources().getColor(R.color.moderate);
        else if(d>=201 && d<301)
            return context.getResources().getColor(R.color.poor);
        else if(d>=301 && d<401)
            return context.getResources().getColor(R.color.verypoor);
        else
            return context.getResources().getColor(R.color.severe);

    }
}

package com.drn.airqualitymonitoring.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.drn.airqualitymonitoring.R;
import com.drn.airqualitymonitoring.model.CityAQM;
import com.drn.airqualitymonitoring.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import static com.drn.airqualitymonitoring.utility.Utils.getColorCode;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TableLayout table;
    private TableRow tr;
    private String lastSelectedCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        table = findViewById(R.id.table);


        ConnectToSocket();
    }
    private WebSocketClient mWebSocketClient;
    private  List<CityAQM>  cityAQMArrayList = new ArrayList<>();
    private void ConnectToSocket() {
        try {
            java.net.URI uri;
            try {
                uri = new URI("ws://city-ws.herokuapp.com/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    Log.i("Websocket", "Opened");
                }

                @Override
                public void onMessage(String s) {
                    final String message = s;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("Websocket",   message);
                            if(message.startsWith("[")) {

                                Type type = new TypeToken<ArrayList<CityAQM>>() {}.getType();
                                List<CityAQM> cityAQMList = new Gson().fromJson(message, type);
                                int i = 0;

                                for(Iterator<CityAQM> bookIterator = cityAQMArrayList.iterator(); bookIterator.hasNext();){
                                    CityAQM cityAQM = bookIterator.next();
                                    for(CityAQM temp : cityAQMList) {
                                        if (temp.getCity().equalsIgnoreCase(cityAQM.getCity())) {
                                            bookIterator.remove();
                                        }
                                    }
                                    i++;
                                }

                                cityAQMArrayList.addAll(cityAQMList);
                            }

                            //For child items
                            table.removeAllViews();
                            //For header title
                            tr = new TableRow(MainActivity.this);
                            addHeader("City",0);
                            addHeader("Current AQI",0);
                            addHeader("Last Updated",0);
                            table.addView(tr,0);
                            Collections.sort(cityAQMArrayList, new Comparator<CityAQM>() {
                                @Override
                                public int compare(CityAQM o1, CityAQM o2) {
                                    return  Double.compare(o2.getAqi(),o1.getAqi());
                                }
                            });
                            for(Iterator<CityAQM> bookIterator = cityAQMArrayList.iterator(); bookIterator.hasNext();) {
                                CityAQM cityAQM = bookIterator.next();
                                tr = new TableRow(MainActivity.this);
                                addHeader(cityAQM.getCity(), 1);
                                addHeader(cityAQM.getAqi()+"", 2);
                                addHeader(Utils.timeAgo(cityAQM.getLastUpdated()), 1);
                                table.addView(tr, 1);
                                tr.setTag(cityAQM.getCity());
                                tr.setOnClickListener(MainActivity.this);
                                if(cityAQM.getCity().equalsIgnoreCase(lastSelectedCity)){
                                    EventBus.getDefault().post(cityAQM.getAqi());
                                }
                            }
                        }
                    });
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.i("Websocket", "Closed " + s);
                }

                @Override
                public void onError(Exception e) {
                    Log.i("Websocket", "Error " + e.getMessage());
                }
            };
            mWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addHeader(String headerName,int colorCode){
        LinearLayout cell = new LinearLayout(this);
        TableRow.LayoutParams llp = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
        cell.setBackgroundColor(colorCode == 0 ? getResources().getColor(R.color.green) : getResources().getColor(R.color.white));
        cell.setLayoutParams(llp);//2px border on the right for the cell
        TextView tv = new TextView(this);
        if(colorCode == 2){
            DecimalFormat df = new DecimalFormat("##.##");
            double d = Double.parseDouble(headerName);
            tv.setText(df.format(d)+"");
        }else{
            tv.setText(headerName);
            Typeface typeface = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typeface = getResources().getFont(R.font.comic);
                tv.setTypeface(typeface);
            }
        }
        tv.setTextSize(colorCode == 0 ? 18 : 16);
        tv.setTextColor((colorCode == 0 || colorCode == 1) ? getResources().getColor(R.color.black) :
                getColorCode(MainActivity.this,headerName));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setPadding(20,20,20,20);
        cell.addView(tv);
        tr.addView(cell, new TableRow.LayoutParams(0,
                TableRow.LayoutParams.WRAP_CONTENT, 1f));
    }


    @Override
    public void onClick(View v) {
        lastSelectedCity = (String) v.getTag();
        Intent intent = new Intent(MainActivity.this,GraphActivity.class);
        intent.putExtra("cityName",lastSelectedCity);
        startActivity(intent);
    }
}

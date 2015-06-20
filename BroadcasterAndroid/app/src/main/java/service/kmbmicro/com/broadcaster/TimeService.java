package service.kmbmicro.com.broadcaster;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by sandiwibowo on 6/10/15.
 */
public class TimeService extends Service {
    // constant
    public static String GPS="GPS";
    public static String MESSAGE_ACT="msgACT";
    static final public String outServ = "TimeService.outServ";
    public  static String MESSAGE_BC="msgBC";


    private int NOTIFY_INTERVAL = 10 * 1000; // 10 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private SimpleLocation gpsLocation;
    private sendHttpPost shp;


    //    private sendHttpPost shp;
    private String dataR;
    private String amazUrl;
    private String clientParameter;
    private String idParameter;
    private String latParameter;
    private String lngParameter;
    private String dscrParameter;
    private String clientRequest;
    private String idRequest;
    private String dscrStr;
    private String toastStr;

    private double latitude;
    private double longitude;

    Intent intent;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        intent = new Intent(outServ);
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId){
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        clientParameter=getResources().getString(R.string.clientParameter);
        idParameter=getResources().getString(R.string.IdParameter);
        latParameter=getResources().getString(R.string.latParameter);
        lngParameter=getResources().getString(R.string.lngParameter);
        dscrParameter=getResources().getString(R.string.dscrParameter);
        clientRequest=getResources().getString(R.string.clientRequest);
        amazUrl=getResources().getString(R.string.urlserver);
        dataR="starting";
        dscrStr=intent.getStringExtra(MainActivity.DESCRIPTION_INTENT);
        NOTIFY_INTERVAL=intent.getIntExtra(MainActivity.INTERVAL_INTENT,NOTIFY_INTERVAL);
        if(gpsLocation==null){
            //create gps location instance
            gpsLocation = new SimpleLocation(this,true,false,NOTIFY_INTERVAL);
        }
        // if we can't access the location yet
        if (!gpsLocation.hasLocationEnabled()) {
            sendGPS(false);
            // ask the user to enable location access
            //SimpleLocation.openSettings(this);
        }
        else{
            sendGPS(true);
            //initiate http post request
            if(shp==null){
                shp=new sendHttpPost();
            }
            shp.execute();
            //for now id is generated randomly;
            Random r = new Random();
            int i1 = r.nextInt(1000000 - 0 + 1) + 0;
            idRequest=Integer.toString(i1);
            i1 = r.nextInt(1000000 - 0 + 1) + 0;
            idRequest+=Integer.toString(i1);
            i1 = r.nextInt(1000000 - 0 + 1) + 0;
            idRequest+=Integer.toString(i1);
            // schedule task
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
        }



        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        InfoSentAct(false);
        if(mTimer != null) {
            mTimer.cancel();
//            mTimer.purge();
//            mTimer=null;
        }
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    updateStatus();

                }

            });
        }
    }
    private void updateStatus(){
        if(shp.getStatus()==AsyncTask.Status.RUNNING || shp.getStatus()== AsyncTask.Status.PENDING){
            //Still Running do nothing

        }
        else if(shp.getStatus()==AsyncTask.Status.FINISHED){
            shp=new sendHttpPost();
            shp.execute();
        }

    }
    private class sendHttpPost extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            postData();
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            //Done AsyncTask
            InfoSentAct(true);
        }
    }
    public void postData(){
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(amazUrl);
        latitude = gpsLocation.getLatitude();
        longitude = gpsLocation.getLongitude();
        String latS=Double.toString(latitude);
        String lngS=Double.toString(longitude);


        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
            nameValuePairs.add(new BasicNameValuePair(clientParameter, clientRequest));
            nameValuePairs.add(new BasicNameValuePair(idParameter, idRequest));
            nameValuePairs.add(new BasicNameValuePair(latParameter, latS));
            nameValuePairs.add(new BasicNameValuePair(lngParameter, lngS));
            nameValuePairs.add(new BasicNameValuePair(dscrParameter, dscrStr));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            dataR= EntityUtils.toString(response.getEntity());
            Log.v("data", dataR);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }
    private void InfoSentAct(boolean b) {
        intent.putExtra(MESSAGE_ACT,b);
        sendBroadcast(intent);
    }
    private void sendGPS(boolean b){
        intent.putExtra(GPS,b);
        sendBroadcast(intent);
    }
}

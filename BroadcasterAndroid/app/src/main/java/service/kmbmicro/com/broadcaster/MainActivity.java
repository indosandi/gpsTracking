package service.kmbmicro.com.broadcaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    public  static String DESCRIPTION_INTENT="dscrEd";
    public  static String INTERVAL_INTENT="interval_time";
    public  static String MESSAGE_BC="msgBC";
    public  static String GPS_ERROR="GPSERROR";
    private String gpsError;
    private String msgBc;
    private String msgFl;

    private boolean runRepeat=false;
    private Intent itn;
    private int intervalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
        itn=new Intent(MainActivity.this, TimeService.class);
        defaultConf();
        //by default is one minutes
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(TimeService.outServ));
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void addListenerOnButton() {
        final Button button = (Button) findViewById(R.id.buttonBroad);
        final EditText ed=(EditText) findViewById(R.id.txtDescription);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                String buttonText;
                itn.putExtra(DESCRIPTION_INTENT,ed.getText().toString());
                if(runRepeat){
                    stopService(itn);
                    buttonText=getResources().getString(R.string.tracking_is_off);
                    button.setText(buttonText);
                    runRepeat=false;
                }
                else{
                    startService(itn);
                    buttonText=getResources().getString(R.string.tracking_is_on);
                    button.setText(buttonText);
                    runRepeat=true;
                }

            }
        });
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.i1:
                if (checked)
                    intervalTime=getResources().getInteger(R.integer.i1_interval);
                break;
            case R.id.i5:
                if (checked)
                    intervalTime=getResources().getInteger(R.integer.i5_interval);
                break;
            case R.id.i15:
                if (checked)
                    intervalTime=getResources().getInteger(R.integer.i15_interval);
                break;
        }
        itn.putExtra(INTERVAL_INTENT,intervalTime);
    }
    private void defaultConf(){
        intervalTime=getResources().getInteger(R.integer.i1_interval);
        String s=getResources().getString(R.string.message_success);
        itn.putExtra(INTERVAL_INTENT,intervalTime);
        gpsError=getResources().getString(R.string.gps_error);
        msgBc=getResources().getString(R.string.message_success);
        msgFl=getResources().getString(R.string.message_no);
    }
    private void updateUI(Intent intent){
        TextView tv=(TextView)findViewById(R.id.txtBroadcast);
        if(!intent.getExtras().getBoolean(TimeService.GPS)){
            tv.setText(gpsError);
            runRepeat=false;
            stopService(itn);
        }
        else{
            if(intent.getExtras().getBoolean(TimeService.MESSAGE_ACT)){
                tv.setText(msgBc);
            }
            else{
                tv.setText(msgFl);
            }
        }

    }
}

package in.bittechpro.technician;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class StateFragment extends Fragment {


    public StateFragment() {
        // Required empty public constructor
    }


    View view;
    DeviceDetail deviceDetail;
    Context applicationContext;
    /*DBHelper dbHelper;
    String[] deviceName;
    BigInteger[] deviceNumber;
    int[] deviceState;*/

    HashMap<String,String> params;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_state, container, false);





        applicationContext = MainActivity.getContextOfApplication();

        deviceDetail = new DeviceDetail(applicationContext);

        MainActivity.mainTitle.setText("Need For Service");

        MainActivity.getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);

        /*deviceName = deviceDetail.deviceName();
        deviceNumber = deviceDetail.deviceNumber();
        deviceState = deviceDetail.deviceState();

        dbHelper = new DBHelper(applicationContext);*/
        sharedpreferences = applicationContext.getSharedPreferences(SPrefManager.PREF_NAME, Context.MODE_PRIVATE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*setState();

        updateState();

        deviceState = new DeviceDetail(applicationContext).deviceState();
        deviceName = new DeviceDetail(applicationContext).deviceName();
        deviceNumber = new DeviceDetail(applicationContext).deviceNumber();
        if(deviceDetail.deviceCount()>0) {
            StateAdapter adapter = new StateAdapter(getActivity(), deviceName, deviceNumber, deviceState);
            recycle.setAdapter(adapter);
        }*/



        getDevice(view);

    }

    private void getDevice(View view) {

        params = new HashMap<>();
        params.put("superv_id",sharedpreferences.getString(SPrefManager.SUPERVISOR_ID,"NULL"));

        asyncTask(params,getActivity(),R.id.progressBar);

    }

    private static void asyncTask(HashMap<String, String> params, Activity activity, int bar) {

        class TaskAsync extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;
            private HashMap<String, String> params;
            private Activity activity;
            private int bar;
            private Context context;

            private TaskAsync(HashMap<String, String> params, Activity activity, int bar) {
                this.params = params;
                this.activity = activity;
                this.bar = bar;
                this.context = activity.getApplicationContext();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(bar!=0) {
                    progressBar = activity.findViewById(bar);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.sendPostRequest(UrlManager.GET_ALL_DEVICE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("ooooo", s);
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        if(result.getInt("nos")!=0) {

                            JSONArray dev_id = result.getJSONArray("device_id");
                            String[] device_id = new String[dev_id.length()];
                            for(int i = 0; i < dev_id.length(); i++)
                                device_id[i] = dev_id.getString(i);

                            JSONArray dev_name = result.getJSONArray("device_name");
                            String[] deviceName = new String[dev_name.length()];
                            for(int i = 0; i < dev_name.length(); i++)
                                deviceName[i] = dev_name.getString(i);

                            JSONObject deviceState = result.getJSONObject("device_state");
                            JSONObject deviceAssign = result.getJSONObject("device_assign");

                            ListView recycle = activity.findViewById(R.id.recycle);
                            StateAdapter adapter = new StateAdapter(activity, deviceName, device_id, deviceState,deviceAssign);
                            recycle.setAdapter(adapter);
                        }
                        else Toast.makeText(context, "No Device has been assigned to you", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error in connecting to network \n Try after sometime", Toast.LENGTH_LONG).show();
                }
            }
        }

        TaskAsync registerDevice = new TaskAsync(params,activity,bar);
        registerDevice.execute();
    }

   /* private void setState() {
        String t_name,body;
        BigInteger dateTimeAsID;
        int id,state,count;
        Cursor res = dbHelper.getAllSms();
        count = res.getCount();
        if(count > 0) {
            while (res.moveToNext()) {
                dateTimeAsID = new BigInteger(res.getString(0));
                t_name = res.getString(1);
                body = res.getString(2);
                body = body.trim();
                dbHelper.smsState(dateTimeAsID);
                try{
                    id = Integer.parseInt(body.substring(3,5));
                }catch (Exception e){
                    continue;
                }
                if(body.endsWith("_A")) {
                    state = 0;
                    Boolean addLog = dbHelper.updateLog(dbHelper.getLogId(t_name,id),dateTimeAsID,0);
                }
                else {
                    state = 1;
                    Boolean addLog = dbHelper.insertLog(dateTimeAsID,deviceName[Arrays.asList(deviceNumber).indexOf(new BigInteger(t_name))],new BigInteger(t_name),id);
                }
                Boolean insert = dbHelper.updateComp(t_name,id,state,dateTimeAsID);


                Log.d("state1", "setState: "+insert);
            }
        }
        res.close();
    }

    private void updateState(){
        if(deviceDetail.deviceCount()>0)
        for(BigInteger device : deviceNumber){
            Cursor res = dbHelper.getDevCompState(String.valueOf(device));
            if(res.getCount()>0){
                Boolean update = dbHelper.updateDev(device,1);
            } else {
                Boolean update = dbHelper.updateDev(device,0);
            }
        }
    }*/
}

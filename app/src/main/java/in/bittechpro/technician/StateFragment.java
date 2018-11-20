package in.bittechpro.technician;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;


public class StateFragment extends Fragment {


    public StateFragment() {
        // Required empty public constructor
    }

    ListView recycle;
    View view;
    DeviceDetail deviceDetail;
    Context applicationContext;
    DBHelper dbHelper;
    String[] deviceName;
    BigInteger[] deviceNumber;
    int[] deviceState;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_state, container, false);
        recycle = view.findViewById(R.id.recycle);

        applicationContext = MainActivity.getContextOfApplication();

        deviceDetail = new DeviceDetail(applicationContext);

        MainActivity.mainTitle.setText("Need For Service");

        MainActivity.getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);

        deviceName = deviceDetail.deviceName();
        deviceNumber = deviceDetail.deviceNumber();
        deviceState = deviceDetail.deviceState();

        dbHelper = new DBHelper(applicationContext);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setState();

        updateState();

        deviceState = new DeviceDetail(applicationContext).deviceState();
        if(deviceDetail.deviceCount()>0) {
            StateAdapter adapter = new StateAdapter(getActivity(), deviceName, deviceNumber, deviceState);
            recycle.setAdapter(adapter);
        }
    }

    private void setState() {
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
                //body = body.substring(0,5);
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
    }
}

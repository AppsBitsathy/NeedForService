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

import java.math.BigInteger;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_state, container, false);
        recycle = view.findViewById(R.id.recycle);

        applicationContext = MainActivity.getContextOfApplication();

        deviceDetail = new DeviceDetail(applicationContext);

        MainActivity.mainTitle.setText("Need For Service");

        MainActivity.getBottomNavigationView().getMenu().findItem(R.id.navigation_home).setChecked(true);

        String[] deviceName = deviceDetail.deviceName();
        BigInteger[] deviceNumber = deviceDetail.deviceNumber();
        int[] deviceState = deviceDetail.deviceState();
        if(deviceDetail.deviceCount()>0) {
            StateAdapter adapter = new StateAdapter(getActivity(), deviceName, deviceNumber, deviceState);
            recycle.setAdapter(adapter);
        }
        dbHelper = new DBHelper(applicationContext);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setState();
    }

    private void setState() {
        String t_name,body;
        int id,state,count;
        Cursor res = dbHelper.getAllSms();
        count = res.getCount();
        if(count > 0) {
            while (res.moveToNext()) {
                t_name = res.getString(1);
                body = res.getString(2);
                //body = body.substring(0,5);
                try{
                    id = Integer.parseInt(body.substring(3,5));
                }catch (Exception e){
                    continue;
                }
                if(body.endsWith("_A"))
                    state = 0;
                else
                    state =1;
                Boolean insert = dbHelper.updateComp(t_name,id,state);
                Log.d("state1", "setState: "+insert);
            }
        }
        res.close();
    }
}

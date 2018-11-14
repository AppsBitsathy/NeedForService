package in.bittechpro.technician;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Objects;

public class DeviceFragment extends Fragment {


    public DeviceFragment() {
        // Required empty public constructor
    }

    Spinner device;
    int i,count;
    DBHelper dbHelper;
    Button add_dev,btn_delete_dev,btn_update_dev;
    EditText dev_name,dev_number;
    String name;
    BigInteger number;
    BigInteger[] num_ary;
    String[] name_ary;
    Boolean selected = false,touched =false;
    FloatingActionButton fab_add;
    TextView txt_add_dev;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device, container, false);

        device = view.findViewById(R.id.spinner_device);
        add_dev = view.findViewById(R.id.add_dev);
        dev_name = view.findViewById(R.id.dev_name);
        dev_number = view.findViewById(R.id.dev_number);
        btn_delete_dev = view.findViewById(R.id.btn_delete_dev);
        btn_update_dev = view.findViewById(R.id.btn_update_dev);
        fab_add =view.findViewById(R.id.fab_add);
        txt_add_dev = view.findViewById(R.id.txt_add_dev);


        dbHelper = new DBHelper(getActivity());

        add_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = dev_name.getText().toString().trim();

                if(!name.equals("") && !dev_number.getText().toString().equals("")) {

                    number =  new BigInteger(dev_number.getText().toString().trim());
                    Boolean res = dbHelper.insertDEV(name, number);

                    if (res) {
                        Toast.makeText(getActivity(), "Device Added Successfully", Toast.LENGTH_SHORT).show();
                        dbHelper.createDevCompTable(number.toString());
                    } else {
                        Toast.makeText(getActivity(), "Device not Added", Toast.LENGTH_SHORT).show();
                    }
                    restartFrag();
                }else {
                    Toast.makeText(getActivity(), "Error - Check Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_update_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = dev_name.getText().toString().trim();
                if(!name.equals("") && !dev_number.getText().toString().equals("")) {

                    number = new BigInteger(dev_number.getText().toString().trim());
                    BigInteger id = num_ary[device.getSelectedItemPosition()];
                    if (dbHelper.updateDev(id,number,name)){
                        Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                        restartFrag();
                    }


                }
            }
        });

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartFrag();
            }
        });

        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(getActivity()).setTitle("Device");

        Cursor res = dbHelper.getAllDev();
        count = res.getCount();
        if(count > 0) {
            num_ary =new BigInteger[count];
            name_ary = new String[count];
            i=0;

            while (res.moveToNext()) {
                num_ary[i] = new BigInteger(res.getString(0));
                name_ary[i] = res.getString(1);
                Log.d("num1",res.getString(0));
                i++;
            }

            ArrayAdapter<String> list = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1,name_ary);
            list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            device.setAdapter(list);
        }else device.setVisibility(View.INVISIBLE);

        device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                BigInteger selectedItem = num_ary[i];
                setDevice(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        device.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!touched || device.getCount()==1) {
                    BigInteger selectedItem = num_ary[0];
                    setDevice(selectedItem);
                    touched=true;
                }
                dev_number.setEnabled(false);
                return false;
            }
        });

        btn_delete_dev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigInteger selectedItem = num_ary[device.getSelectedItemPosition()];
                if(dbHelper.deleteOneDev(selectedItem)) {
                    Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Error - Deleting", Toast.LENGTH_SHORT).show();
                }
                restartFrag();
            }
        });


    }

    private void restartFrag() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        Fragment fragment =new DeviceFragment();
        ft.replace(R.id.content_frame, fragment).commit();
    }

    private void setDevice(BigInteger selectedItem) {

        Cursor res = dbHelper.getOneDev(selectedItem);
        count = res.getCount();
        if(count==1){
            res.moveToNext();
            BigInteger selected_dev_number = new BigInteger(res.getString(0));
            String selected_dev_name = res.getString(1);

            if(selected){
                dev_number.setText((CharSequence) selected_dev_number.toString());
                dev_name.setText(selected_dev_name);
                fab_add.show();
                btn_delete_dev.setVisibility(View.VISIBLE);
                btn_update_dev.setVisibility(View.VISIBLE);
                add_dev.setVisibility(View.INVISIBLE);
                txt_add_dev.setVisibility(View.GONE);
            }

            selected =true;

        }

    }


}

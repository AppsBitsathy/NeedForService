package in.bittechpro.technician;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;


public class EmployeeFragment extends Fragment {


    public EmployeeFragment() {
        // Required empty public constructor
    }

    Context applicationContext;

    int i,count;

    DBHelper dbHelper;

    Button add_emp,btn_delete_emp,btn_update_emp;
    ImageButton btn_contact;
    EditText emp_name,emp_number;

    String name;
    String[] name_ary;

    BigInteger number;
    BigInteger[] num_ary;

    Boolean selected = false,touched =false;

    FloatingActionButton fab_add;

    Spinner device;
    TextView txt_add_emp;

    MainActivity mainActivity;

    String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS,
    };

    Uri result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employee, container, false);

        applicationContext = MainActivity.getContextOfApplication();


        device = view.findViewById(R.id.spinner_employee);
        add_emp = view.findViewById(R.id.add_emp);
        emp_name = view.findViewById(R.id.txt_emp_name);
        emp_number = view.findViewById(R.id.txt_emp_number);
        btn_delete_emp = view.findViewById(R.id.btn_delete_emp);
        btn_update_emp = view.findViewById(R.id.btn_update_emp);
        fab_add =view.findViewById(R.id.fab_add);
        txt_add_emp = view.findViewById(R.id.txt_add_emp);
        btn_contact = view.findViewById(R.id.btn_contact);

        dbHelper = new DBHelper(getActivity());

        add_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name = emp_name.getText().toString().trim();

                if(!name.equals("") && !emp_number.getText().toString().equals("")) {

                    number =  new BigInteger(emp_number.getText().toString().trim());
                    Boolean res = dbHelper.insertEMP(name, number);

                    if (res) {
                        Toast.makeText(getActivity(), "Device Added Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Device not Added", Toast.LENGTH_SHORT).show();
                    }

                    restartFrag();
                }else {
                    Toast.makeText(getActivity(), "Error - Check Details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_update_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = emp_name.getText().toString().trim();
                if(!name.equals("") && !emp_number.getText().toString().equals("")) {

                    number = new BigInteger(emp_number.getText().toString().trim());
                    BigInteger id = num_ary[device.getSelectedItemPosition()];
                    if (dbHelper.updateEMP(id,number,name)){
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
        //you can set the title for your toolbar here for different fragments different titles
        Objects.requireNonNull(getActivity()).setTitle("Employee");

        Cursor res = dbHelper.getAllEMP();
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
                btn_contact.setVisibility(View.INVISIBLE);
                emp_number.setEnabled(false);
                return false;
            }
        });

        btn_delete_emp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BigInteger selectedItem = num_ary[device.getSelectedItemPosition()];
                if(dbHelper.deleteOneEMP(selectedItem)) {
                    Toast.makeText(getActivity(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getActivity(), "Error - Deleting", Toast.LENGTH_SHORT).show();
                }
                restartFrag();
            }
        });

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity = new MainActivity();
                if (mainActivity.checkPermissions(permissions,getActivity(),applicationContext)) {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, 1);
                }
            }
        });


    }

    private void restartFrag() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        Fragment fragment =new EmployeeFragment();
        ft.replace(R.id.content_frame, fragment).commit();
    }

    private void setDevice(BigInteger selectedItem) {

        Cursor res = dbHelper.getOneEMP(selectedItem);
        count = res.getCount();
        if(count==1){
            res.moveToNext();
            BigInteger selected_dev_number = new BigInteger(res.getString(0));
            String selected_dev_name = res.getString(1);
            if(selected){
                emp_number.setText((CharSequence) selected_dev_number.toString());
                emp_name.setText(selected_dev_name);
                fab_add.show();
                btn_delete_emp.setVisibility(View.VISIBLE);
                btn_update_emp.setVisibility(View.VISIBLE);
                add_emp.setVisibility(View.INVISIBLE);
                txt_add_emp.setText("Edit Employee");
            }

            selected =true;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if(resultCode == RESULT_OK) {
                result = data.getData();
                String id = Objects.requireNonNull(result).getLastPathSegment();

                Cursor c = applicationContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[]{id}, null);

                if (c != null && c.getCount() == 1) {
                    if (c.moveToFirst()) {
                        int phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int nameIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        String phone = c.getString(phoneIdx).replace(" ", "");//.replace("+91","");
                        if (phone.length() > 10) {
                            phone = phone.substring(phone.length() - 10);
                        }
                        String name = c.getString(nameIdx);
                        //Log.v(TAG, "Got phone number1: " + phone + "//" + name);
                        emp_name.setText(name);
                        emp_number.setText(phone);
                    } else {
                        Log.w(TAG, "No results");
                    }
                    c.close();
                }

            }

        }

    }


}



package in.bittechpro.technician;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.Objects;

public class AssignFragment extends Fragment {

    public AssignFragment() {
        // Required empty public constructor
    }

    View view;
    Spinner spinner_dev_emp;
    TextView emp_selected,txt_emp_selected_old;
    Button assign;

    DBHelper dbHelper;

    String[] dev_name_ary,emp_name_ary;
    BigInteger[] dev_num_ary,emp_num_ary;
    boolean[] checked_emp;

    int dev_count,emp_count;

    StringBuilder item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_assign, container, false);

        spinner_dev_emp =view.findViewById(R.id.spinner_dev_emp);
        emp_selected = view.findViewById(R.id.txt_emp_selected);
        txt_emp_selected_old = view.findViewById(R.id.txt_emp_selected_old);
        assign= view.findViewById(R.id.btn_assign);
        dbHelper = new DBHelper(getActivity());

        return view;
            }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(getActivity()).setTitle("Device - Employee");

        getDevice();
        
        getEmp();
        
        emp_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listEmp();
            }
        });

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignEmp();
            }
        });

        spinner_dev_emp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                loadOldEmp(dev_num_ary[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getEmp() {
        Cursor res = dbHelper.getAllEMP();
        emp_count = res.getCount();
        if(emp_count > 0) {
            emp_num_ary =new BigInteger[emp_count];
            emp_name_ary = new String[emp_count];
            int i=0;

            while (res.moveToNext()) {
                emp_num_ary[i] = new BigInteger(res.getString(0));
                emp_name_ary[i] = res.getString(1);
                i++;
            }
        } else {
            Toast.makeText(getActivity(), "Please add an Employee !", Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            Fragment fragment =new EmployeeFragment();
            ft.replace(R.id.content_frame, fragment).commit();
        }
    }

    private void getDevice() {

        Cursor res = dbHelper.getAllDev();
        dev_count = res.getCount();
        if(dev_count > 0) {
            dev_num_ary =new BigInteger[dev_count];
            dev_name_ary = new String[dev_count];
            int i=0;

            while (res.moveToNext()) {
                dev_num_ary[i] = new BigInteger(res.getString(0));
                dev_name_ary[i] = res.getString(1);
                i++;
            }

            ArrayAdapter<String> list = new ArrayAdapter<String>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1,dev_name_ary);
            list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_dev_emp.setAdapter(list);
        } else
            spinner_dev_emp.setVisibility(View.INVISIBLE);

    }

    private void listEmp() {

        checked_emp = new boolean[emp_count];
        
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Select Employee");
        mBuilder.setMultiChoiceItems(emp_name_ary, checked_emp, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                checked_emp[position]=isChecked;

            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                item = new StringBuilder();
                for (int i = 0; i < emp_count; i++) {
                    if(checked_emp[i]){
                        item.append(emp_name_ary[i]).append(" - ").append(emp_num_ary[i]).append("\n");
                    }
                }
                emp_selected.setText(item.toString().trim());
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void assignEmp() {
        if (dbHelper.updateDev_emp(dev_num_ary[spinner_dev_emp.getSelectedItemPosition()],item.toString().trim())){
            Toast.makeText(getActivity(), "Updated Succeccfully", Toast.LENGTH_SHORT).show();
            restartFrag();
        }
    }


    private void loadOldEmp(BigInteger id) {
        Cursor res = dbHelper.getOneDev(id);
        int count = res.getCount();
        if(count==1){
            res.moveToNext();
            String old_dev_emp = res.getString(2);
            txt_emp_selected_old.setText(old_dev_emp.equals("")?"Employee not Assigned":old_dev_emp);
        }
    }

    private void restartFrag() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        Fragment fragment =new AssignFragment();
        ft.replace(R.id.content_frame, fragment).commit();
    }
}

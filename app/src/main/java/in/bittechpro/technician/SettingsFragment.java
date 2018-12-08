package in.bittechpro.technician;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.opencsv.CSVWriter;


public class SettingsFragment extends Fragment {



    public SettingsFragment() {
        // Required empty public constructor
    }

    Button btn_export;
    Context context = MainActivity.getContextOfApplication();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity.mainTitle.setText("Need For Service");
        btn_export = view.findViewById(R.id.btn_export);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportDB();
            }
        });
    }

    private void exportDB() {
        int i=0;
        DBHelper dbhelper = new DBHelper(context);
        File exportDir = new File(Environment.getExternalStorageDirectory(), "/NFS/");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "nfs_log.csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));

            Cursor curCSV = dbhelper.exportLog();
            csvWrite.writeNext(curCSV.getColumnNames());
            i = curCSV.getCount();
            if (i>0){
                CompList compList = new CompList();
                while(curCSV.moveToNext())
                {
                    String arrStr[] ={
                            dateConv(curCSV.getString(0)),
                            curCSV.getString(1),
                            curCSV.getString(2),
                            compList.getComp(Integer.parseInt(curCSV.getString(3))),
                            curCSV.getString(4),
                            curCSV.getString(5),
                            dateConv(curCSV.getString(6)),
                            dateConv(curCSV.getString(7)),
                            switchState(curCSV.getString(8))
                    };


                    csvWrite.writeNext(arrStr);
                }

                Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(context, "No log found", Toast.LENGTH_SHORT).show();

            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.d("MainActivity", sqlEx.getMessage(), sqlEx);
        }
        try {
            if (i>0) {
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/NFS/nfs_log.csv");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "text/csv");
                startActivity(intent);
            }
        }catch(Exception e){
            Toast.makeText(context, "No suitable application found to view Log", Toast.LENGTH_SHORT).show();
        }
    }

    private String switchState(String string) {
        switch (Integer.parseInt(string)) {
            case 0:
                return "Completed";
            case 1:
                return "Assigned / In Progress";
            case 2:
                return "Not Assigned";
            default:
                return "Status Unknown";
        }
    }

    private String dateConv(String string) {
        if (string!=null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss", Locale.getDefault());
            Long milli = Long.valueOf(string);
            return formatter.format(new Date(milli));
        }
        return null;
    }


}

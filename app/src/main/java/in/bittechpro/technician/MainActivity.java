package in.bittechpro.technician;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {


    String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String[] number,body;
    BigInteger[] date,num_ary;
    Fragment fragment = null;
    String fragTag ;

    ArrayList<String> arrayList;

    public static Context contextOfApplication;
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    DBHelper dbHelper;
    public static TextView mainTitle;

    public static BottomNavigationView bottomNavigationView;
    public static BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    };
    FloatingActionButton restart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contextOfApplication = getApplicationContext();

        checkPermissions(permissions,this,this);

        bottomNavigationView = findViewById(R.id.b_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                fragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new StateFragment();
                        fragTag = "state";
                        break;
                    /*case R.id.navigation_device:
                        fragment = new DeviceFragment();
                        fragTag = "device";
                        break;*/
                    case R.id.navigation_employee:
                        fragment = new EmployeeFragment();
                        fragTag = "employee";
                        break;
                    case R.id.navigation_backup:
                        fragment = new SettingsFragment();
                        fragTag = "setting";
                        break;

                }
                if (fragment != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                    ft.replace(R.id.content_frame, fragment,fragTag);
                    ft.commit();
                    return true;
                } else {
                    Toast.makeText(MainActivity.this, "There is no way here", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });

        mainTitle = findViewById(R.id.title_toolbar);

        dbHelper = new DBHelper(this);

        //addSms();

        if (fragment == null) {
            fragment = new StateFragment();
            fragTag="state";
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment,fragTag);
            ft.commit();
        }

        restart =   findViewById(R.id.fab_restart);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = contextOfApplication.getPackageManager()
                        .getLaunchIntentForPackage( contextOfApplication.getPackageName() );
                Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
    }

    private void addSms() {

        int count,i=0;
        Cursor res = dbHelper.getAllDev();
        count = res.getCount();
        if(count > 0) {
            num_ary =new BigInteger[count];
            while (res.moveToNext()) {
                num_ary[i] = new BigInteger(res.getString(0));
                i++;
            }


            Uri inboxURI = Uri.parse("content://sms/inbox");
            for(BigInteger num : num_ary) {
                i = 0;
                arrayList = new ArrayList<>();

                Cursor c = getContentResolver().query(inboxURI, null, "address='+91"+num+"'", null, null);
                if(Objects.requireNonNull(c).getCount()>0) {
                    count =c.getCount();
                    number = new String[count];
                    body = new String[count];
                    date = new BigInteger[count];
                    while (c.moveToNext()) {
                        number[i] = c.getString(c.getColumnIndexOrThrow("address")).replace(" ","");
                        if (number[i].length() > 10) {
                            number[i] = number[i].substring(number[i].length() - 10);
                        }
                        body[i] = c.getString(c.getColumnIndexOrThrow("body"));
                        date[i] = BigInteger.valueOf(c.getLong(c.getColumnIndexOrThrow("date")));
                        Boolean insert = dbHelper.insertSMS(number[i], body[i], date[i]);
                        i++;
                    }
                }
                c.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
       if(fragTag.equals("state")) {
           AlertDialog.Builder builder = new AlertDialog.Builder(this);
           builder.setMessage("Do you want to exit");
           builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   MainActivity.super.onBackPressed();
               }
           }).setNegativeButton("No", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

               }
           });
           AlertDialog dialog = builder.create();
           dialog.show();

       } else {
           fragment = new StateFragment();
           fragTag = "state";
           FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
           ft.replace(R.id.content_frame, fragment,fragTag);
           ft.commit();
       }
    }

    /*@Override
    protected void onResume() {

        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        super.onResume();

    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if ((grantResults.length > 0)
                    && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

        }
    }
    public boolean checkPermissions(String[] permissions, Activity activity, Context context) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions( activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
}

package in.bittechpro.technician;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String[] permissions = new String[]{
            Manifest.permission.READ_SMS,
    };
    String[] number,body;
    BigInteger[] date,num_ary;
    Fragment fragment = null;

    ArrayList<String> arrayList;

    public static Context contextOfApplication;
    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    DBHelper dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contextOfApplication = getApplicationContext();

        checkPermissions(permissions,this,this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        dbHelper = new DBHelper(this);

        addSms();



        if (fragment == null) {
            fragment = new HomeNewFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    private void addSms() {

        int count=0,i=0,state=0;
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
                count =0; i = 0;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        fragment = null;
        switch (itemId) {
            case R.id.nav_employee:
                fragment = new EmployeeFragment();
                break;
            case R.id.nav_home:
                fragment = new HomeNewFragment();
                break;
            case R.id.nav_device:
                fragment = new DeviceFragment();
                break;
            case R.id.nav_dev_emp:
                fragment = new AssignFragment();
                break;
        }
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        } else
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

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

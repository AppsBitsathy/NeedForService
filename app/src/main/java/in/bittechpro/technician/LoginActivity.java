package in.bittechpro.technician;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    static FloatingActionButton btn_register;
    EditText sup_num,sup_name;
    HashMap<String,String> params;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }

        btn_register = findViewById(R.id.submit_register);
        sup_num = findViewById(R.id.sup_num);
        sup_name = findViewById(R.id.sup_name);
        progressBar = findViewById(R.id.progressBarRegister);

        SharedPreferences sharedpreferences = getSharedPreferences(SPrefManager.PREF_NAME, Context.MODE_PRIVATE);

        if (sharedpreferences.contains(SPrefManager.LOGGED)){
            if (sharedpreferences.getInt(SPrefManager.LOGGED, 0) == 1) {
                finish();
                startActivity(new Intent(this,MainActivity.class));
            }
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = sup_name.getText().toString().trim();
                String num = sup_num.getText().toString().trim();

                if (num.length()==10 && !name.isEmpty()){
                    params = new HashMap<>();
                    params.put("sup_name",name);
                    params.put("sup_id",num);
                    asyncTask(params,LoginActivity.this,progressBar.getId());
                }
                else Toast.makeText(LoginActivity.this, "Check again", Toast.LENGTH_SHORT).show();
            }
        });
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
                return requestHandler.sendPostRequest(UrlManager.SET_DEVICE, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                progressBar.setVisibility(View.GONE);
                btn_register.show();
                try {
                    JSONObject result = new JSONObject(s);
                    if(result.getInt("status")==0){
                        SharedPreferences.Editor editor = activity.getSharedPreferences(SPrefManager.PREF_NAME, MODE_PRIVATE).edit();
                        editor.putInt(SPrefManager.LOGGED, 1);
                        editor.putString(SPrefManager.SUPERVISOR_ID,params.get("sup_id"));
                        editor.putString(SPrefManager.SUPERVISOR_NAME,params.get("sup_name"));
                        editor.apply();
                        activity.finish();
                        activity.startActivity(new Intent(context,MainActivity.class));
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


}

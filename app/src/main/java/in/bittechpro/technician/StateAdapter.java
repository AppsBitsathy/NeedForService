package in.bittechpro.technician;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class StateAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] dev_name;
    private final String[] dev_num;
    private final JSONObject dev_state;
    private final JSONObject dev_assign;
    private DeviceFunction devComp;
    private EmployeeDetail employeeDetail;
    DBHelper dbHelper;
    CompList compList;
    GetImg getImg;


    StateAdapter(Activity context, String[] deviceName, String[] deviceNumber, JSONObject deviceState,JSONObject deviceAssign) {
        super(context, R.layout.adapter_state, deviceName);
        this.context=context;
        this.dev_name = deviceName;
        this.dev_num = deviceNumber;
        this.dev_state = deviceState;
        this.dev_assign = deviceAssign;
        employeeDetail = new EmployeeDetail(context);
        dbHelper = new DBHelper(context);
    }

    @NonNull
    public View getView(final int position, View view, @NonNull final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") final View rowView = inflater.inflate(R.layout.adapter_state, null, true);

        final ArcLayout arcLayout = rowView.findViewById(R.id.arc_layout);
        final ImageButton device_icon = rowView.findViewById(R.id.butt);
        TextView device_name_text = rowView.findViewById(R.id.txt_device_name);
        device_name_text.setText(dev_name[position]);
        final boolean[] animated = {false};

        getImg = new GetImg();

        final Animation animFade = AnimationUtils.loadAnimation(getContext(),R.anim.fade );
        animFade.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                arcLayout.clearAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        final Animation animFadeIn = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in );
        animFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                arcLayout.clearAnimation();
                arcLayout.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        //int n = new Random().nextInt(100);
        /*if( dev_state[position]==1){ //if(n%2 == 0) { //
            device_icon.setImageResource(R.drawable.icon_red);
        }*/
        device_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!animated[0]) {
                    arcLayout.setVisibility(View.VISIBLE);
                    arcLayout.startAnimation(animFade);
                    animated[0] = true;
                }else {
                    arcLayout.startAnimation(animFadeIn);
                    animated[0] = false;
                }
            }
        });

        final BigInteger[] emp_number = employeeDetail.getEmp_number();
        final String[] emp_name = employeeDetail.getEmp_name();
        final int emp_count = employeeDetail.getCount();

        Iterator<String> temp = dev_state.keys();
        int count = 0;
        final ArrayList<Integer> deviceCompState = new ArrayList<>();
        while (temp.hasNext()) {
            String key = temp.next();
            if (key.contains(dev_num[position])) {
                try {
                    deviceCompState.add(dev_state.getInt(key));
                    if (dev_state.getInt(key)==1)count++;
                } catch (JSONException e) {
                    e.printStackTrace();
                    deviceCompState.add(1);
                }
            }
        }

        if( count>0){ //if(n%2 == 0) { //
            device_icon.setImageResource(R.drawable.icon_red);
        }

        compList = new CompList();

        for (int i =0 ; i < deviceCompState.size() ; i++) {
            final ImageButton fab = new ImageButton(context);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 33, context.getResources().getDisplayMetrics());
            fab.setLayoutParams(new ArcLayout.LayoutParams(height,height));
            fab.setId(i+1);
            fab.setImageResource(getImg.getImg(compList.getComp(i+1)));
            fab.setAdjustViewBounds(true);
            if(deviceCompState.get(i)==0){
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3fb547")));
            }else{
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f65024")));//#f65024 - red // #3fb547 - green
            }
            fab.setElevation(5);
            fab.setBackgroundResource(R.drawable.round_button);
            fab.setScaleType(ImageView.ScaleType.CENTER);
            //fab.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            final int finalI = i;
            fab.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, dev_name[position]+ finalI, Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    if(emp_count>0){
                        LayoutInflater inflater = LayoutInflater.from(context);
                        @SuppressLint("ViewHolder") final View alertView = inflater.inflate(R.layout.fragment_assign, null);
                        builder.setView(alertView);
                        TextView txt_assigned = alertView.findViewById(R.id.txt_assigned);
                        final Spinner spinner_emp = alertView.findViewById(R.id.spinner_dev_emp);
                        ArrayAdapter<String> list = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,emp_name);
                        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_emp.setAdapter(list);
                        if(deviceCompState.get(finalI)==0){
                            spinner_emp.setEnabled(false);
                            txt_assigned.setText("State : No Problem");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });

                        }else{
                            String s = "Not Assigned";
                            try {
                                s = dev_assign.getString(dev_num[position]+"."+(finalI+1));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(s.equals("0"))s = "Yet to be assigned";
                            else {
                                int q = Arrays.asList(emp_number).indexOf(new BigInteger(s));
                                s = q!=-1?emp_name[Arrays.asList(emp_number).indexOf(new BigInteger(s))]:s;
                            }
                            txt_assigned.setText("Employee : "+s);
                            builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //Boolean updateLog = dbHelper.updateLog(deviceCompLogId[finalI],emp_name[spinner_emp.getSelectedItemPosition()],emp_number[spinner_emp.getSelectedItemPosition()]);
                                    //SmsManager smsManager = SmsManager.getDefault();
                                    //smsManager.sendTextMessage(String.valueOf(emp_number[spinner_emp.getSelectedItemPosition()]), null,"NFS Alert\nDevice : "+dev_name[position]+"\nComplaint : "+(compList.getComp(finalI+1)), null, null);
                                    String emp = "Assigned to "+emp_name[spinner_emp.getSelectedItemPosition()]+" : "+String.valueOf(emp_number[spinner_emp.getSelectedItemPosition()]);
                                    //dbHelper.updateCompAssigned(String.valueOf(dev_num[position]),finalI+1,emp);
                                    HashMap<String,String> params = new HashMap<>();
                                    params.put("device_sno",dev_num[position]+"."+(finalI+1));
                                    params.put("emp",String.valueOf(emp_number[spinner_emp.getSelectedItemPosition()]));


                                    class TaskAsync extends AsyncTask<Void, Void, String> {

                                        private ProgressBar progressBar;
                                        private HashMap<String, String> params;
                                        private Activity activity;
                                        private int bar;
                                        private Context context;
                                        private BigInteger[] emp_number = employeeDetail.getEmp_number();


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
                                            return requestHandler.sendPostRequest(UrlManager.ASSIGN_EMP, params);
                                        }

                                        @Override
                                        protected void onPostExecute(String s) {
                                            super.onPostExecute(s);

                                            Log.d("ooooo",s);
                                            if (bar!=0)
                                            progressBar.setVisibility(View.GONE);

                                            try {
                                                JSONObject result = new JSONObject(s);
                                                if(result.getInt("status")==0){

                                                    SmsManager smsManager = SmsManager.getDefault();
                                                    smsManager.sendTextMessage(String.valueOf(emp_number[spinner_emp.getSelectedItemPosition()]), null,"NFS Alert\nDevice : "+dev_name[position]+"\nComplaint : "+(compList.getComp(finalI+1)), null, null);

                                                    Toast.makeText(context, "Assigned Successfully", Toast.LENGTH_SHORT).show();

                                                    activity.finish();
                                                    activity.startActivity(new Intent(context,MainActivity.class));
                                                }
                                                else Toast.makeText(context, "Error : Employee not assigned, try again", Toast.LENGTH_SHORT).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(context, "Error in connecting to network \n Try after sometime", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    TaskAsync registerDevice = new TaskAsync(params,context,0);
                                    registerDevice.execute();





                                    Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show();

                                    /*Intent intent = context.getPackageManager()
                                            .getLaunchIntentForPackage( context.getPackageName() );
                                    Objects.requireNonNull(intent).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intent);*/
                                }
                            });
                        }


                        builder.setTitle("Assign Employee");
                        builder.setMessage(dev_name[position] + " : " +compList.getComp(finalI+1));

                    }else {
                        builder.setTitle("Alert");
                        builder.setMessage("No employee found").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
//                        Toast.makeText(context, "Please add an Employee to assign", Toast.LENGTH_SHORT).show();
                    }



                    final AlertDialog dialog = builder.create();
                    dialog.setCancelable(true);
                    dialog.show();



                }
            });
            arcLayout.addView(fab);
        }

        return rowView;
    }

     void asyncTask(HashMap<String, String> params, Activity activity, int bar) {


    }
}

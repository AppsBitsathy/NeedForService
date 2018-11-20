package in.bittechpro.technician;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


import com.ogaclejapan.arclayout.ArcLayout;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

public class StateAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] dev_name;
    private final BigInteger[] dev_num;
    private final int[] dev_state;
    private DeviceFunction devComp;
    private EmployeeDetail employeeDetail;


    StateAdapter(Activity context, String[] deviceName, BigInteger[] deviceNumber, int[] deviceState) {
        super(context, R.layout.adapter_state, deviceName);
        this.context=context;
        this.dev_name = deviceName;
        this.dev_num = deviceNumber;
        this.dev_state = deviceState;
        employeeDetail = new EmployeeDetail(context);;
    }

    @NonNull
    public View getView(final int position, View view, @NonNull final ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.adapter_state, null, true);

        final ArcLayout arcLayout = rowView.findViewById(R.id.arc_layout);
        final ImageButton device_icon = rowView.findViewById(R.id.butt);
        TextView device_name_text = rowView.findViewById(R.id.txt_device_name);
        device_name_text.setText(dev_name[position]);
        final boolean[] animated = {false};

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

        int n = new Random().nextInt(100);
        if(n%2 == 0) { //if( dev_state[position]==1){
            device_icon.setImageResource(R.drawable.icon_red);
        }
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

        devComp = new DeviceFunction(context,dev_num[position]);
        int[] deviceCompId = devComp.deviceCompId();
        int[] deviceCompState = devComp.deviceCompState();
        int deviceCompCount = devComp.deviceCompCount();

        BigInteger[] emp_number = employeeDetail.getEmp_number();
        final String[] emp_name = employeeDetail.getEmp_name();
        final int emp_count = employeeDetail.getCount();




        for (int i = 0; i < deviceCompCount ; i++) {
            ImageButton fab = new ImageButton(context);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, context.getResources().getDisplayMetrics());
            fab.setLayoutParams(new ArcLayout.LayoutParams(height,height));
            fab.setImageResource(R.drawable.test);
            fab.setId(i+1);
            fab.setAdjustViewBounds(true);
            if(deviceCompState[i]==0){
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#3fb547")));
            }else{
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f65024")));//#f65024 - red // #3fb547 - green
            }
            fab.setElevation(5);
            fab.setBackgroundResource(R.drawable.round_button);
            fab.setScaleType(ImageView.ScaleType.CENTER);
            fab.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            final int finalI = i;
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, dev_name[position]+ finalI, Toast.LENGTH_SHORT).show();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    @SuppressLint("ViewHolder") View alertView = inflater.inflate(R.layout.fragment_assign, null);
                    builder.setView(alertView);

                    builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    if(emp_count>0){
                        final Spinner spinner_emp = alertView.findViewById(R.id.spinner_dev_emp);
                        ArrayAdapter<String> list = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,emp_name);
                        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner_emp.setAdapter(list);
                    }


                    final AlertDialog dialog = builder.create();
                    dialog.show();



                }
            });
            arcLayout.addView(fab);
        }

        return rowView;
    }
}

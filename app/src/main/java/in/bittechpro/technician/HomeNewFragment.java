package in.bittechpro.technician;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;

import java.math.BigInteger;
import java.util.Objects;

public class HomeNewFragment extends Fragment implements Animation.AnimationListener {


    public HomeNewFragment() {
        // Required empty public constructor
    }

    View view;
    GridView gridView;
    String[] name,roll,id;
    int[] state;

    DBHelper dbHelper;
    FloatingActionButton fab_restart;
    Button butt,butq;
    ArcLayout arcLayout;
    Animation animFade;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_new, container, false);
        gridView =view.findViewById(R.id.dev_grid_view);
        fab_restart = view.findViewById(R.id.fab_restart);
        arcLayout = view.findViewById(R.id.arc_layout);
        butt = view.findViewById(R.id.butt);
        butq = view.findViewById(R.id.butq);
        dbHelper = new DBHelper(getActivity());
        Objects.requireNonNull(getActivity()).setTitle("HOME");
        animFade = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade );
        animFade.setAnimationListener(this);

        fab_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = MainActivity.contextOfApplication.getPackageManager()
                        .getLaunchIntentForPackage( MainActivity.contextOfApplication.getPackageName() );
                Objects.requireNonNull(i).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    arcLayout.setVisibility(View.VISIBLE);
                    arcLayout.startAnimation(animFade);


            }
        });
        butq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(), "sdfghsg", Toast.LENGTH_SHORT).show();


            }
        });




        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //setState();


        //updateHome();





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
                if(body.endsWith("_A")){
                    state = 0;
                }else
                    state =1;
                Boolean insert = dbHelper.updateComp(t_name,id,state);
                Log.d("state1", "setState: "+insert);
            }
        }
        res.close();
    }

    private void updateHome() {
        Cursor res = dbHelper.getAllDev();
        int count = res.getCount();
        if(count > 0) {
            name =new String[count];
            roll = new String[count];
            state = new int[count];
            id =new String[count];
            int i=0;

            while (res.moveToNext()) {
                id[i]=res.getString(0);
                name[i] = res.getString(1);
                roll[i] = res.getString(2);


                Cursor res1 = dbHelper.getOneDevState(id[i]);
                if(res1.getCount()>0){
                    state[i] = 1;
                }else{
                    state[i] = 0;
                }
                res1.close();

                Boolean update = dbHelper.updateDev(new BigInteger(id[i]),state[i]);

                Log.d("num1",res.getString(0)+update);
                i++;
            }

            HomeNewGridAdapter adapter = new HomeNewGridAdapter(getActivity(),name,roll,state);
            gridView.setAdapter(adapter);
        }
        res.close();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

        arcLayout.clearAnimation();

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

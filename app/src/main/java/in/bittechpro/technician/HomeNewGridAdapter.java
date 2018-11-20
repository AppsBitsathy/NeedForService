package in.bittechpro.technician;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ogaclejapan.arclayout.ArcLayout;

public class HomeNewGridAdapter extends ArrayAdapter<String> implements Animation.AnimationListener {

    private final Activity context;
    private final String[] dev_name;
    private final String[] roll;
    private final int[] state;
    ImageButton butt;
    private ArcLayout arcLayout;
    private Animation animFade;


    HomeNewGridAdapter(Activity context, String[] dev_name, String[] roll, int[] state) {
        super(context, R.layout.home_list_adapter, dev_name);

        this.context=context;
        this.dev_name=dev_name;
        this.roll=roll;
        this.state = state;


    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.home_new_grid_adapter, null,true);

        TextView txt_dev = rowView.findViewById(R.id.grid_dev_name);
        TextView txt_roll = rowView.findViewById(R.id.grid_dev_emp_det);
        //CardView cardView =rowView.findViewById(R.id.grid_card);

        arcLayout = rowView.findViewById(R.id.arc_layout);
        butt = rowView.findViewById(R.id.butt);

        animFade = AnimationUtils.loadAnimation(getContext(),
                R.anim.fade );
        animFade.setAnimationListener(this);

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                arcLayout.setVisibility(View.VISIBLE);
                arcLayout.startAnimation(animFade);


            }
        });

        txt_dev.setText(dev_name[position]);
        txt_roll.setText(roll[position]);
        /*if(state[position]==1){
            cardView.setCardBackgroundColor(Color.parseColor("#EF5350"));
        } else {
            cardView.setCardBackgroundColor(Color.parseColor("#689F38"));
        }*/

        return rowView;

    };

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
package in.bittechpro.technician;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] number;
    private final String[] body;
    private final BigInteger[] date;

    HomeListAdapter(Activity context, String[] number, String[] body, BigInteger[] date) {
        super(context, R.layout.home_list_adapter, number);

        this.context=context;
        this.number=number;
        this.body=body;
        this.date=date;

    }

    @NonNull
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        @SuppressLint("ViewHolder") View rowView=inflater.inflate(R.layout.home_list_adapter, null,true);

        TextView txt_number = rowView.findViewById(R.id.txt_phone);
        TextView txt_body = rowView.findViewById(R.id.txt_body);
        TextView txt_date = rowView.findViewById(R.id.txt_date);
        TextView txt_int = rowView.findViewById(R.id.txt_int);

        txt_number.setText(number[position]);
        txt_body.setText(body[position]);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy hh:mm:ss");
        Long milli = Long.valueOf(String.valueOf(date[position]));
        String dateString = formatter.format(new Date(milli));

        txt_date.setText(dateString);
        txt_int.setText(String.valueOf(position+1));

        return rowView;

    };
}
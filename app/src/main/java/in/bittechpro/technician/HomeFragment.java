package in.bittechpro.technician;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }


    ListView list;
    ArrayList<String> arlist;

    String[] number;
    String[] body;
    String[] name;
    BigInteger[] date;

    DBHelper dbHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        list = view.findViewById(R.id.list);
        arlist = new ArrayList<>();

        //getSms();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /*private void getSms() {

        Uri inboxURI = Uri.parse("content://sms/inbox");
        arlist = new ArrayList<>();
        ContentResolver cr = Objects.requireNonNull(getContext()).getContentResolver();
        Cursor c = cr.query(inboxURI,null,"address='6505551212'",null,null);
        int count = Objects.requireNonNull(c).getCount();
        number =new String[count];
        body = new String[count];
        name = new String[count];
        date = new BigInteger[count];
        int i = 0 ;

        dbHelper = new DBHelper(getActivity());

        while (Objects.requireNonNull(c).moveToNext()){
            number[i] = c.getString(c.getColumnIndexOrThrow("address"));
            body[i] = c.getString(c.getColumnIndexOrThrow("body"));
            date[i] = BigInteger.valueOf(c.getLong(c.getColumnIndexOrThrow("date")));
            name[i] ="name"+i;
            Boolean insert = dbHelper.insertSMS(name[i],number[i],body[i],date[i]);
            //Log.d("insert",i +insert.toString());
            i++;
        }

        c.close();

        Cursor res = dbHelper.getAllSms();
        count = res.getCount();
        if(count > 0) {
            number =new String[count];
            body = new String[count];
            date = new BigInteger[count];
            i=0;

            while (res.moveToNext()) {
                date[i] = new BigInteger(res.getString(0));
                name[i] = res.getString(1);
                Log.d("name1",res.getString(1));
                number[i] = res.getString(2);
                body[i] =res.getString(3);
                i++;
            }

            HomeListAdapter adapter = new HomeListAdapter(getActivity(),name,body,date);
            list.setAdapter(adapter);


        }
    }*/
}

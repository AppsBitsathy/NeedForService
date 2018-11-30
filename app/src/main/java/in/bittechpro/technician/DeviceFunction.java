package in.bittechpro.technician;

import android.content.Context;
import android.database.Cursor;

import java.math.BigInteger;

class DeviceFunction {

    private int count;

    private int[] state,id;

    private  BigInteger[] log_id;

    private String[] assigned;

    DeviceFunction(Context context, BigInteger number) {
        DBHelper dbHelper = new DBHelper(context);
        Cursor res = dbHelper.getCompTable(number.toString());
        this.count = res.getCount();
        if (count > 0) {

            this.id = new int[count];
            this.state = new int[count];
            this.log_id = new BigInteger[count];
            this.assigned = new String[count];
            int i = 0;

            while (res.moveToNext()) {
                this.id[i] = res.getInt(0);
                this.state[i] = res.getInt(1);
                this.log_id[i]= new BigInteger(res.getString(2));
                this.assigned[i] =  res.getString(3);
                i++;
            }
        }
    }


    int deviceCompCount(){
        return count;
    }
    int[] deviceCompState(){
        return state;
    }
    int[] deviceCompId(){
        return id;
    }
    BigInteger[] deviceCompLogId(){
        return log_id;
    }
    String[] deviceCompAssigned(){
        return assigned;
    }

}

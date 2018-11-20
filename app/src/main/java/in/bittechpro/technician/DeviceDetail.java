package in.bittechpro.technician;

import android.content.Context;
import android.database.Cursor;

import java.math.BigInteger;

public class DeviceDetail {
    private BigInteger[] device_number;
    private int count;
    private String[] device_name;
    private int[] state;

    public DeviceDetail(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        Cursor res = dbHelper.getAllDev();
        this.count = res.getCount();
        if (count > 0) {
            this.device_number = new BigInteger[count];
            this.device_name = new String[count];
            this.state = new int[count];
            int i = 0;

            while (res.moveToNext()) {
                this.device_number[i] = new BigInteger(res.getString(0));
                this.device_name[i] = res.getString(1);
                this.state[i] = res.getInt(2);
                i++;
            }
        }
    }

    String[] deviceName(){
        return device_name;
    }
    int deviceCount(){
        return count;
    }
    BigInteger[] deviceNumber(){
        return device_number;
    }
    int[] deviceState(){
        return state;
    }

}

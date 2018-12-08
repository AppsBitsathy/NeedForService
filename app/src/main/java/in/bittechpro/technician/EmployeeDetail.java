package in.bittechpro.technician;

import android.content.Context;
import android.database.Cursor;

import java.math.BigInteger;

public class EmployeeDetail {
    private BigInteger[] emp_number;
    private int count;
    private String[] emp_name;

    public EmployeeDetail(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        Cursor res = dbHelper.getAllEMP();
        this.count = res.getCount();
        if (count > 0) {
            this.emp_number = new BigInteger[count];
            this.emp_name = new String[count];
            int i = 0;

            while (res.moveToNext()) {
                this.emp_number[i] = new BigInteger(res.getString(0));
                this.emp_name[i] = res.getString(1);
                i++;
            }
        }
    }

    String[] getEmp_name(){
        return emp_name;
    }
    int getCount(){
        return count;
    }
    BigInteger[] getEmp_number(){
        return emp_number;
    }


}

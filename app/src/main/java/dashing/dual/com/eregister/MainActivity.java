package dashing.dual.com.eregister;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener{

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);


        mydb = new DBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        Spinner pspinner = (Spinner) findViewById(R.id.purposeSpinner);
        pspinner.setOnItemSelectedListener(this);
        Spinner outTypespinner = (Spinner) findViewById(R.id.outTypeSpinner);
        outTypespinner.setOnItemSelectedListener(this);
        Spinner outspinner = (Spinner) findViewById(R.id.outSpinner);
        outspinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
//        Log.d(this.getClass().getSimpleName(), "selected : position " + position + " and id : " + id + " " + parentView.getId() + " " + R.id.spinner);
        Spinner purposeSpinner = (Spinner) findViewById(R.id.purposeSpinner);
        Spinner outTypespinner = (Spinner) findViewById(R.id.outTypeSpinner);
        Spinner outspinner = (Spinner) findViewById(R.id.outSpinner);
        LinearLayout ll = (LinearLayout)findViewById(R.id.formLayout);
        LinearLayout oll = (LinearLayout)findViewById(R.id.o_formLayout);
        LinearLayout outll = (LinearLayout)findViewById(R.id.out_formLayout);

        if(parentView.getId()==R.id.spinner) {
            if (position == 0) {
                purposeSpinner.setVisibility(View.VISIBLE);
                if(purposeSpinner.getSelectedItemPosition()==0){
                    oll.setVisibility(View.VISIBLE);
                    ll.setVisibility(View.GONE);
                }else{
                    ll.setVisibility(View.VISIBLE);
                    oll.setVisibility(View.GONE);
                }
                outll.setVisibility(View.GONE);
            }else {
                outll.setVisibility(View.VISIBLE);
                outTypespinner.setVisibility(View.VISIBLE);
                purposeSpinner.setVisibility(View.GONE);
                oll.setVisibility(View.GONE);
                ll.setVisibility(View.GONE);
            }

        }
        if(parentView.getId()==R.id.purposeSpinner){
            if (position == 0) {
                oll.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
            }else{
                ll.setVisibility(View.VISIBLE);
                oll.setVisibility(View.GONE);
            }
        }
        if(parentView.getId()==R.id.outTypeSpinner){
            outspinner.setVisibility(View.VISIBLE);
            if (position == 0) {
                List<String> contacts = mydb.getAllInOContacts();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.spinner_item, contacts);
                outspinner.setAdapter(adapter);
            }else {
                List<String> contacts = mydb.getAllInVMContacts();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.spinner_item, contacts);
                outspinner.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.GetReport) {
            ExportDatabaseCSVTask csvTask = new ExportDatabaseCSVTask();
            csvTask.execute();
            CSVToExcelConverter excelConverter = new CSVToExcelConverter();
            excelConverter.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    public void onSubmitButtonClick(View view){
        Log.d("MainActivity" , "Button Clicked");
        DBHelper dbHelper = new DBHelper(this);
        String name,uid,puspose, approver;
        EditText nameET = (EditText)findViewById(R.id.name);
        EditText uidET = (EditText)findViewById(R.id.mobile);
        EditText purposeET = (EditText)findViewById(R.id.purpose);
        EditText approverET = (EditText)findViewById(R.id.approver);
        TextView tv = (TextView) findViewById(R.id.error);
        name = nameET.getText().toString();
        uid = uidET.getText().toString();
        puspose = purposeET.getText().toString();
        approver = approverET.getText().toString();
        if(name.length()>1 && uid.length()==10) {
            tv.setVisibility(View.GONE);
            Calendar c = Calendar.getInstance();
            String date = "" + c.get(Calendar.YEAR) + ":" + c.get(Calendar.MONTH) + ":" + c.get(Calendar.DATE) + ":"
                    + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
            dbHelper.insertContact(name, uid,puspose,approver, date, "0");
        }else {
            tv.setText("Data not correct");
            tv.setVisibility(View.VISIBLE);
        }
        nameET.setText("");
        uidET.setText("");
        purposeET.setText("");
        approverET.setText("");
    }

    public void onOSubmitButtonClick(View view){
        Log.d("MainActivity" , "Button Clicked");
        DBHelper dbHelper = new DBHelper(this);
        String name,uid,purpose,approver;
        EditText nameET = (EditText)findViewById(R.id.o_name);
        EditText uidET = (EditText)findViewById(R.id.o_mobile);
        EditText purposeET = (EditText)findViewById(R.id.o_purpose);
        EditText approverET = (EditText)findViewById(R.id.o_approver);
        TextView tv = (TextView) findViewById(R.id.o_error);
        name = nameET.getText().toString();
        uid = uidET.getText().toString();
        purpose = purposeET.getText().toString();
        approver = approverET.getText().toString();
        if(name.length()>1 && uid.length()==7) {
            tv.setVisibility(View.GONE);
            Calendar c = Calendar.getInstance();
            String date = "" + c.get(Calendar.YEAR) + ":" + c.get(Calendar.MONTH) + ":" + c.get(Calendar.DATE) + ":"
                    + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);
            dbHelper.insertContact(name, "M"+uid,purpose,approver, date, "0");
        }else {
            tv.setText("Data not correct");
            tv.setVisibility(View.VISIBLE);
        }
        nameET.setText("");
        uidET.setText("");
        purposeET.setText("");
        approverET.setText("");
    }

    public void onOutSubmitButtonClick(View view){
        Spinner outSpinner = (Spinner)findViewById(R.id.outSpinner);
        TextView tv = (TextView) findViewById(R.id.out_error);
        Object out = outSpinner.getSelectedItem();

        if(out!=null){
            Log.d("On Out button", out.toString());
            List<String> data = Arrays.asList(out.toString().split("\\s*:\\s*"));
            tv.setVisibility(View.GONE);
            mydb.outUpdate(data);
            Spinner otype = (Spinner)findViewById(R.id.outTypeSpinner);
            if(otype.getSelectedItemPosition()==0){
                List<String> contacts = mydb.getAllInOContacts();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.spinner_item, contacts);
                outSpinner.setAdapter(adapter);
            }else {
                List<String> contacts = mydb.getAllInVMContacts();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        R.layout.spinner_item, contacts);
                outSpinner.setAdapter(adapter);
            }
        }else{
            tv.setText("Select at least one entry");
            tv.setVisibility(View.VISIBLE);
        }

    }

    public void onQueryButtonClick(View view){
        DBHelper dbHelper = new DBHelper(this);

        Log.d("MainActivity", dbHelper.getAllContacts().toString());
        Log.d("MainActivity", dbHelper.getAllInOContacts().toString());
        Log.d("MainActivity", dbHelper.getAllInVMContacts().toString());
    }

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean>

    {

        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()

        {

            this.dialog.setMessage("Exporting database...");

            this.dialog.show();

        }



        protected Boolean doInBackground(final String... args)

        {
            File dbFile=new File("database_name");
            //AABDatabaseManager dbhelper = new AABDatabaseManager(getApplicationContext());
            DBHelper dbhelper = new DBHelper(MainActivity.this) ;
            System.out.println(dbFile);  // displays the data base path in your logcat


            File exportDir = new File(Environment.getExternalStorageDirectory(), "");

            if (!exportDir.exists())

            {
                exportDir.mkdirs();
            }


            File file = new File(exportDir, "excerDB.csv");


            try

            {

                if (file.createNewFile()){
                    System.out.println("File is created!");
                    System.out.println("myfile.csv "+file.getAbsolutePath());
                }else{
                    System.out.println("File already exists.");
                }

                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                SQLiteDatabase db = dbhelper.getWritableDatabase();

                Cursor curCSV=db.rawQuery("select * from " + dbhelper.CONTACTS_TABLE_NAME,null);

                csvWrite.writeNext(curCSV.getColumnNames());

                while(curCSV.moveToNext())

                {

                    String arrStr[] ={curCSV.getString(0),curCSV.getString(1),curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),curCSV.getString(5),curCSV.getString(6)};


                    csvWrite.writeNext(arrStr);


                }

                csvWrite.close();
                curCSV.close();
        /*String data="";
        data=readSavedData();
        data= data.replace(",", ";");
        writeData(data);*/

                return true;

            }

            catch(SQLException sqlEx)

            {

                Log.e("MainActivity", sqlEx.getMessage(), sqlEx);

                return false;

            }

            catch (IOException e)

            {

                Log.e("MainActivity", e.getMessage(), e);

                return false;

            }

        }

        protected void onPostExecute(final Boolean success)

        {

            if (this.dialog.isShowing())

            {

                this.dialog.dismiss();

            }

            if (success)

            {

                Toast.makeText(MainActivity.this, "Export succeed", Toast.LENGTH_SHORT).show();

            }

            else

            {

                Toast.makeText(MainActivity.this, "Export failed", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public class CSVToExcelConverter extends AsyncTask<String, Void, Boolean> {


        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute()
        {this.dialog.setMessage("Exporting to excel...");
            this.dialog.show();}

        @Override
        protected Boolean doInBackground(String... params) {
            ArrayList arList=null;
            ArrayList al=null;

            //File dbFile= new File(getDatabasePath("database_name").toString());
            File dbFile=new File("database_name");
            String yes= dbFile.getAbsolutePath();

            String inFilePath = Environment.getExternalStorageDirectory().toString()+"/excerDB.csv";
            String outFilePath = Environment.getExternalStorageDirectory().toString()+"/Report.xls";
            String thisLine;
            int count=0;

            try {

                FileInputStream fis = new FileInputStream(inFilePath);
                DataInputStream myInput = new DataInputStream(fis);
                int i=0;
                arList = new ArrayList();
                while ((thisLine = myInput.readLine()) != null)
                {
                    al = new ArrayList();
                    String strar[] = thisLine.split(",");
                    for(int j=0;j<strar.length;j++)
                    {
                        al.add(strar[j]);
                    }
                    arList.add(al);
                    System.out.println();
                    i++;
                }} catch (Exception e) {
                System.out.println("shit");
            }

            try
            {
                HSSFWorkbook hwb = new HSSFWorkbook();
                HSSFSheet sheet = hwb.createSheet("new sheet");
                for(int k=0;k<arList.size();k++)
                {
                    ArrayList ardata = (ArrayList)arList.get(k);
                    HSSFRow row = sheet.createRow((short) 0+k);
                    for(int p=0;p<ardata.size();p++)
                    {
                        HSSFCell cell = row.createCell((short) p);
                        String data = ardata.get(p).toString();
                        if(data.startsWith("=")){
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            data=data.replaceAll("\"", "");
                            data=data.replaceAll("=", "");
                            cell.setCellValue(data);
                        }else if(data.startsWith("\"")){
                            data=data.replaceAll("\"", "");
                            cell.setCellType(Cell.CELL_TYPE_STRING);
                            cell.setCellValue(data);
                        }else{
                            data=data.replaceAll("\"", "");
                            cell.setCellType(Cell.CELL_TYPE_NUMERIC);
                            cell.setCellValue(data);
                        }
                        //*/
                        // cell.setCellValue(ardata.get(p).toString());
                    }
                    System.out.println();
                }
                FileOutputStream fileOut = new FileOutputStream(outFilePath);
                hwb.write(fileOut);
                fileOut.close();
                System.out.println("Your excel file has been generated");
            } catch ( Exception ex ) {
                ex.printStackTrace();
            } //main method ends
            return true;
        }

        protected void onPostExecute(final Boolean success)

        {

            if (this.dialog.isShowing())

            {

                this.dialog.dismiss();

            }

            if (success)

            {

                Toast.makeText(MainActivity.this, "file is built!", Toast.LENGTH_LONG).show();

            }

            else

            {

                Toast.makeText(MyApplication.getContext(), "file fail to build", Toast.LENGTH_SHORT).show();

            }

        }


    }
}
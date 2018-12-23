package laundry.aslijempolcustomer.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;

public class OrderActivity extends AppCompatActivity {

    EditText inputTanggal;
    EditText inputJam;
    EditText inputAlamat;
    EditText inputNote;
    EditText inputPerfume;
    TextView txtTanggal;
    Button btnNext;
    RadioGroup radioGroup;
    private ProgressDialog progressDialog;

    String selected_addr;
    String[] custAddrs;
    String[] custAddrs_more;
    String[] nama_service;
    String[] list_perfume;
    JSONArray addr;
    Integer[] radioid;
    private String selected_parfume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        initToolbar();
        init_dialog_data();
        init_dialog_parfume();
        init_service_data();
        initComponent();

    }

    private void init_dialog_parfume() {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_PERFUME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray parfumes = jsonObject.getJSONArray("parfumes");
                        list_perfume = new String[parfumes.length()];
                        for (int i = 0; i < parfumes.length(); i++) {
                            list_perfume[i] = parfumes.getString(i);
                        }
                        selected_parfume = list_perfume[0];
                        inputPerfume.setText(list_perfume[0]);
                    }else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                params.put("access_token",sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void init_service_data() {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_SERVICE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.d("Services: ", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        addr = jsonObject.getJSONArray("services");
                        radioGroup = findViewById(R.id.radioGrp);
                        RadioGroup.LayoutParams rdprm;

                        nama_service = new String[addr.length()];
                        radioid = new Integer[addr.length()];
                        for (int i = 0; i < addr.length(); i++) {
                            RadioButton radioButton = new RadioButton(OrderActivity.this);
                            if(i==0) radioButton.setChecked(true);
                            String nama = addr.getJSONObject(i).getString("nama");
                            nama_service[i] = nama;
                            String estimasi = addr.getJSONObject(i).getString("estimasi");
                            String harga = String.valueOf(addr.getJSONObject(i).getInt("harga"));
                            radioButton.setText(nama+" ("+estimasi+") Rp. "+harga+"/kg");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                radioButton.setTextAppearance(R.style.TextAppearance_AppCompat_Subhead);
                            }
                            int ids = i*2+3;
                            radioButton.setId(ids);
                            OrderActivity.this.radioid[i] = ids;
                            rdprm = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                            radioGroup.addView(radioButton,rdprm);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                params.put("access_token",sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void initComponent(){
//        TextInputLayout inputTanggalTL = findViewById(R.id.inputTanggalTL);
//        TextInputLayout inputJamTL = findViewById(R.id.inputJamTL);
//        TextInputLayout inputAlamatTL = findViewById(R.id.inputAlamatTL);

        inputTanggal = findViewById(R.id.inputTanggal);
        inputJam = findViewById(R.id.inputJam);
        inputAlamat = findViewById(R.id.inputAlamat);
        inputNote = findViewById(R.id.inputNote);
        txtTanggal = findViewById(R.id.txtTanggal);
        inputPerfume = findViewById(R.id.inputParfum);
        btnNext = findViewById(R.id.btn_next);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy");
        if (date.getHours() > 9 && date.getMinutes() > 0){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            date = c.getTime();
        }
        setTanggalView("EEEE, dd MMM yyyy",date);
        inputTanggal.setText(format.format(date));

        inputTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDatePicker();
            }
        });

        inputJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTimePicker();
            }
        });

        inputAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("choice", Arrays.toString(custAddrs));
                final AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                if(custAddrs != null){
                    builder.setTitle("Pilih alamat anda");
                    builder.setSingleChoiceItems(custAddrs, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selected_addr = custAddrs[i].toString()+"-"+custAddrs_more[i].toString();
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            inputAlamat.setText(selected_addr);
                        }
                    });
                    builder.setNegativeButton("BATAL", null);
                    builder.setNeutralButton("BARU", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            Intent intent = new Intent(OrderActivity.this, PickAddressActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }else {
                    builder.setTitle("Choose your address");
                    builder.setNegativeButton("CANCEL", null);
                    builder.setNeutralButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            Intent intent = new Intent(OrderActivity.this, PickAddressActivity.class);
                            startActivity(intent);
                        }
                    });
                    builder.show();
                    Toast.makeText(getApplicationContext(), "Wait for data", Toast.LENGTH_SHORT);
                }
            }
        });

        inputPerfume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("choice_parfume", Arrays.toString(list_perfume));
                final AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                if(list_perfume != null){
                    builder.setTitle("Pilih parfum yang tersedia");
                    builder.setSingleChoiceItems(list_perfume, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            selected_parfume = list_perfume[i];
                        }
                    });
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            inputPerfume.setText(selected_parfume);
                        }
                    });
                    builder.setNegativeButton("BATAL", null);
                    builder.show();
                }else {
                    Toast.makeText(getApplicationContext(), "Wait for data", Toast.LENGTH_SHORT);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    MySingleton.getInstance(getApplicationContext()).setTanggalOrder(inputTanggal.getText().toString());
                    MySingleton.getInstance(getApplicationContext()).setJamOrder(inputJam.getText().toString());
//                    String[] splitAddr = inputAlamat.getText().toString().split("-");
                    MySingleton.getInstance(getApplicationContext()).setAddressOrder(inputAlamat.getText().toString());
                    MySingleton.getInstance(getApplicationContext()).setParfum(inputPerfume.getText().toString());
                    MySingleton.getInstance(getApplicationContext()).setNoteOrder(inputNote.getText().toString());
                    int selectedService = radioGroup.getCheckedRadioButtonId();
                    int radioPos = Arrays.asList(radioid).indexOf(selectedService);
                    MySingleton.getInstance(getApplicationContext()).setTipeService(nama_service[radioPos]);
                    try {
                        MySingleton.getInstance(getApplicationContext()).setTipeServiceHarga(addr.getJSONObject(radioPos).getInt("harga"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent toOrderA = new Intent(OrderActivity.this, OrderAActivity.class);
                    startActivity(toOrderA);
                }

            }
        });
    }

    private void setTanggalView(String pattern, Date date){
        Locale id = new Locale("in", "ID");
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, id);
        txtTanggal.setText(dateFormat.format(date));
    }

    private boolean validateForm() {
        boolean valid = true;

        String tanggal = inputTanggal.getText().toString();
        String jam = inputJam.getText().toString();
        String alamat = inputAlamat.getText().toString();
        String parfume = inputPerfume.getText().toString();

        if(tanggal.isEmpty() || tanggal.equals(" ")){
            inputTanggal.setError("Tanggal tidak valid");
            valid = false;
        }else{
            inputTanggal.setError(null);
        }

        if (jam.isEmpty() || jam.equals(" ")){
            inputJam.setError("Jam tidak valid");
            valid = false;
        }else {
            inputJam.setError(null);
        }

        if (alamat.isEmpty() || alamat.equals(" ")){
            inputAlamat.setError("Alamat tidak valid");
            valid = false;
        }else {
            inputAlamat.setError(null);
        }

        if (parfume.isEmpty() || parfume.equals(" ")){
            inputPerfume.setError("Parfum tidak valid");
            valid = false;
        }else{
            inputPerfume.setError(null);
        }

        return valid;
    }

    private void init_dialog_data(){
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_CUST_ADDRS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Cust addrs: ", response.toString());
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        JSONArray addr = jsonObject.getJSONArray("address_name");
                        JSONArray addrs = jsonObject.getJSONArray("address");

                        custAddrs = new String[addr.length()];
                        custAddrs_more = new String[addr.length()];
                        for (int i = 0; i < addr.length(); i++) {
                            custAddrs[i] = addr.getString(i);
                            custAddrs_more[i] = addrs.getString(i);
                        }
                        selected_addr = custAddrs[0].toString()+"-"+custAddrs_more[0].toString();
                        inputAlamat.setText(selected_addr);
                    }else{
                        Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                params.put("access_token",sessionManager.getAccessToken());

                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void dialogTimePicker() {
        Calendar cur_calender = Calendar.getInstance();
        TimePickerDialog timePicker = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                inputJam.setText(hourOfDay + ":" + minute);
            }
        }, cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), true);
        //set dark light
        timePicker.setThemeDark(false);
        if (cur_calender.get(Calendar.HOUR_OF_DAY) < 21){
            timePicker.setMinTime(cur_calender.get(Calendar.HOUR_OF_DAY), cur_calender.get(Calendar.MINUTE), cur_calender.get(Calendar.SECOND));
        }else{
            timePicker.setMinTime(8,0,0);
        }
        timePicker.setMaxTime(21,0,0);
        timePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        timePicker.show(getFragmentManager(), "Timepickerdialog");
    }



    private void dialogDatePicker() {
        Calendar cur_calnedar = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String theTanggal = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
                        inputTanggal.setText(theTanggal);
                        SimpleDateFormat format = new SimpleDateFormat("d-M-yyyy");
                        try {
                            Date date = format.parse(theTanggal);
                            setTanggalView("EEEE, dd MMM yyyy",date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                cur_calnedar.get(Calendar.YEAR),
                cur_calnedar.get(Calendar.MONTH),
                cur_calnedar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        if (cur_calnedar.get(Calendar.HOUR_OF_DAY) > 20 && cur_calnedar.get(Calendar.MINUTE) > 59){
            cur_calnedar.add(Calendar.DAY_OF_MONTH, 1);
        }
        datePicker.setMinDate(cur_calnedar);
        datePicker.show(getFragmentManager(), "Datepickerdialog");
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.pickup_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init_dialog_data();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }
}

package laundry.aslijempolcustomer.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import laundry.aslijempolcustomer.R;
import laundry.aslijempolcustomer.utils.ApiConfig;
import laundry.aslijempolcustomer.utils.MySingleton;
import laundry.aslijempolcustomer.utils.SessionManager;
import laundry.aslijempolcustomer.utils.Tools;

public class PickAddressActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    EditText inputLabel;
    EditText inputAddress;
    EditText inputNote;
    Button btnSave;
    FloatingActionButton btnGps;
    String id;
    Intent getI;

    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    Location location;
    CameraUpdate cameraUpdate;
    Marker marker;
    Geocoder geocoder;
    List<Address> addresses;
    double latitude;
    double longitude;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int FINE_LOCATION_PERMISSION_REQUEST = 2;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_address);
        init_toolbar();

        inputAddress = findViewById(R.id.inputAddress);
        inputLabel = findViewById(R.id.inputLabel);
        inputNote= findViewById(R.id.inputNote);
        btnSave = findViewById(R.id.btnSave);
        btnGps = findViewById(R.id.btnGps);

        getI = getIntent();
        id = getI.getStringExtra("id");
        geocoder = new Geocoder(this, Locale.getDefault());
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        inputAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    inputAddress.setEnabled(false);
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(PickAddressActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    inputAddress.setEnabled(true);
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    inputAddress.setEnabled(true);
                    // TODO: Handle the error.
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                final String label = inputLabel.getText().toString();
                final String addrs = inputAddress.getText().toString();
                final String note = inputNote.getText().toString();
                final double lat = PickAddressActivity.this.latitude;
                final double lng = PickAddressActivity.this.longitude;

                String id = PickAddressActivity.this.id;

                if (label.isEmpty()){
                    inputLabel.setError("Label tidak valid!");
                    valid = false;
                }else {
                    inputLabel.setError(null);
                }

                if (addrs.isEmpty() || addrs.length()<3){
                    inputAddress.setError("Alamat tidak valid");
                    valid=false;
                }else {
                    inputAddress.setError(null);
                }

                if (lat == 0.0 || lng == 0.0){
                    Toast.makeText(PickAddressActivity.this, "Lat&Lng is empty", Toast.LENGTH_SHORT);
                    valid = false;
                }

                if (valid){
                    progressDialog.setMessage("loading...");
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST,
                            ApiConfig.URL_ADD_ADDR, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("add", response);
                            progressDialog.hide();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean error = jsonObject.getBoolean("error");

                                if (!error) {
                                    Dialog dialog = new Dialog(PickAddressActivity.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.dialog_info);
                                    dialog.setCancelable(false);

                                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                                    lp.copyFrom(dialog.getWindow().getAttributes());
                                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                                    dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    });
                                    ((TextView) dialog.findViewById(R.id.title)).setText(R.string.thank_yout);
                                    ((TextView) dialog.findViewById(R.id.description)).setText(R.string.address_submitted);

                                    dialog.show();
                                    dialog.getWindow().setAttributes(lp);
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.hide();
                            Log.d("errr", error.getMessage());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            SessionManager sessionManager = new SessionManager(PickAddressActivity.this);
                            params.put("access_token", sessionManager.getAccessToken());
                            params.put("label", label);
                            params.put("address", addrs);
                            params.put("note", note);
                            params.put("lat", String.valueOf(lat));
                            params.put("lng", String.valueOf(lng));
                            if (PickAddressActivity.this.id != null){
                                params.put("id", PickAddressActivity.this.id);
                            }
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }
            }
        });

        btnGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationNow();
            }
        });

        buildGoogleApiClient();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = Tools.configActivityMaps(googleMap);
                if (id != null){
//                    Log.d("maps id", id);
                    load_addr_data();
                }
                map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        PickAddressActivity.this.latitude = marker.getPosition().latitude;
                        PickAddressActivity.this.longitude = marker.getPosition().longitude;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            if (addresses.size() > 0)
                            inputAddress.setText(addresses.get(0).getAddressLine(0)+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void load_addr_data() {
        progressDialog.setMessage("loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ApiConfig.URL_GET_ADDRS_BY_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("load", response);
                progressDialog.hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        inputAddress.setText(jsonObject.getString("alamat"));
                        inputLabel.setText(jsonObject.getString("nama_address"));
                        if (jsonObject.getString("keterangan") != "null")
                        inputNote.setText(jsonObject.getString("keterangan"));
                        PickAddressActivity.this.latitude = jsonObject.getDouble("lat");
                        PickAddressActivity.this.longitude = jsonObject.getDouble("lng");
                        setMarker();
                    }else {
                        Toast.makeText(PickAddressActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> params = new HashMap<>();
                SessionManager sessionManager = new SessionManager(PickAddressActivity.this);
                params.put("access_token", sessionManager.getAccessToken());
                params.put("id", id);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private void getLocationNow() {
        if (marker != null) {
            marker.remove();
        }
        if (location != null){
            marker = map.addMarker(new MarkerOptions().position(new LatLng(this.latitude, this.longitude)).draggable(true));
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(this.latitude, this.longitude), 15);
            map.moveCamera(cameraUpdate);

            try {
                addresses = geocoder.getFromLocation(this.latitude, this.longitude, 1);
                inputAddress.setText(addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setMarker(){
        if (marker != null) {
            marker.remove();
        }
        if (location != null) {
            marker = map.addMarker(new MarkerOptions().position(new LatLng(this.latitude, this.longitude)).draggable(true));
            cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(this.latitude, this.longitude), 15);
            map.moveCamera(cameraUpdate);
        }
    }

    private void init_toolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.pick_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findLatLngNow();
    }

    private void findLatLngNow() {
        if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);
        }else{
            try {
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (location != null){
                    this.latitude = location.getLatitude();
                    this.longitude = location.getLongitude();
                    setMarker();
                }
            }catch (SecurityException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                inputAddress.setText(place.getAddress());
                this.latitude = place.getLatLng().latitude;
                this.longitude = place.getLatLng().longitude;
                setMarker();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Error autocomplete", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        inputAddress.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findLatLngNow();
                }
            }
        }
    }
}

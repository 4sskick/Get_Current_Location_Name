package androdev.getcurrentlocation;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager = null;
    LocationListener locationListener = null;

    public Button buttonGetLocation;
    public EditText editTextLocation;
    ProgressBar progressBar;

    final String TAG = "DEBUG";
    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextLocation = (EditText) findViewById(R.id.editTextLocation);
        buttonGetLocation = (Button) findViewById(R.id.btnLocation);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        progressBar.setVisibility(View.INVISIBLE);
        buttonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = displayGetGPSStatus();
                if (flag) {
                    Log.d(TAG, "onClick: ");
                    editTextLocation.setText("Move your device to see chage of coordinates!");
                    progressBar.setVisibility(View.VISIBLE);

                    locationListener = new MyLocationListeners();
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            5000,
                            10,
                            locationListener
                    );
                } else {
                    alertBox("GPS Status", "Your GPS: OFF");
                }
            }
        });
    }

    private void alertBox(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device is Disable")
                .setCancelable(false)
                .setTitle("GPS STATUS")
                .setPositiveButton(
                        "GPS ON",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                /* this gonna call class of settings then dialog interface disappeared */
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGetGPSStatus() {
        boolean gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        ContentResolver contentResolver = getContentResolver();
//        boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(
//                contentResolver,
//                LocationManager.GPS_PROVIDER
//        );

        if (gpsStatus) {
            return true;
        } else {

            return false;
        }
    }

    private class MyLocationListeners implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            editTextLocation.setText("");
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(
                    getApplicationContext(),
                    "Location Changed: Lat: "+location.getLatitude()+" Long: "+location.getLongitude(),
                    Toast.LENGTH_SHORT).show();

            String longitude = "Longitude: "+location.getLongitude();
            Log.d(TAG, "onLocationChanged: "+longitude);

            String latitude = "Latitude: "+location.getLatitude();
            Log.d(TAG, "onLocationChanged: "+latitude);

        /* Get city name from coordinates */
            String cityName = null;
            Geocoder geoCoder = new Geocoder(
                    getApplicationContext(),
                    Locale.getDefault()
            );
            List<Address> addressList;
            try{
                // getFromLocation (double latitude, double longitude, int maxResults)
                addressList = geoCoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1
                );
                if(addressList.size() > 0){
                    cityName = addressList.get(0).getLocality();
                    Log.d(TAG, "onLocationChanged: Address: "+cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            editTextLocation.setText(longitude+", "+latitude+" Current City: "+cityName);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

package com.yt37.clima;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {
    final String url = "http://api.openweathermap.org/data/2.5/weather";
    final String appId = "b274c054ae9cd9cb734082db041caf4c";

    final int reqCode = 123;

    final long min = 5000;
    final float minDist = 1000;

    String locProv = LocationManager.GPS_PROVIDER;

    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;
    LocationManager mLocMan;
    LocationListener mLocLis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);

        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appInt = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(appInt);
            }
        });

    }

    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String city = intent.getStringExtra("City");

        if (city != null) {
            getWeatherForNewCity(city);

        } else {
            getWeatherForCurrentLocation();
        }
    }

    private void getWeatherForNewCity(String city){
        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", appId);
        networking(params);
    }

    private void getWeatherForCurrentLocation() {
        mLocMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocLis = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", appId);
                networking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(WeatherController.this, "Location Is Disabled", Toast.LENGTH_SHORT).show();

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
            return;
        }
        mLocMan.requestLocationUpdates(locProv, min, minDist, mLocLis);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == reqCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima", "Permission Granted");
                getWeatherForCurrentLocation();
            } else {
                Log.d("Clima", "Permission Denied");
            }
        }
    }

    private void networking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                WeatherDataModel weather = WeatherDataModel.fromJson(response);
                if (weather != null) {
                    updateUI(weather);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void updateUI(WeatherDataModel weather) {
        mCityLabel.setText(weather.getCity());
        mTemperatureLabel.setText(weather.getTemp());

        int resource = getResources().getIdentifier(weather.getIcon(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resource);

    }

    protected void onPause() {
        super.onPause();
        if(mLocMan != null) mLocMan.removeUpdates(mLocLis);
    }

}

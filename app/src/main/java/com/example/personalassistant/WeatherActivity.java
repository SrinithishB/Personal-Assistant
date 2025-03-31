package com.example.personalassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextView locationText, temperatureText, weatherConditionText;
    private ImageView weatherBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherBackground = findViewById(R.id.weatherBackground);
        locationText = findViewById(R.id.locationText);
        temperatureText = findViewById(R.id.temperatureText);
        weatherConditionText = findViewById(R.id.weatherConditionText);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    new FetchWeatherTask().execute(latitude, longitude);
                } else {
                    Toast.makeText(WeatherActivity.this, "Failed to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class FetchWeatherTask extends AsyncTask<Double, Void, String> {
        @Override
        protected String doInBackground(Double... params) {
            double latitude = params[0];
            double longitude = params[1];

            String apiKey = "3b98b32be67bc47386828e260fa56595"; // Replace with your API key
            String apiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey + "&units=metric";

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseWeatherData(result);
            } else {
                Toast.makeText(WeatherActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseWeatherData(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String cityName = jsonObject.getString("name");

            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            String weatherDescription = weatherObject.getString("description");

            locationText.setText(cityName);
            temperatureText.setText(temperature + "°C");
            weatherConditionText.setText(weatherDescription);
            updateBackground(weatherDescription);
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing weather data", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateBackground(String weatherCondition) {
        switch (weatherCondition.toLowerCase()) {
            case "clouds":
                weatherBackground.setImageResource(R.drawable.bg_cloudy);
                break;
            case "rain":
                weatherBackground.setImageResource(R.drawable.bg_rainy);
                break;
            case "thunderstorm":
                weatherBackground.setImageResource(R.drawable.bg_stromy);
                break;
            case "snow":
                weatherBackground.setImageResource(R.drawable.bg_snowy);
                break;
            case "fog":
            case "mist":
                weatherBackground.setImageResource(R.drawable.bg_foggy);
                break;
            default:
                weatherBackground.setImageResource(R.drawable.bg_sunny);
        }
    }
}
package com.yt37.clima;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDataModel {

    private String mTemp;
    private int mCon;
    private String mCity;
    private String mIcon;

    public static WeatherDataModel fromJson(JSONObject jsonObject) {
        try {
            WeatherDataModel weather = new WeatherDataModel();

            weather.mCity = jsonObject.getString("name");
            weather.mCon = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weather.mIcon = updateIcon(weather.mCon);

            double temp = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int round = (int) Math.rint(temp);

            weather.mTemp = Integer.toString(round);
            return weather;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String updateIcon(int condition) {

        if (condition >= 0 && condition < 300) {
            return "tstorm1";
        } else if (condition >= 300 && condition < 500) {
            return "light_rain";
        } else if (condition >= 500 && condition < 600) {
            return "shower3";
        } else if (condition >= 600 && condition <= 700) {
            return "snow4";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition < 800) {
            return "tstorm3";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy2";
        } else if (condition >= 900 && condition <= 902) {
            return "tstorm3";
        } else if (condition == 903) {
            return "snow5";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "tstorm3";
        }

        return "dunno";
    }

    public String getTemp() {
        return mTemp + "°";
    }

    public String getCity() {
        return mCity;
    }

    public String getIcon() {
        return mIcon;
    }
}

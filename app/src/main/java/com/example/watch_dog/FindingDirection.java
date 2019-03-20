package com.example.watch_dog;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FindingDirection {
    private static final String GOOGLE_DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyAiQ2NTa3GzRvKSlUyszTv8aGfQn22vhVY";
    private DirectFinderListner Directlistener;
    private String Start;
    private String end;

    /**
     * Constructor For finding directions
     * @param listner is the parameter for the DirectFinderListner Class
     * @param Start is the starting location of where you are currently
     * @param end is the ending location of your destination
     */
    public FindingDirection(DirectFinderListner listner, String Start, String end){
        this.Directlistener = listner;
        this.Start = Start;
        this.end = end;

    }

    /**
     * execute the OndirectfinderStart function and creates a new Raw data from the url
     * @throws UnsupportedEncodingException
     */
    public void execute() throws UnsupportedEncodingException {
        Directlistener.onDirectFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    /**
     * Creates url from the user given start, end, and GOogle API
     * @return
     * @throws UnsupportedEncodingException
     */
    private String createUrl() throws UnsupportedEncodingException {
        String urlStart = URLEncoder.encode(Start, "utf-8");
        String urlDest = URLEncoder.encode(end, "utf-8");

        return GOOGLE_DIRECTION_URL_API + "origin=" + urlStart + "&destination=" + urlDest + "&key=" + GOOGLE_API_KEY;
    }

    /**
     * Private class that focuses on download the data from the GOOGLe API
     */
    private class DownloadRawData extends AsyncTask<String, Void, String> {
        /**
         * Run in background on what GEO Cord Information and checks if its ok to run or if any errors happen retriving the information
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private void parseJSon(String data) throws JSONException {
        if (data == null) {
            return;
        }

        List<RouteFromStartToDest> routes = new ArrayList<RouteFromStartToDest>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            RouteFromStartToDest r = new RouteFromStartToDest();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
            JSONObject jsonLeg = jsonLegs.getJSONObject(0);
            JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
            JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

            r.eAdd = jsonLeg.getString("end_address");
            r.sAdd = jsonLeg.getString("start_address");
            r.sLoc = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
            r.eLoc = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
            r.points = decodePolyLine(overview_polylineJson.getString("points"));

            routes.add(r);
        }

        Directlistener.onDirectFinderSuccess(routes);
    }

    /**
     * Decodes the poly line in an Array for Latitude
     * @param poly
     * @return
     */
    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decode = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decode.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decode;
    }
}

package edu.uw.ask710.geopaint;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.ArrayList;

/**
 * Code for converting Polylines into GeoJSON formatted Strings (and back)
 * @author Joel Ross 
 * @author Kyungmin Lee
 * @author Zico Deng
 * @version May 2017
 */
public class GeoJsonConverter {

    /**
     * Returns a GeoJSON String representing the given list of Polylines. Style information
     * (width and color) is included as properties.
     * @param lines A list of Polylines
     * @return A GeoJSON FeatureCollection of LineStrings
     */
    public static String convertToGeoJson(List<Polyline> lines) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"type\": \"FeatureCollection\", " +
                "\"features\": [");
        for (Polyline line : lines) {
            builder.append("{ \"type\": \"Feature\", " + //new feature for each line
                    "\"geometry\": { \"type\": \"LineString\", "+ "\"coordinates\": [ ");
            for(LatLng point : line.getPoints()) { //add points
                builder.append("["+point.longitude+","+point.latitude+"],"); //invert lat/lng for GeoJSON

            }
            builder = builder.deleteCharAt(builder.length() - 1); //remove trailing comma
            builder.append("]},"); //end geometry
            builder.append("\"properties\": { ");
            builder.append("\"color\": " + line.getColor() + ","); //color property
            builder.append("\"width\": " + line.getWidth()); //width property
            builder.append("} },"); //end properties/feature
        }
        builder = builder.deleteCharAt(builder.length() - 1); //remove trailing comma
        builder.append("]}"); //end json
        return builder.toString();
    }

    /**
     * Returns a list of PolylineOptions (for creating Polylines) from a GeoJSON string.
     * Color of the lines should be stores in "properties.color" for each geometry.
     * @param geojson A String of a GeoJSON FeatureCollection of LineStrings
     * @return A list of PolylineOptions representing those LineStrings
     * @throws JSONException if error parsing the GeoJSON String
     */
    public static List<PolylineOptions> convertFromGeoJson(String geojson) throws JSONException {

        ArrayList<PolylineOptions> polyLineList = new ArrayList<PolylineOptions>();

        JSONArray featuresArray = new JSONObject(geojson).getJSONArray("features");

        //loop through features, creating an options for each line.
        for(int i=0; i< featuresArray.length(); i++){
            JSONObject featureObj = featuresArray.getJSONObject(i);

            //get polyline properties
            JSONObject properties = featureObj.getJSONObject("properties");
            int color = properties.getInt("color");
            float width = (float)properties.getDouble("width");

            //define the polyline
            PolylineOptions line = new PolylineOptions()
                    .color(color)
                    .width(width);

            //get LatLng coordinates
            JSONArray coordinates = featureObj.getJSONObject("geometry").getJSONArray("coordinates");

            //iterate through coordinates and add them to polyline
            for(int j = 0; j < coordinates.length(); j++) {
                JSONArray currCoord = coordinates.getJSONArray(j);
                LatLng curr = new LatLng(currCoord.getDouble(1), currCoord.getDouble(0));
                line.add(curr);
            }

            polyLineList.add(line);
        }
        return polyLineList;
    }
}
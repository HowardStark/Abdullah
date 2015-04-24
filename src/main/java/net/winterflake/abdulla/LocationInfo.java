package net.winterflake.abdulla;

import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;
import sun.awt.image.BufferedImageDevice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by howard on 4/22/15.
 */
public class LocationInfo {

    private String ip;
    private String state;
    private String country;
    private String city;
    private String zip;
    private String countryIso;
    private String stateIso;
    private boolean reachable = true;


    public LocationInfo(String ip, String country, String state, String city, String zip, String countryIso, String stateIso) {
        this.ip = ip;
        this.state = state;
        this.country = country;
        this.zip = zip;
        this.city = city;
        this.countryIso = countryIso;
        this.stateIso = stateIso;
    }

    public LocationInfo(boolean reachable){
        super();
        this.reachable = reachable;
    }

    public String getIp() {
        return ip;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getZip() {
        return zip;
    }

    public boolean isReachable() {
        return reachable;
    }

    public String getLocalWeather() throws Exception{
        if(reachable) {
            URL weather = new URL("http://api.openweathermap.org/data/2.5/weather?zip=" + zip + "," + countryIso + "&units=imperial");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    weather.openStream()));
            String json = in.readLine();
            in.close();
            System.out.println(json);
            JSONObject jsonObject = new JSONObject(json);
            Integer temp = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");

            Abdulla abdulla = new Abdulla();
            BufferedReader fileInput = new BufferedReader(new InputStreamReader(abdulla.getClass().getResourceAsStream("weather.json")));
            JSONObject message = new JSONObject(fileInput.readLine());

            for (int i = 0; i < 10; i++) {
                System.out.println(message.getJSONArray(temp.toString()).getJSONObject(new Random().nextInt(3)).getString("message"));
            }
            return message.getJSONArray(temp.toString()).getJSONObject(new Random().nextInt(3)).getString("message");
        }
        return "I could not get the weather";
    }

    public String getLocalTrain() throws Exception {
        if(reachable) {
            URL caltrain = new URL("http://caltrain-realtime.herokuapp.com/api/"+city);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    caltrain.openStream()));
            String json = in.readLine();
            in.close();
            JSONObject jsonObject = new JSONObject(json);
            Integer southBound = jsonObject.getJSONArray("southbound").getJSONObject(1).getInt("minutesUntilDeparture");
            Integer northBound = jsonObject.getJSONArray("northbound").getJSONObject(1).getInt("minutesUntilDeparture");
            return "There is a southbound train in " + southBound.toString() + "minutes and a northbound train in " + northBound.toString() + "minutes.";
        }
        return "I couldn't fetch the CalTrain schedule.";
    }
}

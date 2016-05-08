package net.winterflake.abdulla;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.java2d.pipe.SpanShapeRenderer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by howard on 4/10/15.
 */
public class Abdulla {

    private static final String ACOUSTIC_MODEL =
            "resource:/edu/cmu/sphinx/models/en-us/en-us";
    private static final String DICTIONARY_PATH =
            "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH =
            "resource:/net/winterflake/abdulla";
    private static final String LANGUAGE_MODEL_PATH =
            "resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp";

    private static LocationInfo currentLoc;

    public Abdulla() {}

    public static void main(String[] args) throws Exception {
        setLocalInformation();
        System.out.println(currentLoc.getLocalWeather());

        Configuration config = new Configuration();
        config.setAcousticModelPath(ACOUSTIC_MODEL);
        config.setDictionaryPath(DICTIONARY_PATH);
        config.setGrammarPath(GRAMMAR_PATH);
        config.setLanguageModelPath(LANGUAGE_MODEL_PATH);
        config.setUseGrammar(true);

        config.setGrammarName("abdulla");
        LiveSpeechRecognizer confRecognizer =
                new LiveSpeechRecognizer(config);

        confRecognizer.startRecognition(true);
        while (true) {
            String utterance = confRecognizer.getResult().getHypothesis();

            System.out.println(utterance);

            if (utterance.startsWith("okay abdullah") && utterance.contains("exit")) {
                System.out.println("Crazay");
                break;
            }

            if(utterance.contains("abdullah")) {
                System.out.println("Crazay");
                confRecognizer.stopRecognition();
                recognizeCommand(confRecognizer);
                confRecognizer.startRecognition(true);
            }

        }

        confRecognizer.stopRecognition();
    }

    private static void recognizeCommand(LiveSpeechRecognizer recognizer) throws Exception{

        recognizer.startRecognition(true);
        while (true) {
            String utterance = recognizer.getResult().getHypothesis();

            if (utterance.startsWith("how are you")) {
                Runtime.getRuntime().exec("say \"I am good. Thank you for asking!\"");
                break;
            } else
                System.out.println(utterance);

            if (utterance.contains("weather")) {
                String[] args = {"say", currentLoc.getLocalWeather()};
                Runtime.getRuntime().exec(args);
                break;
            }

            if (utterance.contains("time")) {
                String[] args = {"say", "It is " + getSpeakableTime()};
                Runtime.getRuntime().exec(args);
                break;
            }

            if (utterance.contains("train")) {
                String[] args = {"say", currentLoc.getLocalTrain()};
                Runtime.getRuntime().exec(args);
                break;
            }

        }
        recognizer.stopRecognition();
    }

    public static void setLocalInformation() throws Exception{
        URL whatismyip = new URL("https://context.skyhookwireless.com/accelerator/ip?version=2.0&key=&user=abdulla");
        BufferedReader in = new BufferedReader(new InputStreamReader(
                whatismyip.openStream()));
        String json = in.readLine();
        in.close();
        System.out.println(json);
        JSONObject jsonObject = new JSONObject(json);
        try {
            currentLoc = new LocationInfo(jsonObject.getJSONObject("data").getString("ip"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("country"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("state"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("city"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("postcode"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("countryIso"), jsonObject.getJSONObject("data").getJSONObject("civic").getString("stateIso"));
        } catch (JSONException ex){
            System.err.println(ex);
            currentLoc = new LocationInfo(false);
        }
    }

    public static String getSpeakableTime() {
        Date date = Calendar.getInstance().getTime();
        return new SimpleDateFormat("h").format(date) + " " + new SimpleDateFormat("mm").format(date);
    }

}

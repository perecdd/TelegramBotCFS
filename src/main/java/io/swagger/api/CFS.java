package io.swagger.api;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CFS {
    public static final Integer port = 3939;
    public static final String ip = "http://cfs";
    public static HttpURLConnection con;

    public static boolean tryLogin(String email, String password) {
        try {
            URL url = new URL (ip + ":" + port + "/user");

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("email", email);
            con.setRequestProperty("password", password);
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            int responseCode = con.getResponseCode();
            con.disconnect();

            return responseCode == 200;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static JSONArray getBasket(String email, String password){
        try {
            URL url = new URL (ip + ":" + port + "/user");

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("email", email);
            con.setRequestProperty("password", password);
            con.setRequestMethod("GET");
            con.setDoOutput(true);
            con.setDoInput(true);

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }

            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(sb.toString());

            int responseCode = con.getResponseCode();
            con.disconnect();

            return jsonArray;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

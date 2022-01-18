package io.swagger;

import io.swagger.api.CFS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.util.logging.Level;

public class Bot extends TelegramLongPollingBot {
    public static final Integer port = 1515;
    public static final String ip = "http://localhost";
    public static HttpURLConnection con;

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String id = update.getMessage().getChatId().toString();

        try {
            String[] elements = message.split("[ ]+");
            switch (elements[0]){
                case "/login": login(id, elements[1], elements[2]);
                    break;
                case "/orders": orders(id);
                    break;
                case "/basket": basket(id);
                    break;
                case "/cancel": cancel(id, Integer.valueOf(elements[1]));
                    break;
                default: sendMsg(id, "Unknown command, please try again.");
                    break;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void login(String chatid, String email, String password){
        try {
            URL url = new URL (ip + ":" + port + "/user/login");

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

            if(responseCode == 200){
                DataBase.SetEmailAndPassword(chatid, email, password);
            }
            else{
                sendMsg(chatid, "Can't connect with Cloud Food Store.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendMsg(chatid, "Sorry, something went wrong...");
        }
    }

    public void basket(String chatid){
        try {
            URL url = new URL (ip + ":" + port + "/user/basket");

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");

            Pair<String, String> loginPair = DataBase.getEmailAndPassword(chatid);

            con.setRequestProperty("email", loginPair.first);
            con.setRequestProperty("password", loginPair.second);
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

            if(responseCode == 200){
                StringBuilder message = new StringBuilder();
                Integer iters = 0;
                for(Object obj : jsonArray.toArray()){
                    iters++;
                    JSONObject json = (JSONObject) obj;
                    message.append("count: ").append(json.get("count")).append('\n');
                    message.append("description: ").append(json.get("description")).append('\n');
                    message.append("name: ").append(json.get("name")).append('\n');
                    message.append("price: ").append(json.get("price")).append('\n');
                    message.append("-------------").append('\n');
                }

                if(iters > 0) {
                    sendMsg(chatid, message.toString());
                }
                else{
                    sendMsg(chatid, "Your basket is empty.");
                }
            }
            else{
                sendMsg(chatid, "Can't connect with Cloud Food Store.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendMsg(chatid, "Sorry, something went wrong...");
        }
    }

    public void orders(String chatid){
        try {
            URL url = new URL (ip + ":" + port + "/user/tickets");

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");

            Pair<String, String> loginPair = DataBase.getEmailAndPassword(chatid);

            con.setRequestProperty("email", loginPair.first);
            con.setRequestProperty("password", loginPair.second);
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

            if(responseCode == 200){
                StringBuilder message = new StringBuilder();
                Integer iters = 0;
                for(Object obj : jsonArray.toArray()){
                    JSONObject json = (JSONObject) obj;
                    String status = (String) json.get("status");
                    if(status.equals("cancel") || status.equals("canceled") || status.equals("success") || status.equals("successful")) continue;
                    iters++;
                    JSONArray jsonArray1 = (JSONArray) json.get("products");
                    message.append("id: ").append(json.get("id")).append('\n');
                    message.append("status: ").append(status).append('\n');
                    message.append("products: ").append('\n');
                    for(Object object : jsonArray1){
                        JSONObject Json = (JSONObject) object;
                        message.append("    count: ").append(Json.get("count")).append('\n');
                        message.append("    description: ").append(Json.get("description")).append('\n');
                        message.append("    name: ").append(Json.get("name")).append('\n');
                        message.append("    price: ").append(Json.get("price")).append('\n');
                        message.append("    -------------").append('\n');
                    }
                    message.append("-------------").append('\n');
                }

                if(iters > 0) {
                    sendMsg(chatid, message.toString());
                }
                else{
                    sendMsg(chatid, "Your order list is empty.");
                }
            }
            else{
                sendMsg(chatid, "Can't connect with Cloud Food Store.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendMsg(chatid, "Sorry, something went wrong...");
        }
    }

    public void cancel(String chatid, Integer ticket){
        try {
            URL url = new URL (ip + ":" + port + "/user/tickets");

            con = (HttpURLConnection)url.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");

            Pair<String, String> loginPair = DataBase.getEmailAndPassword(chatid);

            con.setRequestProperty("email", loginPair.first);
            con.setRequestProperty("password", loginPair.second);
            con.setRequestProperty("id", String.valueOf(ticket));
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);

            int responseCode = con.getResponseCode();
            con.disconnect();

            if(responseCode == 200){
                sendMsg(chatid, "The order has been successfully canceled.");
            }
            else{
                sendMsg(chatid, "Can't connect with Cloud Food Store.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            sendMsg(chatid, "Sorry, something went wrong...");
        }
    }

    public void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Cloud Food Store Bot";
    }

    @Override
    public String getBotToken() {
        return "2006641971:AAEdwq0gsi53WP1dGOymZMIRj1zbD55wuHI";
    }
}

package io.swagger;

import io.swagger.api.CFS;
import org.json.simple.JSONArray;

import java.sql.*;

class Pair<T, V>{
    T first = null;
    V second = null;

    public Pair(T first, V second){
        this.first = first;
        this.second = second;
    }
}

public class DataBase {
    public static Connection connection;
    public static Statement statement;

    public static boolean SetEmailAndPassword(String chatid, String email, String password){
        try {
            statement.execute("INSERT INTO users(chatid, email, password) VALUES (" + chatid + ", " + email + ", " + password + ") ON CONFLICT (chatid) DO UPDATE SET email = '" +  email + "', password = '" + password + "';");
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static Pair<String, String> getEmailAndPassword(String chatid){
        try {
            statement.execute("SELECT * FROM users WHERE chatid = '" + chatid + "';");
            ResultSet resultSet = statement.getResultSet();
            if(resultSet.next()){
                return new Pair<>(resultSet.getString("email"), resultSet.getString("password"));
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public DataBase() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");

            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:postgresql://").  //db type
                    append("localhost:").          //host name // TODO: postrges or telegram by container name
                    append("5432/").               //port
                    append("postgres?").             //db name
                    append("user=postgres&").      //login
                    append("password=postgres");     //password*/

            connection = DriverManager.getConnection(url.toString());
            statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "    chatid     TEXT UNIQUE,\n" +
                    "    email     TEXT,\n" +
                    "    password      TEXT\n" +
                    ");");
        }
        catch (Exception e){
            throw new SQLException("Unable to connect with DataBase");
        }
    }
}

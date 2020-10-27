package APILoginREST;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

public class APILogin {

    Gson gson;
    private static final APILogin instance = new APILogin();

    private APILogin(){ gson = new Gson();}

    public static APILogin getInstance(){
        return instance;
    }

//    public void register(String name, String password) throws IOException {
//        URL url = new URL("http://localhost:8080/register");
//        HttpURLConnection con = (HttpURLConnection)url.openConnection();
//        con.setRequestMethod("POST");
//        con.setRequestProperty("Content-Type", "application/json; utf-8");
//        con.setRequestProperty("Accept", "application/json");
//        con.setDoOutput(true);
//        String jsonInputString = " {\n" +
//                "            \"name\": \"test\",\n" +
//                "                \"password\": \"password\"\n" +
//                "        }";
//        try(OutputStream os = con.getOutputStream()) {
//            byte[] input = jsonInputString.getBytes("utf-8");
//            os.write(input, 0, input.length);
//        }
//        try(BufferedReader br = new BufferedReader(
//                new InputStreamReader(con.getInputStream(), "utf-8"))) {
//            StringBuilder response = new StringBuilder();
//            String responseLine = null;
//            while ((responseLine = br.readLine()) != null) {
//                response.append(responseLine.trim());
//            }
//            System.out.println(response.toString());
//        }
//    }

//    public void register(String name, String password) throws IOException {
//        String data = "data=Hello+World!";
//        URL url = new URL("http://localhost:8084/WebListenerServer/webListener");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("POST");
//        con.setDoOutput(true);
//        con.getOutputStream().write(data.getBytes("UTF-8"));
//        con.getInputStream();
//    }

        public String register(String name, String password) throws IOException {
        URL url = new URL("http://localhost:8080/register");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
//        String jsonInputString = " {\n" +
//                "            \"name\": \"test\",\n" +
//                "                \"password\": \"password\"\n" +
//                "        }";
            LoginClass loginClass = new LoginClass();
            loginClass.setName(name);
            loginClass.setPassword(password);
            String json = gson.toJson(loginClass);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());
            return response.toString();
        }
        }

}

class LoginClass{
    public String name;
    public String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

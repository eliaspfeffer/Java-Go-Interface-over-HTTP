package com.eliaspfeffer.shared.javaGoHttpHandshake;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

/** communicates with .go files via HTTP. Need to start HTTP server via go file first */
public class startGo {
    /**Main method to start the go file
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception { 
        String methodenName = "exampleMethod";
        String[] uebergabeParameter = {"exampleParameter1", "exampleParameter2"}; // Example parameters
        startGoFile(methodenName, uebergabeParameter);
    }

    /**Responsible to starting go file without any parameter
     * Solution to either just call a methodname or call a methodname with a parameter additionally.
     * @param methodenName
     */     
    public static void startGoFile(String methodenName){
        startGoFile(methodenName, new String[0]);
    }

    /**Responsible for starting the go file with a parameter
     * @param methodenName
     * @param uebergabeParameter
     */
    public static void startGoFile(String methodenName, String[] uebergabeParameter){
        try
        {
            URL url = new URL("http://localhost:8080/execute");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Constructing the JSON object
            JSONObject json = new JSONObject();
            json.put("method", methodenName);

            if (uebergabeParameter != null && uebergabeParameter.length > 0) {
                JSONArray jsonArray = new JSONArray(Arrays.asList(uebergabeParameter));
                json.put("variable", jsonArray);
            } else {
                json.put("variable", new JSONArray());
            }

            String jsonInputString = json.toString();

            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);			
            }

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            System.out.println("Response Content: " + content.toString());
        }
        catch(Exception e){
            System.out.println("Error: " + e);
        }
    }
}

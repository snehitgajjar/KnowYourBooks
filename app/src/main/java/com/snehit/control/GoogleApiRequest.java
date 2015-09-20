package com.snehit.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;


import com.snehit.data.Books;
import com.snehit.fblogin.ListViewActivity;
import com.snehit.fblogin.MainActivity;

import org.codehaus.jackson.*;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by snehitgajjar on 9/1/15.
 */
public class GoogleApiRequest extends AsyncTask<ArrayList<Books>, Object, ArrayList<String>> {

    public static boolean isLocked=true;
    public Activity activity;
    public ArrayList<Books> listOfBooks;

    private ProgressDialog progressBar;

    public GoogleApiRequest(){

    }

    public GoogleApiRequest(ListViewActivity activity){
        this.activity=activity;
    }

    public GoogleApiRequest(MainActivity activity){
        this.activity=activity;
    }


    @Override
    protected void onPreExecute() {
        // Check network connection.
        if (isNetworkConnected() == false) {
            // Cancel request.
            Log.i(getClass().getName(), "Not connected to the internet");
            cancel(true);
            return;
        }

        progressBar = new ProgressDialog(activity);
        progressBar.setCancelable(true);

        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.show();



    }

    @Override
    protected ArrayList<String> doInBackground(ArrayList<Books>... object) {
        // Stop if cancelled
        ArrayList<String> arrayList = new ArrayList<String>();
        if (isCancelled()) {
            return null;
        }

        listOfBooks = object[0];

        for(int i=0;i<object[0].size();i++) {
            String apiUrlString = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + encodeBookTitle(object[0].get(i).getTitle());
            System.out.println("Snehit : " + apiUrlString);
            try {
                HttpURLConnection connection = null;
                // Build Connection.
                try {
                    URL url = new URL(apiUrlString);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(10000); // 5 seconds
                    connection.setConnectTimeout(15000); // 5 seconds
                    connection.setRequestProperty("User-Agent", "android");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.addRequestProperty("Content-Type", "application/json");
                } catch (MalformedURLException e) {
                    // Impossible: The only two URLs used in the app are taken from string resources.
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    // Impossible: "GET" is a perfectly valid request method.
                    e.printStackTrace();
                }
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    Log.w(getClass().getName(), "GoogleBooksAPI request failed. Response Code: " + responseCode);
                    connection.disconnect();
                    return null;
                }

                // Read data from response.
                StringBuilder builder = new StringBuilder();
                BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = responseReader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = responseReader.readLine();
                }
                String responseString = builder.toString();
                arrayList.add(responseString);
                //System.err.println("Response String: " + responseString);
                //  JSONObject responseJson = new JSONObject(responseString);
                // Close connection and return response code.
                connection.disconnect();


            } catch (SocketTimeoutException e) {
                Log.w(getClass().getName(), "Connection timed out. Returning null");
                return null;
            } catch (IOException e) {
                Log.d(getClass().getName(), "IOException when connecting to Google Books API.");
                e.printStackTrace();
                return null;
            }

        }
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<String> responseJson) {
        if (isCancelled()) {
            // Request was cancelled due to no network connection.
            //   showNetworkDialog();
        } else if (responseJson == null) {
            // showSimpleDialog(getResources().getString(R.string.dialog_null_response));
        } else {
            // All went well. Do something with your new JSONObject.


            for(int i =0;i<responseJson.size();i++) {
                try {


                    byte jacksonData[] =responseJson.get(i).getBytes(Charset.forName("UTF-8"));

                    ObjectMapper objectMapper = new ObjectMapper();


                    JsonNode  rootNode = objectMapper.readTree(jacksonData);

                    JsonNode idNode = rootNode.path("kind");

                    System.out.println("kind : " + idNode.asText());

                    JsonNode itemsNode = rootNode.get("items");

                  //  Iterator<JsonNode> iterator = itemsNode.getElements();

                    int j=0;

                   // while(iterator.hasNext() && j<1){

                        JsonNode item1 = itemsNode.path(1);
                        System.out.println("item1 : "+item1.asText());

                        JsonNode node1 = item1.path("volumeInfo");
                        System.out.println("node1 : "+node1.asText());


                    String author="";
                    for (JsonNode node : node1.path("authors")) {
                        System.out.println("Authors: "+node.toString());

                        if(j<1){
                            author += node.toString();
                        }
                        else{
                            author += ", "+node.toString();
                        }
                        j++;
                    }

                    listOfBooks.get(i).setAuthor(author);

                    j++;
                    JsonNode amount = item1.path("saleInfo").path("retailPrice").path("amount");
                    JsonNode currencyCode = item1.path("saleInfo").path("retailPrice").path("currencyCode");

                    if(amount.asText()!=null){
                        System.out.println("Price: "+currencyCode.asText()+" "+amount.asText());
                        listOfBooks.get(i).setPrice(currencyCode.asText()+" "+amount.asText());
                    }
                    else{
                        listOfBooks.get(i).setPrice("Not Available");
                    }




                 //   }






                  /*  JsonFactory jsonFactory = new MappingJsonFactory();
                    org.codehaus.jackson.JsonParser jP = jsonFactory.createJsonParser(responseJson.get(i));

                    JsonToken current;

                    current = jP.nextToken();

                    StringBuilder authors = new StringBuilder();

                    if (current != JsonToken.START_OBJECT) {
                        System.out.println("Error: root should be object: quiting.");
                        return;
                    }


                    while (jP.nextToken() != JsonToken.END_OBJECT) {

                        String fieldName = jP.getCurrentName();

                        System.out.println("Got it..... " + fieldName);


                        if (fieldName.equals("items")) {


                            while(jP.nextToken()!=JsonToken.END_OBJECT) {



                             //   jP.nextToken();
                                fieldName=jP.getCurrentName();
                                System.out.println("1..... "+fieldName);

                                if(fieldName.equals("volumeInfo")) {
                                    fieldName = jP.getCurrentName();

                                    if(fieldName.equals("authors")){
                                        jP.nextToken();
                                        fieldName = jP.getCurrentName();
                                        System.out.println("2..... "+fieldName);

                                    }

                               }

                                if(fieldName.equals("saleInfo")){

                                    System.out.println("3..... " + fieldName);
                                }

                                if (jP.getText().equals("volumeInfo")) {

                                    while (jP.nextToken() != JsonToken.END_OBJECT) {

                                        if (jP.getText().equals("authors")) {

                                            System.out.println("1..... " + jP.getText());


                                            while (jP.nextToken() != JsonToken.END_ARRAY) {
                                                if (jP.nextToken() != JsonToken.START_ARRAY) {
                                                    System.out.println("2..... " + jP.getText());
                                                    authors.append(jP.getText() + ",");
                                                    MainActivity.listOfBooks.get(i).setAuthor(authors.toString());
                                                    System.out.println("3..... " + authors.toString());
                                                }
                                            }

                                        }

                                    }


                                }




                                if(jP.getText().equals("saleInfo")){
                                    System.out.println("111..... " + jP.getText());

                                    while(jP.nextToken()!=JsonToken.END_OBJECT){

                                        if(jP.nextToken().equals("retailPrice")){

                                            String price="";
                                            while(jP.nextToken()!=JsonToken.END_OBJECT){
                                                System.out.println("11..... " + jP.getText());
                                                if(jP.getText().equals("amount")){
                                                    jP.nextToken();
                                                    System.out.println("12..... " + jP.getText());
                                                    price +=jP.getText();
                                                }
                                                else if(jP.getText().equals("currencyCode")){
                                                    jP.nextToken();
                                                    System.out.println("13..... " + jP.getText());
                                                    price+=" "+jP.getText();
                                                }

                                            }

                                            MainActivity.listOfBooks.get(i).setPrice(price);

                                        }




                                    }

                                }


                                }
                            }

                    }

                    jP.close();




                JSONObject object = new JSONObject(responseJson);
                JSONArray items = object.getJSONArray("items");

                 object = items.getJSONObject(0);

                 items = object.getJSONObject("volumeInfo").getJSONArray("authors");

                author = items.optString(0)+", ";

                for(int i=0;i<author.length();i++){
                    author += ", "+items.optString(i);
                }
                System.err.println("Authors name : "+author);
                MainActivity.listOfBooks.get(0).setAuthor(author);


                */

                  //  Thread.sleep(2000);

                    progressBar.dismiss();
                    Intent refresh = new Intent(activity, ListViewActivity.class);
                    refresh.putExtra("list", (Serializable) listOfBooks);
                    activity.startActivity(refresh);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        }
    }


    protected boolean isNetworkConnected() {

        /* Instantiate mConnectivityManager if necessary
        if(mConnectivityManager == null){
            mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        // Is device connected to the Internet?
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        } else {
            return false;
        }*/
        return true;
    }




    public String encodeBookTitle(String title){

        StringBuilder newTitle = new StringBuilder();

        for(int i=0;i<title.length();i++){

            if(title.charAt(i)==' '){
                newTitle.append("%20");
            }
            else{
                newTitle.append(title.charAt(i));
            }
        }

        return newTitle.toString();
    }






}
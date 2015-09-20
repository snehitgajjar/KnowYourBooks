package com.snehit.control;

import com.snehit.data.Books;
import com.snehit.fblogin.MainActivity;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by snehitgajjar on 8/31/15.
 */
public class LocalJsonParser {



    public ArrayList<Books> parseBookInfo(JSONObject object){

        ArrayList<Books> set = new ArrayList<Books>();

        try {
            JSONArray books = object.getJSONArray("data");

            Books book;

            JSONObject bookObj,dataInBooks,dataInBooks1;
            for(int i=0;i<books.length();i++){
                dataInBooks = books.getJSONObject(i);
                bookObj = dataInBooks.getJSONObject("data");
                dataInBooks = bookObj.getJSONObject("book");
                System.err.println("object: "+object+" title : "+dataInBooks.optString("title") +
                                "url : "+dataInBooks.optString("id"));
                book = new Books();book.setTitle(dataInBooks.optString("title"));
                book.setUrl(dataInBooks.optString("id"));
                set.add(book);
            }



        } catch (JSONException e) {
            e.printStackTrace();

        }


        return set;
    }








    public String parseBookInfoFromGoogle(String object){
        StringBuilder authors = new StringBuilder();

        try {

            JsonFactory jsonFactory = new MappingJsonFactory();
            org.codehaus.jackson.JsonParser jP = jsonFactory.createJsonParser(object);

            JsonToken current;

            current = jP.nextToken();



            if (current != JsonToken.START_OBJECT) {
                System.out.println("Error: root should be object: quiting.");
                return "";
            }


            while(jP.nextToken() != JsonToken.END_OBJECT){

                String fieldName = jP.getCurrentName();

                System.out.println("Got it..... "+fieldName);


                if(fieldName.equals("items")){


                    while(jP.nextToken()!=JsonToken.END_ARRAY){



                        if(jP.getText().equals("volumeInfo")){

                            while(jP.nextToken()!=JsonToken.END_OBJECT){

                                if(jP.getText().equals("authors")){

                                    System.out.println("1..... "+jP.getText());

                                    while(jP.nextToken()!=JsonToken.END_ARRAY) {
                                        if(jP.nextToken()!=JsonToken.START_ARRAY) {
                                            System.out.println("2..... " + jP.getText());
                                            authors.append(jP.getText() + ",");
                                           // MainActivity.listOfBooks.get(0).setAuthor(authors.toString());
                                            System.out.println("3..... " + authors.toString());
                                        }
                                    }

                                }

                            }


                        }


                    }



                }

            }






        } catch (IOException e) {
            e.printStackTrace();
        }

        return authors.toString();
    }










}

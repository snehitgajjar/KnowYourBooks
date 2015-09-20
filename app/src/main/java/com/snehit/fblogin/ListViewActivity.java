package com.snehit.fblogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.snehit.control.GoogleApiRequest;
import com.snehit.control.LocalJsonParser;
import com.snehit.data.Books;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by snehitgajjar on 8/31/15.
 */
public class ListViewActivity extends ActionBarActivity {

    // XML node keys
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "artist";
    static final String KEY_DURATION = "duration";
    static final String KEY_THUMB_URL = "thumb_url";

    ListView list;
    ListViewAdapter adapter;
    ArrayList<Books> listOfBooks;
    ImageLoader imageLoader;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_view_adapter);

        listOfBooks = (ArrayList<Books>) getIntent().getSerializableExtra("list");
        imageLoader = new ImageLoader(getApplicationContext());
        ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();







       /* JsonParser jsonParser=new JsonParser();
        String author = jsonParser.parseBookInfoFromGoogle(jsonObject);

        listOfBooks.get(0).setAuthor(author);
        */





        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ID, 0+"");
        map.put(KEY_TITLE, "Alchemist");
        map.put(KEY_ARTIST, "Paul Ceohlo");
        map.put(KEY_DURATION, "$3.12");
        map.put(KEY_THUMB_URL, "temp");
        mapList.add(map);

        if(listOfBooks!=null) {
            for (Books book : listOfBooks) {
                map = new HashMap<String, String>();
                map.put(KEY_ID, 1 + "");
                map.put(KEY_TITLE, book.getTitle());
                map.put(KEY_ARTIST, book.getAuthor());
                map.put(KEY_DURATION, book.getPrice());
                map.put(KEY_THUMB_URL, book.getUrl());
                mapList.add(map);
            }
        }

        list = (ListView) findViewById(R.id.list);

        adapter=new ListViewAdapter(this, mapList);

        list.setAdapter(adapter);

        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_view_title_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:





                System.out.println("Hey I clicked on search....");
                new GraphRequest(
                        MainActivity.accessToken,
                        "/me/books.wants_to_read",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {



                                // info.setText(response.toString());
                                LocalJsonParser jsonParser = new LocalJsonParser();
                               ArrayList<Books> newlistOfBooks = jsonParser.parseBookInfo(response.getJSONObject());


                                String jsonObject = null;
                                GoogleApiRequest googleValue = new GoogleApiRequest(ListViewActivity.this);


                                googleValue.execute(newlistOfBooks);


                                //   Thread.sleep(7000);


                                //Intent refresh = new Intent(getApplicationContext(), ListViewActivity.class);
                                //startActivity(refresh);

                            }
                        }
                ).executeAsync();





                return true;
            case R.id.action_settings:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }











}

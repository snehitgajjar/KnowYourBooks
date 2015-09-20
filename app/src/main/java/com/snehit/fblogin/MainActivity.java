package com.snehit.fblogin;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.snehit.control.GoogleApiRequest;
import com.snehit.control.LocalJsonParser;
import com.snehit.data.Books;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
/*
public class MainActivity extends FragmentActivity {

    private TextView info;
    private LoginButton loginButton;


    private MainFragment mainFragment;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null) {
            mainFragment = new MainFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        }else {
            mainFragment =(MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }






    }


*/

public class MainActivity extends Activity {

    private TextView info;
    private LoginButton loginButton;
    CallbackManager callbackManager;
    private Button button;
    public  ArrayList<Books> listOfBooks;
    private LocalJsonParser jsonParser;
    public static AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        button = (Button)findViewById(R.id.nextActivity);
        button.setEnabled(false);



        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_actions.books"));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
                intent.putExtra("list", (Serializable) listOfBooks);

                startActivity(intent);

            }
        });



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                accessToken = loginResult.getAccessToken();

                new GraphRequest(
                        loginResult.getAccessToken(),
                        "/me/books.wants_to_read",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {

                                // info.setText(response.toString());
                                jsonParser = new LocalJsonParser();
                                listOfBooks = jsonParser.parseBookInfo(response.getJSONObject());



                                button.setEnabled(true);

                                String jsonObject = null;
                                GoogleApiRequest googleValue = new GoogleApiRequest(MainActivity.this);


                                googleValue.execute(listOfBooks);


                            }
                        }
                ).executeAsync();




            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });









    }





    @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}

package com.example.stackoverflowsearch.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends Activity {

    private EditText queryEditText;
    private Button searchButton;
    private String commonURL="http://api.stackexchange.com/2.2/search/?order=desc&sort=votes&filter=default&site=stackoverflow&intitle=";
    private String query;

    private String queryURL;

    private static final String AUTHOR = "display_name";
    private static final String SCORE = "score";
    private static final String TITLE = "title";
    private static final String QUESTION_ID = "question_id";
    JSONObject queryResult = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = (EditText) findViewById(R.id.queryEditText);
        searchButton =(Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(getQueryURL);

    }

    View.OnClickListener getQueryURL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            query = queryEditText.getText().toString();

            try {
                queryURL = commonURL + URLEncoder.encode(query, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            new JSONParserAsyncTask().execute(queryURL);
        }
    };

    public void getJSONData(String json)
    {
            try {
                //
                //
                queryResult = new JSONObject(json);
                JSONArray questions =  queryResult.getJSONArray("items");
                for(int i=0;i<questions.length();i++) {

                    JSONObject c = questions.getJSONObject(i);
                    JSONObject owner = c.getJSONObject("owner");
                    Log.d("title",c.getString(TITLE));
                    Log.d("score",c.getString(SCORE));
                    Log.d("name",owner.getString(AUTHOR));
                    Log.d("question id",c.getString(QUESTION_ID));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



    }

    class JSONParserAsyncTask extends AsyncTask<String,String,Void>
    {
        JSONObject jsonObject;
        String json = "";

        @Override
        protected Void doInBackground(String... params) {
            try {

                URL url = new URL(queryURL);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder stringBuilder = new StringBuilder();

                int read;
                char[] chars = new char[1024];
                while ((read = bufferedReader.read(chars)) != -1)
                    stringBuilder.append(chars, 0, read);

                    Log.d("line", stringBuilder.toString());

                json=stringBuilder.toString();
                getJSONData(json);

            }
            catch (Exception e ) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v){
            try{
                 jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

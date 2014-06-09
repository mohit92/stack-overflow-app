package com.example.stackoverflowsearch.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;


public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper> {

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

    private String author;
    private String title;
    private int query_id;
    private int question_id;
    private int score;

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
                feedIntoDb(json);

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

    public void feedIntoDb(String json)
    {
        try {
                RuntimeExceptionDao<QueryData,Integer> queryDao = getHelper().getQueryRuntimeDao();
                final RuntimeExceptionDao<QuestionData,Integer> questionDao = getHelper().getQuestionRuntimeDao();

                final QueryData queryData = new QueryData(query);
                queryDao.create(queryData);

                queryResult = new JSONObject(json);
                JSONArray questions =  queryResult.getJSONArray("items");
                for(int i=0;i<questions.length();i++) {

                    JSONObject c = questions.getJSONObject(i);
                    JSONObject owner = c.getJSONObject("owner");
                    title=c.getString(TITLE);
                    score=Integer.parseInt(c.getString(SCORE));
                    author=owner.getString(AUTHOR);
                    question_id=Integer.parseInt(c.getString(QUESTION_ID));

                    QuestionData questionData = new QuestionData(question_id, queryData, score, author, title);
                    questionDao.create(questionData);



                }


            QueryBuilder<QuestionData, Integer> statementBuilder = questionDao.queryBuilder();
            statementBuilder.where().eq(QuestionData.QUERY_ID_FIELD_NAME, queryData);
            final List<QuestionData> list = questionDao.query(statementBuilder.prepare());

            displayQueryResults(list);



            /*List<QuestionData> list = questionDao.queryForAll();
            StringBuilder sb = new StringBuilder();
            sb.append("got ").append(list.size()).append(" entries in ").append("Question").append("\n");

            // if we already have items in the database
            int questionC = 0;
            for (QuestionData question : list) {
                sb.append("------------------------------------------\n");
                sb.append("[").append(questionC).append("] = ").append(question.getQuestionId()+"  "+question.getScore()+"   "+question.getQueryId()+"  "+question.getQueryId() + "   "+question.getTitle()).append("\n");
                questionC++;
            }
            sb.append("------------------------------------------\n");
            Log.d("fetch",sb.toString());

            List<QueryData> queryList = queryDao.queryForAll();
            StringBuilder qsb = new StringBuilder();
            qsb.append("got ").append(queryList.size()).append(" entries in ").append("Queries").append("\n");

            // if we already have items in the database
            int queryC = 0;
            for (QueryData queries : queryList) {
                qsb.append("------------------------------------------\n");
                qsb.append("[").append(queryC).append("] = ").append(queries.getId()+"  "+queries.getQuery()).append("\n");
                queryC++;
            }
            qsb.append("------------------------------------------\n");
            Log.d("fetch",qsb.toString());

        */


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void displayQueryResults(List<QuestionData> list ) {

        final ListView listView = (ListView) findViewById(R.id.questionListView);
        final CustomAdapter customAdapter = new CustomAdapter(MainActivity.this,list);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(customAdapter);
            }
        });
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

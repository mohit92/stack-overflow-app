package com.example.stackoverflowsearch.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
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


public class MainActivity extends OrmLiteBaseActivity<DatabaseHelper>  {

    private EditText queryEditText;
    private Button searchButton;
    private String commonURL="http://api.stackexchange.com/2.2/search/?order=desc&sort=votes&filter=default&site=stackoverflow";
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

    private RuntimeExceptionDao<QueryData,Integer> queryDao;
    private RuntimeExceptionDao<QuestionData,Integer> questionDao;

    private ListView listView;
    private CustomAdapter customAdapter;



    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        queryEditText = (EditText) findViewById(R.id.queryEditText);
        searchButton =(Button) findViewById(R.id.searchButton);
        searchButton.setOnClickListener(getQueryURL);

    }

    View.OnClickListener getQueryURL = new View.OnClickListener() {


        @Override
        public void onClick(View v) {

            hideKeyBoard();
            query = queryEditText.getText().toString();

            if (query != null && !query.isEmpty()) {

                try {
                    queryURL = commonURL +"&intitle="+URLEncoder.encode(query, "UTF-8");


                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if (isNetworkAvailable()) {

                    try {
                        //delete older entries for the same query from the database
                        deleteOlderQueryResults(query);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }

                    //feed new questions parsed from internet into the database
                    new JSONParserAsyncTask().execute(queryURL);

                }
                else {

                    try {
                        offlineResultsInDatabase(query);

                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
            else {
                resetListView();
                showToast("query can not be empty");
            }
        }

    };

    private void offlineResultsInDatabase(String query) throws SQLException {
        queryDao = getHelper().getQueryRuntimeDao();
        questionDao = getHelper().getQuestionRuntimeDao();

        //find the query_id in QueryData for the user inputted query
        QueryBuilder<QueryData,Integer> queryQb = queryDao.queryBuilder();
        List<QueryData> resultQueryDataList=queryQb.where().like(QueryData.QUERY_FIELD_NAME,"%"+query+"%").query();

        if(resultQueryDataList.isEmpty()) {
            resetListView();
            showToast("No offline results in database");
        }
        else {
            //store questions corresponding to query_id
            QueryBuilder<QuestionData,Integer> questionQb = questionDao.queryBuilder();
            List<QuestionData> questionResults = questionQb.join(queryQb).query();
            displayQueryResults(questionResults);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnected();
    }

    private void deleteOlderQueryResults(String query) throws SQLException {

        queryDao = getHelper().getQueryRuntimeDao();
        questionDao = getHelper().getQuestionRuntimeDao();

        //find query_id in QueryData for the query
        QueryBuilder<QueryData,Integer> queryQb = queryDao.queryBuilder();
        queryQb.where().eq(QueryData.QUERY_FIELD_NAME,query);

        //delete all the questions in QuestionData corresponding to query_id
        DeleteBuilder<QuestionData,Integer> questionDb = questionDao.deleteBuilder();
        questionDb.where().in(QuestionData.QUERY_ID_FIELD_NAME,queryQb.query());
        questionDb.delete();

        //delete query from the QueryData
        DeleteBuilder<QueryData,Integer> queryDb = queryDao.deleteBuilder();
        queryDb.where().eq(QueryData.QUERY_FIELD_NAME,query);
        queryDb.delete();



    }



    class JSONParserAsyncTask extends AsyncTask<String,String,Void>
    {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llProgressBar);
        JSONObject jsonObject;
        String json = "";
        @Override
        protected void onPreExecute() {
            linearLayout.setVisibility(View.VISIBLE);
        }
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

            linearLayout.setVisibility(View.INVISIBLE);

        }
    }

    public void feedIntoDb(String json)
    {
        try {
                queryResult = new JSONObject(json);
                JSONArray questions =  queryResult.getJSONArray("items");

                if(questions.length()>0) {

                    queryDao = getHelper().getQueryRuntimeDao();
                    questionDao = getHelper().getQuestionRuntimeDao();
                    QueryData queryData;

                    queryData = new QueryData(query);
                    queryDao.create(queryData);


                    for (int i = 0; i < questions.length(); i++) {

                        JSONObject c = questions.getJSONObject(i);
                        JSONObject owner = c.getJSONObject("owner");
                        title = c.getString(TITLE);
                        score = Integer.parseInt(c.getString(SCORE));
                        author = owner.getString(AUTHOR);
                        question_id = Integer.parseInt(c.getString(QUESTION_ID));

                        QuestionData questionData = new QuestionData(question_id, queryData, score, author, title);
                        questionDao.create(questionData);
                    }

                    QueryBuilder<QuestionData, Integer> statementBuilder = questionDao.queryBuilder();
                    statementBuilder.where().eq(QuestionData.QUERY_ID_FIELD_NAME, queryData);
                    List<QuestionData> list = questionDao.query(statementBuilder.prepare());

                    displayQueryResults(list);
                }
                else {

                    resetListView();
                    showToast("No results found, please enter a relevent query");
                }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void displayQueryResults(List<QuestionData> list ) {

        listView = (ListView) findViewById(R.id.questionListView);
        customAdapter = new CustomAdapter(MainActivity.this,list);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(customAdapter);

            }
        });
    }

    private void showToast(final String string) {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
            }

        });
    }

    private void resetListView() {
        listView = (ListView) findViewById(R.id.questionListView);
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                listView.setAdapter(null);
            }
        });

    }
    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(queryEditText.getWindowToken(), 0);
    }

    /*private void updateQueryURL() throws UnsupportedEncodingException {
        queryURL = commonURL +"&page="+pageNo+"&pagesize="+pageSize+"&intitle="+URLEncoder.encode(query, "UTF-8");
    }
*/

}

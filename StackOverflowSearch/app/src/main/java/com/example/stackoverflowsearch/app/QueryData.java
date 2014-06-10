package com.example.stackoverflowsearch.app;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mohit on 6/9/14.
 */
@DatabaseTable
public class QueryData {
    public static final String QUERY_FIELD_NAME="query";
    public static final String ID_FIELD_NAME="id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField()
    private String query;



    QueryData() {

    }

    QueryData(String query) {
        this.query=query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    public String getQuery() {
        return query;
    }

    public int getId() {
        return id;
    }


}

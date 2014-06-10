package com.example.stackoverflowsearch.app;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mohit on 6/9/14.
 */
@DatabaseTable(tableName = "questions")

public class QuestionData {
    public static final String QUERY_ID_FIELD_NAME = "query_id_id";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(id = false)
    private int question_id;

    @DatabaseField(canBeNull = false, foreign = true, columnName = QUERY_ID_FIELD_NAME)
    private QueryData query_id;

    @DatabaseField
    private int score;

    @DatabaseField
    private String author;

    @DatabaseField
    private String title;

    QuestionData() {

    }

    public QuestionData(int question_id,QueryData query_id, int score, String author, String title) {
        this.question_id = question_id;
        this.query_id = query_id;
        this.score = score;
        this.author = author;
        this.title = title;
    }

    public int getId() {return id;}
    public void setQuestionId(int question_id) {
        this.question_id=question_id;
    }
    public int getQuestionId() {
        return question_id;
    }

    public void setQueryId(QueryData queryData) {
        this.query_id =queryData;
    }
    public QueryData getQueryId() {
        return query_id;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public int getScore() {
        return score;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public String getAuthor() {
        return author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}

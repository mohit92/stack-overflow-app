package com.example.stackoverflowsearch.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mohit on 6/8/14.
 */

public class CustomAdapter extends ArrayAdapter {

    private final Activity activity;
    private final List list;
    //private Context context;
    public CustomAdapter(Activity activity, List<QuestionData> list){
        super(activity,R.layout.questions_layout,list);
        this.activity = activity;
        this.list = list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;

        if(rowView==null) {
            LayoutInflater layoutInflater = activity.getLayoutInflater();
            rowView = layoutInflater.inflate(R.layout.questions_layout,null);

            viewHolder = new ViewHolder();
            viewHolder.score = (TextView) rowView.findViewById(R.id.scoreTextView);
            viewHolder.title = (TextView) rowView.findViewById(R.id.titleTextView);
            viewHolder.author = (TextView) rowView.findViewById(R.id.authorTextView);
            rowView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder)rowView.getTag();
        }

        QuestionData item = (QuestionData) list.get(position);
        viewHolder.score.setText((Integer.toString(item.getScore())));
        viewHolder.title.setText(item.getTitle().toString());
        viewHolder.author.setText(item.getAuthor().toString());
        return rowView;
    }

    static class ViewHolder {
        TextView score;
        TextView title;
        TextView author;
    }
}

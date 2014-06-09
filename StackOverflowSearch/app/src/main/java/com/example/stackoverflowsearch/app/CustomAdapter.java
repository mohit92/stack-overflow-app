package com.example.stackoverflowsearch.app;

/**
 * Created by mohit on 6/8/14.
 */
/*
public class CustomAdapter extends ArrayAdapter {

    private final Activity activity;
    private final List list;

    public CustomAdapter(Activity activity, ArrayList<Question> list){
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

        Question item = list.get(position);
        viewHolder.score.setText(item.getScore().toString());
        viewHolder.title.setText(item.getTitle().toString());
        viewHolder.author.setText(item.getAuthor().toString());
    }

    static class ViewHolder {
        TextView score;
        TextView title;
        TextView author;
    }
}
*/
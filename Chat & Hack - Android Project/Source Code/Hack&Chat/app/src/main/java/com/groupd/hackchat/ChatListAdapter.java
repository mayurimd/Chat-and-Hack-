package com.groupd.hackchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.


 /*

This is an adapter class holding the Name/Emails details for users that is listed in the ChatListActivity class.

 */

public class ChatListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> ListName;
    private ArrayList<String> ListEmail;


    Typeface font;



    public ChatListAdapter(Activity activity, ArrayList<String> ListName, ArrayList<String> ListEmail) {

        this.activity = activity;
        this.ListName = ListName;
        this.ListEmail = ListEmail;

        /*
               Font type for text
       */

        font = Typeface.createFromAsset(activity.getAssets(), "BakersfieldBold.ttf");



    }

    @Override
    public int getCount() {
        return ListName.size();
    }

    @Override
    public Object getItem(int location) {
        return ListName.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);



        TextView ListNameTextView = (TextView) convertView.findViewById(R.id.NameID);
        TextView ListEmailTextView = (TextView) convertView.findViewById(R.id.EmailID);



        ListNameTextView.setTypeface(font);
        ListEmailTextView.setTypeface(font);



        ListNameTextView.setText(ListName.get(position));
        ListEmailTextView.setText("(" + ListEmail.get(position) + ")");



        return convertView;
    }




}
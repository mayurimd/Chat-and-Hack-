package com.groupd.hackchat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// This source code is developed by Group D, M.Sc Applied Computer Science (IT-Security) 2016.


/*

This is an adapter class holding the Chat conversation between users that is listed in the PrivateChatActivity class.

 */


public class ConversationListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<String> conversationList;
    private ArrayList<Bitmap> profilePhotoList;

    Typeface font;



    public ConversationListAdapter(Activity activity, ArrayList<String> conversationList, ArrayList<Bitmap> profilePhotoList) {

        this.activity = activity;
        this.conversationList = conversationList;
        this.profilePhotoList = profilePhotoList;

        /*
               Font type for text
       */

        font = Typeface.createFromAsset(activity.getAssets(), "BakersfieldBold.ttf");



    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public Object getItem(int location) {
        return conversationList.get(location);
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
            convertView = inflater.inflate(R.layout.chat_message_row, null);


        TextView chatTextMessage = (TextView) convertView.findViewById(R.id.TextMessageID);
        ImageView chatProfilePhoto = (ImageView) convertView.findViewById(R.id.ProfilePhotoID);



        chatTextMessage.setTypeface(font);


        chatTextMessage.setText(conversationList.get(position));
        chatProfilePhoto.setImageBitmap(profilePhotoList.get(position));


        return convertView;
    }




}

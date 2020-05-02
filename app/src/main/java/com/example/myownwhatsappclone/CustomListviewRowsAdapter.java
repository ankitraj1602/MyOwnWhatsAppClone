package com.example.myownwhatsappclone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CustomListviewRowsAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<HashMap<String,String>> infoList;
    private Timer timer;



    public CustomListviewRowsAdapter(Context context, ArrayList<HashMap<String,String>> list) {
        super(context, R.layout.layout_for_chats_row,list);
        this.infoList = list;
        this.context=context;

    }


    static class ViewHolder{
        TextView textName;
        TextView textUserName;
        ImageView imgPropic;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(timer != null){timer.cancel();
            //Toast.makeText(context, "Timer cancelled", Toast.LENGTH_SHORT).show();
        }
        final View result;
        final ViewHolder viewHolder;
        HashMap<String,String> data = (HashMap<String, String>) getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_for_chats_row,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.textName = (TextView)convertView.findViewById(R.id.rowProperName);
            viewHolder.textUserName = (TextView)convertView.findViewById(R.id.rowUserName);
            viewHolder.imgPropic = (ImageView) convertView.findViewById(R.id.rowImg);

            result = convertView;
            convertView.setTag(viewHolder);




        }

        else{
            //by getting tag and since it was set in the form of a viweholder as we
            // can set a object as the tag,we can get the viewholder.And that way
            //we don't have to use findViewById everytime.And it becomes faster

            viewHolder = (ViewHolder)convertView.getTag();
            result = convertView;
        }

        String propname = data.get("proper_name");
        final String username = data.get("username");

//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                ParseQuery<ParseUser> query= ParseUser.getQuery();
//                query.whereEqualTo("username",username);
//                //query.whereExists("picture");
//                query.getFirstInBackground(new GetCallback<ParseUser>() {
//                    @Override
//                    public void done(ParseUser object, ParseException e) {
//                        ParseFile file = (ParseFile) object.get("picture");
//                        file.getDataInBackground(new GetDataCallback() {
//                            @Override
//                            public void done(byte[] data, ParseException e) {
//                                if(e==null & data!=null){
//                                    Bitmap img = BitmapFactory.decodeByteArray(data,0,data.length);
//                                    viewHolder.imgPropic.setImageBitmap(img);
//                                }
//                            }
//                        });
//
//                    }
//                });
//
//            }
//        },0);
        //timer.cancel();
        ParseQuery<ParseUser> query= ParseUser.getQuery();
        query.whereEqualTo("username",username);
        query.whereExists("picture");
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                ParseFile file = (ParseFile) object.get("picture");
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if(e==null & data!=null){
                            Bitmap img = BitmapFactory.decodeByteArray(data,0,data.length);
                            viewHolder.imgPropic.setImageBitmap(img);
                        }
                    }
                });

            }
        });

        //I was testing without this, as to see, how it happens when i do not implement
        //the viewholder. And thats why, i have to find finaViewById everything
        //and therefore it is slow.
//        ( (TextView)convertView.findViewById(R.id.rowProperName)).setText(propname);
//        ( (TextView)convertView.findViewById(R.id.rowUserName)).setText(username);


        viewHolder.textName.setText(propname);
        viewHolder.textUserName.setText(username);

        return convertView;

    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}

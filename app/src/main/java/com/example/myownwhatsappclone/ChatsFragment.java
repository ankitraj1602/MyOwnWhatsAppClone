package com.example.myownwhatsappclone;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment implements ListView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<HashMap<String,String>> data;
    private ArrayList<String> userNameArray;

    private ListView listView;
    private CustomListviewRowsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view= inflater.inflate(R.layout.fragment_chats, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        listView = view.findViewById(R.id.listviewForChats);
        listView.setOnItemClickListener(this);
        data = new ArrayList<HashMap<String, String>>();
        userNameArray = new ArrayList<>();


        //String[] queryConstraints = {"username,name"};
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());


        //query.selectKeys(Arrays.asList(queryConstraints));


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size()>0 && e==null){

                    for(ParseUser user : objects){
                    HashMap<String,String> userdata = new HashMap<String, String>();
                    userdata.put("username",user.getUsername());

                    //I did this to simplify refresh feature to update,so to compare usernames with new usernames
                    userNameArray.add(userdata.get("username"));

                    userdata.put("proper_name",user.get("name").toString());
                    data.add(userdata);

                }

                    adapter = new CustomListviewRowsAdapter(getContext(),data);
                    listView.setAdapter(adapter);

                }

                else{
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(getContext(),IndividualChatListBars.class);
        HashMap<String,String> info = data.get(position);
        intent.putExtra("username",info.get("username"));
        intent.putExtra("proper_name",info.get("proper_name"));
        startActivity(intent);

    }

    @Override
    public void onRefresh() {
        Toast.makeText(getContext(), "Refresh Ok!", Toast.LENGTH_SHORT).show();

        final ArrayList<HashMap<String,String>> dataRefresh =
                new ArrayList<HashMap<String, String>>();

        final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username",ParseUser.getCurrentUser().getUsername());
        query.whereNotContainedIn("username",userNameArray);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(objects.size()>0 && e==null){

                    for(ParseUser user : objects){
                        HashMap<String,String> userdata = new HashMap<String, String>();
                        userdata.put("username",user.getUsername());
                        userdata.put("proper_name",user.get("name").toString());
                        data.add(userdata);
                    }

                    adapter.notifyDataSetChanged();
                    if(swipeRefreshLayout.isRefreshing()==true){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                else{
                    if(swipeRefreshLayout.isRefreshing()==true){
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });

    }







    }

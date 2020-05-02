package com.example.myownwhatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class loggedInUser extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_user);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        Adapter adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager,true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(
                loggedInUser.this);
        menuInflater.inflate(R.menu.menu_logged_in_user,menu);
        return true;

        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutItem:
                ParseUser.getCurrentUser().logOutInBackground(
                        new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Intent intentToLogout = new Intent(loggedInUser.this,MainActivity.class);
                            startActivity(intentToLogout);
                            finish();
                        }
                        else{
                            Toast.makeText(loggedInUser.this,
                 "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

            case R.id.updateProfileItem:
                Intent intent = new Intent(this,Profile.class);
                intent.putExtra("username",ParseUser.getCurrentUser().getUsername());
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.egyeso.chatfy.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.egyeso.chatfy.Fragment.SelectionPagerAdapter;
import com.egyeso.chatfy.R;
import com.egyeso.chatfy.StartActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ViewPager mViewPager;
    SelectionPagerAdapter selectionPagerAdapter;
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mViewPager = findViewById(R.id.main_tabPager);
        selectionPagerAdapter = new SelectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(selectionPagerAdapter);
        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId() == R.id.main_logout){
             FirebaseAuth.getInstance().signOut();
             startActivity(new Intent(MainActivity.this, StartActivity.class));
             finish();
         }
         if (item.getItemId() == R.id.main_setting){
             startActivity(new Intent(MainActivity.this, SettingsActivity.class));
         }
         if (item.getItemId() == R.id.main_alluser){
             startActivity(new Intent(MainActivity.this, UsersActivity.class));
         }

        return true;
    }
}

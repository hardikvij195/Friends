package com.hvtechnologies.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.hvtechnologies.friends.MyFragmentPagerAdapter;

public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mauth;
    ViewPager mviewPager;
    TabLayout mtabLayout;
    DatabaseReference mDatabaseReference;
    MyFragmentPagerAdapter mFragmentPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mauth = FirebaseAuth.getInstance();
        mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("users");


        mviewPager=(ViewPager)findViewById(R.id.viewPager);

        //---ADDING ADAPTER FOR FRAGMENTS IN VIEW PAGER----
        mFragmentPagerAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mFragmentPagerAdapter);

        //---SETTING TAB LAYOUT WITH VIEW PAGER
        mtabLayout=(TabLayout)findViewById(R.id.tabLayout);
        mtabLayout.setupWithViewPager(mviewPager);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.settings){
            Intent intent=new Intent(HomeActivity.this,SettingActivity.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.allUsers){
            Intent intent=new Intent(HomeActivity.this,UserActivity.class);
            startActivity(intent);
        }

        //---LOGGING OUT AND ADDING TIME_STAMP----
        if(item.getItemId()==R.id.logout){
            mDatabaseReference.child(mauth.getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        FirebaseAuth.getInstance().signOut();
                        startfn();
                    }
                    else{
                        Toast.makeText(HomeActivity.this, "Try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mauth.getCurrentUser();

        if(user==null){

            startfn();

        }
        else{

            mDatabaseReference.child(user.getUid()).child("online").setValue("true");

        }
    
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
        builder.setMessage("Really Exit ??");
        builder.setTitle("Exit");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok",new MyListener());
        builder.setNegativeButton("Cancel",null);
        builder.show();

    }
    public class MyListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            finish();
        }
    }
    private void startfn(){
        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

}

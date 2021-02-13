package com.example.sdsd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DecorToolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextView address_view, userName, userEmail, userPoint;
    String gu = "", dong="", name="", email="";
    public static final int REQUEST_CODE_MENU = 101;
    private static final String TAG = "MainActivity";

    Button logButton, pointButton;
    Double point;
    View header;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logButton = (Button)findViewById(R.id.logoutButton);
        pointButton = (Button)findViewById(R.id.testButton);

        address_view = findViewById(R.id.textLocation);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.userName);
        userEmail = (TextView) header.findViewById(R.id.userEmail);
        userPoint = (TextView) header.findViewById(R.id.userPoint);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();
                String title = item.getTitle().toString();

                if(id == R.id.store){
                    myStartActivity(NoPageActivity.class);
                }
                else if(id == R.id.comunity || id == R.id.event || id == R.id.qna){
                    myStartActivity(NoPageActivity.class);
                }
                return true;
            }
        });


        if(user == null){
            logButton.setText("로그인");

            //myStartActivity(SignUpActivity.class);
        }else{
            logButton.setText("로그아웃");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if(document != null){
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                name = document.getString("name");
                                userName.setText(name);
                                email = user.getEmail();
                                userEmail.setText(email);
                                point = document.getDouble("point");
                                userPoint.setText(point.toString());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        address_view.setText(preferences.getString("address", "위치를 설정해주세요."));
        gu = preferences.getString("gu", "");
        dong = preferences.getString("dong", "");

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.SearchButton).setOnClickListener(onClickListener);
        findViewById(R.id.locButton).setOnClickListener(onClickListener);
        findViewById(R.id.carTimeButton).setOnClickListener(onClickListener);
        findViewById(R.id.infoButton).setOnClickListener(onClickListener);

        findViewById(R.id.menuButton).setOnClickListener(onClickListener);
        findViewById(R.id.testButton).setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.SearchButton:
                    myStartActivity(SearchActivity.class);
                    break;
                case R.id.locButton:
                    Intent intent = new Intent(MainActivity.this, LocActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_MENU);
                    break;
                case R.id.carTimeButton:
                    intent = new Intent(MainActivity.this, AlarmActivity.class);
                    intent.putExtra("guText", gu);
                    intent.putExtra("dongText", dong);
                    startActivity(intent);
                    break;
                case R.id.infoButton:
                    myStartActivity(RecycleInfoActivity.class);
                    break;
                case R.id.logoutButton:
                    if(logButton.getText()=="로그인"){
                        myStartActivity(SignUpActivity.class);
                    }
                    else{
                        FirebaseAuth.getInstance().signOut();
                        myStartActivity(MainActivity.class);
                        finish();
                    }
                    break;
                case R.id.menuButton:
                    if(logButton.getText()=="로그인"){
                        myStartActivity(SignUpActivity.class);
                    }
                    else{
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                    break;
                case R.id.testButton:
                    if(logButton.getText()=="로그인"){
                        myStartActivity(SignUpActivity.class);
                    }
                    else{
                        Toast.makeText(MainActivity.this,"정답입니다. 포인트가 적립되었습니다.("+ point +"+3)",Toast.LENGTH_LONG).show();
                        pointUpdate();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_MENU){
            Toast.makeText(getApplicationContext(), "위치를 설정하지 못했습니다.", Toast.LENGTH_LONG).show();
        }
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "위치를 설정했습니다.", Toast.LENGTH_LONG).show();
            int count = 0;
            String address = data.getStringExtra("addText");
            for (int i = 0; i < address.length(); i++){
                char ch = address.charAt(i);
                if(Character.isWhitespace(ch) && count == 0){
                    address = address.substring(i+1);
                    count++;
                }
                else if(Character.isWhitespace(ch) && count == 1){ count++; }
                else if(count == 2){
                    if(!Character.isWhitespace(ch)){ gu += ch; }
                    else{ count++; }
                }
                else if(count == 3){
                    if(!Character.isWhitespace(ch)){ dong += ch; }
                    else{break;}
                }
            }
            editor.putString("address", address);
            editor.putString("gu", gu);
            editor.putString("dong", dong);
            editor.apply();
            address_view.setText(address);
        }
    }

    private void pointUpdate(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        point += 3;
        docRef.update("point", point);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            name = document.getString("name");
                            point = document.getDouble("point");
                            userPoint.setText(point.toString());
                        } else {
                            Log.d(TAG, "No such document");
                            myStartActivity(MemberActivity.class);
                        }
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

}
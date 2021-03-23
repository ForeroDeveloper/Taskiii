package com.fordev.taski;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.fordev.taski.otros.SliderAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserDashboard extends AppCompatActivity {


    ViewPager viewPager;
    LinearLayout mDotsLayout;
    TextView[] mDots;
    SliderAdapter sliderAdapter;
    int currentPos;

    RelativeLayout btnSigIn;
    GoogleSignInClient signInClient;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String primerLogin;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    final Calendar Cal = Calendar.getInstance();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dashboard);

        //Vieopager
        viewPager = findViewById(R.id.slider);
        mDotsLayout = findViewById(R.id.dots);


        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(changeListener);


    }

    public void LoginClick(View view) {
        Intent intent = new Intent(UserDashboard.this, LoginScreen.class);
        startActivity(intent);
    }


    ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPos = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void next(View view) {
        viewPager.setCurrentItem(currentPos + 1);
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[4];
        mDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(45);

            mDotsLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void googleLogin(View view) {
        Intent intent = new Intent(UserDashboard.this, SignUpScreen.class);
        startActivity(intent);
    }
}
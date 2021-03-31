package com.fordev.taski;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.shashank.sony.fancytoastlib.FancyToast;

public class CalificarAppTaski extends AppCompatActivity {

    TextView resultStar;
    RatingBar rateStar;
    ImageView charPlace;
    MaterialButton calificar;
    Animation charAnimation;
    String answerValue = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calificar_app_taski);
        rateStar = findViewById(R.id.rateStars);
        resultStar = findViewById(R.id.resultRate);
        charPlace = findViewById(R.id.charPlace);
        calificar = findViewById(R.id.calificar_btn);

        charAnimation = AnimationUtils.loadAnimation(this, R.anim.calificar_app_scale_img);
        charPlace.startAnimation(charAnimation);


        rateStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                answerValue = String.valueOf((int) (rateStar.getRating()));
                if (answerValue.equals("1")) {
                    charPlace.setImageResource(R.drawable.ic_rate_malo);
                    //Texto
                    resultStar.setText("Pésimo");
                    //anim
                    charPlace.startAnimation(charAnimation);
                } else if (answerValue.equals("2")) {
                    charPlace.setImageResource(R.drawable.ic_rate_malo);
                    //Texto
                    resultStar.setText("Malo");
                    //anim
                    charPlace.startAnimation(charAnimation);
                } else if (answerValue.equals("3")) {
                    charPlace.setImageResource(R.drawable.ic_rate_bueno);
                    //Texto
                    resultStar.setText("Bueno");
                    //anim
                    charPlace.startAnimation(charAnimation);
                } else if (answerValue.equals("4")) {
                    charPlace.setImageResource(R.drawable.ic_rate_bueno);
                    //Texto
                    resultStar.setText("Muy Bueno");
                    //anim
                    charPlace.startAnimation(charAnimation);
                } else if (answerValue.equals("5")) {
                    charPlace.setImageResource(R.drawable.ic_rate_excelente);
                    //Texto
                    resultStar.setText("Excelente");
                    //anim
                    charPlace.startAnimation(charAnimation);
                }else {
                    Toast.makeText(CalificarAppTaski.this, "Not Point", Toast.LENGTH_SHORT).show();
                }
            }
        });

        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerValue.equals("4") || answerValue.equals("5")){
                    String url = "https://play.google.com/store/apps/details?id=com.fordev.taski";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    FancyToast.makeText(CalificarAppTaski.this, "Esto nos ayuda a mejorar para ti!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, R.drawable.logo_taski, false).show();
                    finish();
                }else {
                    String url = "https://wa.me/+573178816094/?text=Describe%20como%20podemos%20mejorar%20para%20usted...%20";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    FancyToast.makeText(CalificarAppTaski.this, "Esto nos ayuda a mejorar para ti!", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, R.drawable.logo_taski, false).show();
                    finish();
                }
            }
        });

    }
}
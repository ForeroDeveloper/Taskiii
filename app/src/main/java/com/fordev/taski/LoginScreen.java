package com.fordev.taski;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.Country;
import com.hbb20.CountryCodePicker;

public class LoginScreen extends AppCompatActivity {

    private MaterialButton btnEnviar;
    TextInputLayout phoneNumber;
    CountryCodePicker codePicker;
    TextInputEditText txtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        btnEnviar = findViewById(R.id.enviarBtn);
        phoneNumber = findViewById(R.id.edtNumero);
        txtPhone = (TextInputEditText) findViewById(R.id.txtNumero);
        codePicker = findViewById(R.id.codePicker);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numeroCel = phoneNumber.getEditText().getText().toString().trim();
                String fullNumber = "+"+ codePicker.getFullNumber() + "" + numeroCel;

                if (numeroCel.isEmpty()){
                    Toast.makeText(LoginScreen.this, "ingrese un número valido", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent = new Intent(getApplicationContext(), VerificationOTP.class);
                    intent.putExtra("phoneNumber", fullNumber);
                    startActivity(intent);
                }

            }
        });

    }
}
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

import es.dmoral.toasty.Toasty;

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

                try {
                    String numeroCel = phoneNumber.getEditText().getText().toString().trim();
                    String fullNumber = "+"+ codePicker.getFullNumber() + "" + numeroCel;
                    String numeroSencillo = numeroCel;

                    if (numeroCel.isEmpty()){
                        Toast.makeText(LoginScreen.this, "ingrese un número valido", Toast.LENGTH_SHORT).show();
                    }else{

                        Intent intent = new Intent(getApplicationContext(), VerificationOTP.class);
                        intent.putExtra("phoneNumber", fullNumber);
                        intent.putExtra("phoneNumberSencillo", numeroSencillo);
                        startActivity(intent);
                        finish();
                    }
                }catch (NumberFormatException e){
                    Toasty.error(LoginScreen.this,"Verifica lo signos que usas en los campos!",Toast.LENGTH_LONG, true).show();
                }


            }
        });

    }
}
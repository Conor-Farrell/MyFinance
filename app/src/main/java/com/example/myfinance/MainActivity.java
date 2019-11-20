package com.example.myfinance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //Variables to be used in this class
    private SharedPreferences mPreferences;
    public static final int POP_ACTIVITY_LANGUAGE_REQUEST_CODE = 0;
    Locale myLocale;
    public String currentLanguage;
    public String localeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        //Obtain and assign the values that were passed in the Intent
        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            currentLanguage = intent.getExtras().getString("currentLanguage");
        }

        //Update the locale before the setContentView line in order to update the activity before it loads
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLanguage = mPreferences.getString("language", "English");
        localeString = mPreferences.getString("locale", "en");
        myLocale = new Locale(localeString);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        setContentView(R.layout.activity_main);
/*
        //If a user is currently logged in, close this activity and go to the HomeActivity via Intent
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, HomeActivity.class));
            return;
        }
*/
        //Different flags to be displayed depending on value of the currentLanguage
        final ImageView languageImage = findViewById(R.id.language);
        switch (currentLanguage) {
            case "English": languageImage.setImageResource(R.drawable.english);
                break;
            case "Español": languageImage.setImageResource(R.drawable.spanish);
                break;
            case "Français": languageImage.setImageResource(R.drawable.french);
                break;
            case "Deutsche": languageImage.setImageResource(R.drawable.german);
                break;
            case "Português": languageImage.setImageResource(R.drawable.portuguese);
                break;
            case "Gaeilge": languageImage.setImageResource(R.drawable.irish);
                break;
            case "Svenska": languageImage.setImageResource(R.drawable.swedish);
                break;
            case "Italiano": languageImage.setImageResource(R.drawable.italian);
                break;
        }

        //Intent to PopActivityChangeLanguage executed when language flag is pressed
        languageImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), PopActivityChangeLanguage.class);
                intent.putExtra("currentLanguage", currentLanguage);
                startActivityForResult(intent, POP_ACTIVITY_LANGUAGE_REQUEST_CODE);
            }
        });
    }

    //Handling the values returned from the onCLickListener
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            //If the requestCode matches the Language requestCode
            case (POP_ACTIVITY_LANGUAGE_REQUEST_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    currentLanguage = data.getStringExtra("currentLanguage");
                    //Different tasks to be executed depending on value of the currentLanguage
                    switch (currentLanguage) {
                        //setLocale is called with the locale to match whichever language was selected
                        case "English": setLocale("en");
                            break;
                        case "Español": setLocale("es");
                            break;
                        case "Français": setLocale("fr");
                            break;
                        case "Deutsche": setLocale("de");
                            break;
                        case "Português": setLocale("pt");
                            break;
                        case "Gaeilge": setLocale("ga");
                            break;
                        case "Svenska": setLocale("sv");
                            break;
                        case "Italiano": setLocale("it");
                            break;
                    }
                }
                break;
            }
        }
    }

    //Refresh the screen with the updated locale
    public void setLocale(String lang) {
        //Set myLocale to the locale that was passed
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        //Update the configurations locale to match the locale that was passed
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //Create Intent to the current Activity, basically refreshing the activity with the new locale, and passing exercise data through the intent as extras
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.putExtra("currentLanguage", currentLanguage);
        startActivity(refresh);
        //Update the SharedPrefManager to store the new language and locale
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString("language", currentLanguage);
        mEditor.putString("locale", lang);
        mEditor.apply();
    }

    //Method executed when Login button is pressed
    public void loginPressed(View view) {
        //Checks to see if there is a solid connection to Internet via WiFi
        if (isNetworkAvailable()) {
            //Creates Intent to LoginActivity class
            Intent myIntent = new Intent(this, LoginActivity.class);
            myIntent.putExtra("currentLanguage", currentLanguage);
            startActivity(myIntent);
        }
        else {
            //Alert Dialog informing the user that the app is unable to connect to the Internet
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(R.string.serverError);
            builder.setMessage(R.string.noInternet);
            builder.setNegativeButton(R.string.gotIt, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Method executed when Register button is pressed
    public void registerPressed(View view) {
        //Checks to see if there is a solid connection to Internet via WiFi
        if (isNetworkAvailable()) {
            //Creates Intent to RegisterActivity class
            Intent myIntent = new Intent(this, com.example.myfinance.RegisterActivity.class);
            myIntent.putExtra("currentLanguage", currentLanguage);
            startActivity(myIntent);
        }
        else {
            //Alert Dialog informing the user that the app is unable to connect to the Internet
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(R.string.serverError);
            builder.setMessage(R.string.noInternet);
            builder.setNegativeButton(R.string.gotIt, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    //Check if there is a connection to the Internet via WiFi
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}

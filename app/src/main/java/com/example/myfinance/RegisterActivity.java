package com.example.myfinance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Variables to be used in this class
    private static final String TAG = "Registration";
    public String currentLanguage;
    public String firstName;
    public String lastName;
    public String emailAddress;
    public String password;
    public String dateOfBirth;
    public String gender;
    private EditText userFirstName, userLastName, userEmailAddress, userPassword;
    private TextView userDOB;
    public RadioButton maleRadioButton;
    public RadioButton femaleRadioButton;
    private TextView errorText;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private static final String KEY_FIRSTNAME = "First Name";
    private static final String KEY_LASTNAME = "Last Name";
    private static final String KEY_EMAILADDRESS = "Email Address";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_DATEOFBIRTH = "Date Of Birth";
    private static final String KEY_GENDER = "Gender";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userFirstName = findViewById(R.id.myFirstName);
        userLastName = findViewById(R.id.myLastName);
        userEmailAddress = findViewById(R.id.myEmailAddress);
        userPassword = findViewById(R.id.myPassword);
        userDOB = findViewById(R.id.myDateOfBirth);
        maleRadioButton = findViewById(R.id.radio_male);
        femaleRadioButton = findViewById(R.id.radio_female);
        errorText = findViewById(R.id.errorText);

        //Creating a dialog picker when the Date of Birth button is clicked
        userDOB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //Setting the value obtained from the date picker to the dateOfBirth String and userDOB TextView
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                dateOfBirth = day + "/" + month + "/" + year;
                userDOB.setText(dateOfBirth);
            }
        };
    }


    //Method executed when a Gender RadioButton is pressed
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        //Different tasks to be executed depending on which RadioButton is checked
        switch(view.getId()) {
            //If the male radioButton is checked
            case R.id.radio_male: if (checked) {
                //Set the gender to Male
                gender = "Male";
                maleRadioButton.setTextColor(getResources().getColor(R.color.secondaryColor));
                femaleRadioButton.setTextColor(getResources().getColor(R.color.secondaryColor));
            }
                break;
            //If the female radioButton is checked
            case R.id.radio_female: if (checked) {
                //Set the gender to Female
                gender = "Female";
                maleRadioButton.setTextColor(getResources().getColor(R.color.secondaryColor));
                femaleRadioButton.setTextColor(getResources().getColor(R.color.secondaryColor));
            }
                break;
        }
    }

    public void onRegisterButtonClicked(View v) {
        //emailPattern ensures the user does not just enter any string for an email. It must match the format "___@__.__"
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        //Convert data entered in fields to String values
        String firstName = userFirstName.getText().toString();
        String lastName = userLastName.getText().toString();
        String emailAddress = userEmailAddress.getText().toString();
        String password = userPassword.getText().toString();
        String userDOBString = userDOB.getText().toString();

        //Ensure no Strings are empty, the email matches that pattern, and one of the gender radioButton's are selected
        if ((firstName.length() > 0) && (lastName.length() > 0) && ((emailAddress.matches(emailPattern) && emailAddress.length() > 0)) && (password.length() > 0) && (userDOBString.equals(dateOfBirth)) && ((maleRadioButton.isChecked()) || (femaleRadioButton.isChecked()))) {

            Map<String, Object> note = new HashMap<>();
            note.put(KEY_FIRSTNAME, firstName);
            note.put(KEY_LASTNAME, lastName);
            note.put(KEY_EMAILADDRESS, emailAddress);
            note.put(KEY_PASSWORD, password);
            note.put(KEY_DATEOFBIRTH, userDOBString);
            note.put(KEY_GENDER, gender);

            db.collection("Users").document("User").set(note)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RegisterActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                            //Bring the user to the IntroActivity where they can learn more about Sleep Apnea
                            Intent goToIntroIntent = new Intent(getApplicationContext(), IntroActivity.class);
                            goToIntroIntent.putExtra("currentLanguage", currentLanguage);
                            startActivity(goToIntroIntent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    });
        }
        else {
            errorText.setText(R.string.invalidDataEntered);
            if (firstName.length() == 0) {
                userFirstName.setText("");
                userFirstName.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            if (lastName.length() == 0) {
                userLastName.setText("");
                userLastName.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            if ((!emailAddress.matches(emailPattern)) || (emailAddress.length() == 0)) {
                userEmailAddress.setText("");
                userEmailAddress.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            if (password.length() == 0) {
                userPassword.setText("");
                userPassword.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            if (!userDOBString.equals(dateOfBirth)) {
                userDOB.setHintTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
            if ((!maleRadioButton.isChecked()) && (!femaleRadioButton.isChecked())) {
                maleRadioButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                femaleRadioButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }
    }
}

package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import hu.unideb.inf.obdbypm.MainActivity;
import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.database.DatabaseHelper;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Person;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        DatabaseManager.init(this);

        editName = (EditText) findViewById(R.id.editTextName);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editPassword = (EditText) findViewById(R.id.editTextPassword1);
    }

    public void onClickSaveBtn(View v)
    {
        Person newUser = new Person();
        newUser.setName(editName.getText().toString().trim());
        newUser.setEmailAddress(editEmail.getText().toString().trim());
        newUser.setPassword(editPassword.getText().toString().trim());

        DatabaseManager.getInstance().addPerson(newUser);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickCancelBtn(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

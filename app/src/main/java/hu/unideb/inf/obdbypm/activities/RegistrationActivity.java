package hu.unideb.inf.obdbypm.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import hu.unideb.inf.obdbypm.MainActivity;
import hu.unideb.inf.obdbypm.R;
import hu.unideb.inf.obdbypm.database.DatabaseHelper;
import hu.unideb.inf.obdbypm.database.DatabaseManager;
import hu.unideb.inf.obdbypm.models.Person;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editPassword2;
    public static Integer num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FIRST
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editName = (EditText) findViewById(R.id.editTextName);
        editEmail = (EditText) findViewById(R.id.editTextEmail);
        editPassword = (EditText) findViewById(R.id.editTextPassword);
        editPassword2 = (EditText) findViewById(R.id.editTextPassword2);
    }

    @Override
    protected void onStart() {
        //SECOND
        super.onStart();
    }

    @Override
    protected void onResume() {
        //LAST - ALWAYS RUNNING
        super.onResume();
    }

    protected void onResume(Bundle savedInstanceState) {

    }

    public void onClickSaveBtn(View v)
    {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password1 = editPassword.getText().toString().trim();
        String password2 = editPassword2.getText().toString().trim();

        List<Person> allUser = DatabaseManager.getInstance().getAllPersons();
        for(int i = 0; i < allUser.size(); i++)
            if (allUser.get(i).getEmailAddress().equals(editEmail.getText().toString().trim()))
            {
                Toast.makeText(getApplicationContext(),
                        "This email is already registered!" , Toast.LENGTH_LONG)
                        .show();
                return;
            }

        if (name.equals("") || email.equals("") || password1.equals("") || password2.equals(""))
        {
            Toast.makeText(getApplicationContext(),
                    "Fill in the form below completely!" , Toast.LENGTH_LONG)
                    .show();
            return;
        }
        else if (!password1.equals(password2))
        {
            Toast.makeText(getApplicationContext(),
                    "Passwords don't match!" , Toast.LENGTH_LONG)
                    .show();
            return;
        }

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

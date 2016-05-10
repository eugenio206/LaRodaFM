package es.geniuspacs.larodafm;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SendMail extends AppCompatActivity {

    private final String URL_MAIL = "larodafm1072@gmail.com";
    private final String TYPE_MAIL = "message/rfc822";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMail();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void enviarMail() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType(TYPE_MAIL);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{URL_MAIL});
        emailIntent.putExtra(android.content.Intent.EXTRA_TITLE, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendMail.this, "No hay ningun cliente de correo instalado.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }
}

package es.geniuspacs.larodafm;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.support.v7.app.ActionBar;
import android.widget.Toast;

import es.geniuspacs.larodafm.services.StreamService;

public class LaRodaFM extends AppCompatActivity implements View.OnClickListener {

    static ImageButton iniciarServicio;
    Toolbar miToolbar;
    ActionBar actionBar;
    DrawerLayout dwLayout;
    static ProgressDialog progress;
    static boolean radioON = false;
    MediaPlayer mp;
    NotificationManager nManager;
    Notification notif;
    private final int CODIGO_NOTIFICACION = 001;

    private final String URL_MAIL = "geniuspacs@gmail.com";
    private final String TYPE_MAIL = "message/rfc822";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!comprobarConexionRed()) {
            verDialogoErrorRed();
        }

        recargarMp();

        miToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(miToolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.icon_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dwLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);

        NavigationView nView = (NavigationView) findViewById(R.id.myNavigationView);

        if(nView != null) {
            setUpNavigationDrawerContent(nView);
        }

        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        progress = new ProgressDialog(this);

        iniciarServicio = (ImageButton) findViewById(R.id.iniciarService);

        if(mp != null) {
            iniciarServicio.setImageResource(R.mipmap.btn_pause);
        }

        iniciarServicio.setOnClickListener(this);

        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        nManager.cancel(CODIGO_NOTIFICACION);
    }

    public void recargarMp() {
        this.mp = StreamService.getMP();
    }

    private void verDialogoErrorRed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LaRodaFM.this);

        builder.setTitle(getResources().getString(R.string.titleErrorRed));
        builder.setMessage(getResources().getString(R.string.textErrorRed));
        builder.setPositiveButton(getResources().getString(R.string.btnOkErrorRed), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        builder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(radioON) {
            iniciarNotificacion();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if(radioON) {
            nManager.cancel(CODIGO_NOTIFICACION);
        }

    }

    public void setRadioON(boolean radioON) {
        this.radioON = radioON;
    }

    private void iniciarNotificacion() {

        Intent i = new Intent(this, LaRodaFM.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, 0);

        notif = new NotificationCompat.Builder(this)
                .setContentIntent(contentIntent)
                .setTicker(getResources().getString(R.string.tickerNotif))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.textoNotif))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build();

        notif.flags = Notification.FLAG_NO_CLEAR;

        nManager.notify(CODIGO_NOTIFICACION, notif);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iniciarService:

                recargarMp();

                if(mp==null || !mp.isPlaying()) {
                    verVentana();
                    startService(new Intent(LaRodaFM.this, StreamService.class));
                    iniciarServicio.setImageResource(R.mipmap.btn_pause);
                }
                else {
                    stopService(new Intent(LaRodaFM.this, StreamService.class));
                    setPlayIcon();
                    setRadioON(false);
                }
                break;

        }
    }

    private boolean comprobarConexionRed() {

        ConnectivityManager conManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo infoRed = conManager.getActiveNetworkInfo();

        if(infoRed == null || !infoRed.isConnected() || !infoRed.isAvailable()) {
            return false;
        }
        else {
            return true;
        }

    }

    public static void setPlayIcon() {
        iniciarServicio.setImageResource(R.mipmap.btn_play);
    }

    public void verVentana() {
        progress.setTitle(R.string.title_ventCarga);
        progress.setMessage(getResources().getString(R.string.text_ventCarga));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCanceledOnTouchOutside(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                stopService(new Intent(LaRodaFM.this, StreamService.class));
                setPlayIcon();
                radioON = false;
            }
        });
        progress.show();
    }

    public void quitarVentana() {
        progress.dismiss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                dwLayout.openDrawer(GravityCompat.START);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setUpNavigationDrawerContent(NavigationView nView) {
        nView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.item_navigation_drawer_email:
                        dwLayout.closeDrawers();
                        startActivity(new Intent(LaRodaFM.this, SendMail.class));
                        break;

                    case R.id.item_navigation_drawer_nuestroPueblo:
                        dwLayout.closeDrawers();
                        startActivity(new Intent(LaRodaFM.this, NuestroPueblo.class));
                        break;

                    case R.id.item_navigation_drawer_reportError:
                        dwLayout.closeDrawers();
                        reportarError();
                        break;

                    case R.id.item_navigation_drawer_salir:
                        if(radioON) {
                            stopService(new Intent(LaRodaFM.this, StreamService.class));
                            setRadioON(false);
                        }
                        finish();
                        break;
                }

                return true;
            }
        });
    }

    private void reportarError() {

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType(TYPE_MAIL);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{URL_MAIL});
        emailIntent.putExtra(android.content.Intent.EXTRA_TITLE, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Reporte de error");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LaRodaFM.this, "No hay ningun cliente de correo instalado.", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("radioON", radioON);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(radioON) {
            iniciarServicio.setImageResource(R.mipmap.btn_pause);
        }
    }
}

package es.geniuspacs.larodafm.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;
import java.io.IOException;
import android.net.wifi.WifiManager.WifiLock;

import es.geniuspacs.larodafm.LaRodaFM;

public class StreamService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    static MediaPlayer mp;
    private final String URL_RADIO = "http://larodafm.ddns.net:8000";
    //private final String URL_RADIO = "http://195.55.74.212/cope/rockfm.mp3?GKID=b803794ee76d11e4b84e00163ea2c744&fspref=aHR0cDovL3BsYXllci5yb2NrZm0uZm0v";
    LaRodaFM activityPrincipal;


    @Override
    public void onCreate() {
        super.onCreate();

        activityPrincipal = new LaRodaFM();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        WifiLock wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "WifiLock");
        wifiLock.acquire();

        try {
            mp = new MediaPlayer();
            mp.setDataSource(URL_RADIO);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mp.setOnErrorListener(this);
            mp.setOnPreparedListener(this);
            mp.prepareAsync();
        } catch (IllegalArgumentException e) {
            showToastErrors(e.getMessage());
        } catch (IOException e) {
            showToastErrors(e.getMessage());
        } catch (SecurityException e) {
            showToastErrors(e.getMessage());
        } catch (IllegalStateException e) {
            showToastErrors(e.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mp!=null) {
            mp.stop();
            mp.release();
            mp=null;
        }

    }

    public static MediaPlayer getMP() {
        return mp;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showToastErrors(String error) {
        Toast.makeText(this, "ERROR: "+error, Toast.LENGTH_LONG).show();
        activityPrincipal.quitarVentana();
        activityPrincipal.setPlayIcon();
        activityPrincipal.setRadioON(false);
        onDestroy();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {

        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                showToastErrors("Error de conexión a servidor.");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                showToastErrors("MEDIA ERROR SERVER DIED.");
                break;
        }

        switch (extra) {
            case MediaPlayer.MEDIA_ERROR_IO:
                showToastErrors("Media Error IO.");
                break;

            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                showToastErrors("Media Error Malformed.");
                break;

            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                showToastErrors("Media Error Unsupported.");
                break;

            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                showToastErrors("Tiempo de conexión con el servidor agotado.");
                break;

            default:
                showToastErrors("Error de conexión al servidor. Inténtelo más tarde.");
                break;
        }

        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        activityPrincipal.quitarVentana();
        activityPrincipal.setRadioON(true);
    }
}

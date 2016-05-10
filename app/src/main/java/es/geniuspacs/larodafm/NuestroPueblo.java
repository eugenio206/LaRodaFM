package es.geniuspacs.larodafm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class NuestroPueblo extends AppCompatActivity {

    WebView visorWeb;
    ProgressBar progressBar;
    private final String URL_WIKIPEDIA_PUEBLO = "https://es.wikipedia.org/wiki/La_Roda_de_Andaluc%C3%ADa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuestro_pueblo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        visorWeb = (WebView) findViewById(R.id.visorWeb);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        visorWeb.getSettings().setJavaScriptEnabled(true);
        visorWeb.getSettings().setBuiltInZoomControls(true);

        visorWeb.loadUrl(URL_WIKIPEDIA_PUEBLO);

        visorWeb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });

        visorWeb.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);
                NuestroPueblo.this.setProgress(newProgress * 1000);

                progressBar.incrementProgressBy(newProgress);

                if(newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

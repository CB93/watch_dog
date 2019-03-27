package com.example.watch_dog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * <p>
 *     SchoolPageActivity
 * </p>
 */
public class SchoolPageActivity extends AppCompatActivity {

    /**
     * <p>
     *     WebView for school site
     * </p>
     */
    private WebView schoolSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_page);

        schoolSite = findViewById(R.id.schoolSite);
        schoolSite.setWebViewClient(new WebViewClient());
        schoolSite.loadUrl(getSchoolWebsite());
    }

    /**
     * <p>
     *     getSchoolWebsite
     * </p>
     *
     * @return Url to school site
     */
    public String getSchoolWebsite() {
        return (String) this.getIntent().getExtras().get("site");
    }
}

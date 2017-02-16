package com.example.jared.webcrawler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    GridView gridView;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawerNavView;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity self = this;
        final Context context = getApplicationContext();

        final Crawler myCrawler = new Crawler() {
            @Override
            public void onCrawlFinished(ArrayList<CrawledUrl> result) {
                RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
                loading.setVisibility(View.INVISIBLE);
                if(result != null) {
                    ArrayList<String> links = new ArrayList<>();
                    for(CrawledUrl crawledUrl: result) {
                        links.add(crawledUrl.getUrl());
                    }

                    Intent intent = new Intent(self, CrawlerList.class);
                    intent.putExtra("root", this.getRoot());
                    intent.putExtra("links", links);

                    startActivity(intent);
                } else {
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "Could not resolve the url", duration);
                    toast.show();
                }
            }
        };

        final EditText editUrlText = (EditText) findViewById(R.id.editUrlText);
        editUrlText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
                    loading.setVisibility(View.VISIBLE);
                    myCrawler.crawl(editUrlText.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }
}

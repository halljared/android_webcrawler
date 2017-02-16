package com.example.jared.webcrawler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CrawlerList extends BaseActivity {

    ListView listView;
    TextView urlLabel;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawler_list);
        final AppCompatActivity self = this;
        ArrayList<String> links = (ArrayList<String>) getIntent().getSerializableExtra("links");
        String root = getIntent().getStringExtra("root");
        urlLabel = (TextView) findViewById((R.id.url_label));
        listView = (ListView) findViewById(R.id.crawler_list);
        urlLabel.setText(root);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, links);
        listView.setAdapter(adapter);
        final Context context = getApplicationContext();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Crawler myCrawler = new Crawler() {
                    @Override
                    public void onCrawlFinished(ArrayList<CrawledUrl> result) {
                        if(result != null) {
                            ArrayList<String> links = new ArrayList<>();
                            for(CrawledUrl crawledUrl: result) {
                                links.add(crawledUrl.getUrl());
                            }
                            Intent intent = new Intent(self, CrawlerList.class);
                            intent.putExtra("root", this.getRoot());
                            intent.putExtra("links", links);
                            RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
                            if(loading != null) {
                                loading.setVisibility(View.INVISIBLE);
                            }
                            startActivity(intent);
                        } else {
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, "Could not resolve the url", duration);
                            toast.show();
                        }
                    }
                };
                String url = links.get(position);
                RelativeLayout loading = (RelativeLayout) findViewById(R.id.loadingPanel);
                if(loading != null) {
                    loading.setVisibility(View.VISIBLE);
                }
                myCrawler.crawl(url.toString());
            }
        });
    }

    public void goToUrl(View view) {
        Uri uri = Uri.parse(getIntent().getStringExtra("root"));
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(launchBrowser);
    }
}

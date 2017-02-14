package com.example.jared.webcrawler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView gridView;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView = (GridView) findViewById(R.id.main_grid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, numbers);

        gridView.setAdapter(adapter);

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




        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //do stuff
                Intent intent = new Intent(self, CrawlerList.class);
                //startActivity(intent);
            }
        });
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

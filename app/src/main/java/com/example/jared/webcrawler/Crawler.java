package com.example.jared.webcrawler;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jared on 2/9/2017.
 */

public abstract class Crawler {

    private String root;

    public Crawler() {

    }

    public abstract void onCrawlFinished(ArrayList<CrawledUrl> result);

    public void crawl(String url) {
        this.setRoot(url);
        new FetchDocument() {
            protected void onPostExecute(ArrayList<CrawledUrl> result) {
                super.onPostExecute(result);
                onCrawlFinished((result));
                // dismiss UI progress indicator
                // process the result
                // ...
            }
        }.execute(url); // start the background processing
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }
}

class CrawledUrl {
    private String url;
    private Boolean isNew;

    public CrawledUrl(String url, Boolean isNew) {
        this.url = normalizeUrl(url);
        this.isNew = isNew;
    }

    public static String normalizeUrl(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        Pattern pattern = Pattern.compile("(https?://(www\\.)?[^/]+).*");
        Matcher matcher = pattern.matcher(url);
        String match = null;
        while(matcher.find()) {
            String simpleDomain = matcher.group(1);
            match = simpleDomain;
        }
        if(match == null) {
            throw new RuntimeException("Couldn't normalize url: " + url);
        }
        return match;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }
}

class FetchDocument extends AsyncTask<String, Void, ArrayList<CrawledUrl>> {
    @Override
    protected ArrayList<CrawledUrl> doInBackground(String... strings) {
        // do background processing and return the appropriate result
        // ...
        ArrayList<CrawledUrl> results = new ArrayList<>();
        String url = strings[0];
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IllegalArgumentException e) {
            try {
                doc = Jsoup.connect("http://" + url).get();
                url = "http://" + url;
            } catch (IllegalArgumentException e2) {
                try {
                    doc = Jsoup.connect("http://www." + url).get();
                    url = "http://www." + url;
                } catch (Exception e3) {
                    return null;
                }
            } catch (IOException ioe) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

        Elements links = doc.select("a[href]");
        HashMap<String, Boolean> founds = new HashMap<>();
        for (Element _link : links) {
            String link = _link.attr("abs:href");
            if(!link.isEmpty()) {
                CrawledUrl cUrl = new CrawledUrl(link, true);
                String normalizedLink = cUrl.getUrl();
                if(!founds.containsKey(normalizedLink)) {
                    if(!normalizedLink.startsWith(url)) { //filter out self-referential links
                        founds.put(normalizedLink, true);
                        results.add(cUrl);
                    }
                }
            }
        }
        return results;
    }
}
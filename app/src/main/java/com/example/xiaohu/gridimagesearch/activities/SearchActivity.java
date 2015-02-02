package com.example.xiaohu.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.xiaohu.gridimagesearch.EndlessScrollListener;
import com.example.xiaohu.gridimagesearch.R;
import com.example.xiaohu.gridimagesearch.adapters.ImageResultsAdapter;
import com.example.xiaohu.gridimagesearch.models.ImageResult;
import com.example.xiaohu.gridimagesearch.models.SearchOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {
    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResult;
    private ImageResultsAdapter aImageResults;
    private SearchOptions searchOptions = new SearchOptions();
    static final int SETTING_REQUEST_CODE = 50;
    static int start=0;
    static int RESULT_SIZE = 8;
    static int MAX_START = 56;
    private static AsyncHttpClient client = new AsyncHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setupViews();
        imageResult = new ArrayList<>();
        aImageResults = new ImageResultsAdapter(this, imageResult);
        gvResults.setAdapter(aImageResults);

    }

    private void setupViews(){
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                ImageResult result = imageResult.get(position);
                i.putExtra("result", result);
                startActivity(i);
            }
        });
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                //customLoadMoreDataFromApi(page);
                Log.d("INFO", "page = " + page + " " + "totalItemscount = " + totalItemsCount);
                if((page-1)*RESULT_SIZE <= MAX_START){
                    String searchUrl = GenerateSearchUrlStartAt(etQuery.getText().toString(), (page-1)*RESULT_SIZE, searchOptions);
                    CallImageSearchApi(client, searchUrl);
                }else{
                    Toast.makeText(SearchActivity.this, "Max number reached.", Toast.LENGTH_SHORT).show();
                }

                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
            //Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("settings", searchOptions);
            startActivityForResult(i, SETTING_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onImageSearch(View v){
        String query = etQuery.getText().toString();
        Toast.makeText(this, "Search for: "+ query, Toast.LENGTH_SHORT).show();
        imageResult.clear();
        String searchUrl = GenerateSearchUrlStartAt(query, start, searchOptions);
        CallImageSearchApi(client, searchUrl);

    }

    private void CallImageSearchApi(AsyncHttpClient client, String searchUrl) {
        client.get(searchUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    //imageResult.clear();
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                    aImageResults.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("INFO", imageResult.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    String responseDetails = errorResponse.getString("responseDetails");
                    String responseStatus = errorResponse.getString("responseStatus");
                    Toast.makeText(SearchActivity.this, "Error " + responseStatus + ": " + responseDetails, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private String GenerateSearchUrlStartAt(String query, int start, SearchOptions searchOptions) {
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+ query + "&rsz=" + RESULT_SIZE;
        if(!"".equals(searchOptions.type)){
            url = url + "&imgtype=" + searchOptions.type;
        }
        if(!"".equals(searchOptions.color)){
            url = url + "&imgcolor=" + searchOptions.color;
        }
        if(!"".equals(searchOptions.size)){
            url = url + "&imgsz=" + searchOptions.size;
        }
        if(!"".equals(searchOptions.site)){
            url = url + "&as_sitesearch=" + searchOptions.site;
        }
        url = url + "&start=" + start;
        Log.i("INFO", "url="+ url);
        return url;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SETTING_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                searchOptions = (SearchOptions) data.getSerializableExtra("settings");
                //Toast.makeText(this, searchOptions.color + " " + searchOptions.site + " "
                        //+ searchOptions.size + " " + searchOptions.type, Toast.LENGTH_SHORT).show();
                imageResult.clear();
            }
        }
    }
}

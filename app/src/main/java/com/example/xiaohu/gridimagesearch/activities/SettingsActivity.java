package com.example.xiaohu.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.xiaohu.gridimagesearch.R;
import com.example.xiaohu.gridimagesearch.models.SearchOptions;

public class SettingsActivity extends ActionBarActivity {
    private SearchOptions searchOptions;
    private Spinner spImageSize;
    private Spinner spColorFilter;
    private Spinner spImageType;
    private EditText etSiteFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        searchOptions = (SearchOptions) getIntent().getSerializableExtra("settings");
        spImageSize = (Spinner) findViewById(R.id.spImageSize);
        spColorFilter = (Spinner) findViewById(R.id.spColorFilter);
        spImageType = (Spinner) findViewById(R.id.spImageType);
        etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
        InitializeSpinner();
    }

    private void InitializeSpinner() {
        if(searchOptions != null){
            String imageSize = searchOptions.size;
            String imageType = searchOptions.type;
            String colorFilter = searchOptions.color;
            String siteFilter = searchOptions.site;
            setSpinnerToValue(spImageSize, imageSize);
            setSpinnerToValue(spColorFilter, colorFilter);
            setSpinnerToValue(spImageType, imageType);
            etSiteFilter.setText(siteFilter);
        }
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i =0; i< adapter.getCount(); i++){
            if(adapter.getItem(i).equals(value)){
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void onSaveSettings(View v){
        searchOptions = new SearchOptions(spImageSize.getSelectedItem().toString(),
                spColorFilter.getSelectedItem().toString(),
                spImageType.getSelectedItem().toString(),
                etSiteFilter.getText().toString());
        Intent i = new Intent();
        i.putExtra("settings", searchOptions);
        setResult(RESULT_OK, i);
        this.finish();
    }

}

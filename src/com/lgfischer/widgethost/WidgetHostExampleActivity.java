package com.lgfischer.widgethost;

import java.util.ArrayList;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class WidgetHostExampleActivity extends Activity {
    final String TAG = "WidgetHostActivity";
    
    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;
    
    ViewGroup mainlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.widget_host);
        //setContentView(R.id.);
        setContentView(R.layout.widget_host);
        
        mainlayout = (ViewGroup) findViewById(R.id.main_layout);
        
        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this, R.id.APPWIDGET_HOST_ID);
    }
    
    void selectWidget(){
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }
    
    void addEmptyData(Intent pickIntent){
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            if(requestCode == R.id.REQUEST_PICK_APPWIDGET){
                configureWidget(data);
            }else if(requestCode == R.id.REQUEST_CREATE_APPWIDGET){
                createWidget(data);
            }
        }else if(resultCode == RESULT_CANCELED && data != null){
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if(appWidgetId != -1)
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
        }
    }
    
    private void configureWidget(Intent data){
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if(appWidgetInfo.configure != null){
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
        }else{
            createWidget(data);
        }
    }
    
    private void createWidget(Intent data){
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        
        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        mainlayout.addView(hostView);
        
        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }
    
    @Override
    protected void onStart(){
        super.onStart();
        mAppWidgetHost.startListening();
    }
    
    @Override
    protected void onStop(){
        super.onStop();
        mAppWidgetHost.stopListening();
    }
    
    public void removeWidget(AppWidgetHostView hostView){
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        mainlayout.removeView(hostView);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Log.i(TAG, "Menu selected: " + item.getTitle() + " / " + item.getItemId() + " / " + R.id.addWidget);
        switch (item.getItemId()) {
            case R.id.addWidget:
                selectWidget();
                return true;
            case R.id.removeWidget:
                removeWidgetMenuSelected();
                return false;
            }
            return super.onOptionsItemSelected(item);
    }

    private void removeWidgetMenuSelected() {
        int childCount = mainlayout.getChildCount();
        if(childCount > 1){
            View view = mainlayout.getChildAt(childCount -1);
            if(view instanceof AppWidgetHostView){
                removeWidget((AppWidgetHostView) view);
                Toast.makeText(this, R.string.widget_removed_popup, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(this, R.string.no_widgets_popup, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.widget_host_menu, menu);
        return true;
    }

}

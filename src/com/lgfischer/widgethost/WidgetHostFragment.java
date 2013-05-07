package com.lgfischer.widgethost;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class WidgetHostFragment extends Fragment implements OnClickListener{
    final String     TAG = "WidgetHostFragment";

    AppWidgetManager mAppWidgetManager;
    AppWidgetHost    mAppWidgetHost;

    ViewGroup        mainlayout;
    View             fraglayout;

    Button           addWidget;
    Button           removeWidget;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setContentView(R.layout.widget_host);

        mAppWidgetManager = AppWidgetManager.getInstance(getActivity());
        mAppWidgetHost = new AppWidgetHost(getActivity(), R.id.APPWIDGET_HOST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        return super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_widget_layout, container, false);
        mainlayout = (ViewGroup) view;

        addWidget = (Button) view.findViewById(R.id.btnAddWidget);
        removeWidget = (Button) view.findViewById(R.id.btnRemoveWidget);
        
        addWidget.setOnClickListener(this);
        removeWidget.setOnClickListener(this);

        return view;

    }

    void selectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, R.id.REQUEST_PICK_APPWIDGET);
    }

    void addEmptyData(Intent pickIntent) {
        ArrayList<AppWidgetProviderInfo> customInfo = new ArrayList<AppWidgetProviderInfo>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList<Bundle> customExtras = new ArrayList<Bundle>();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == R.id.REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            } else {
                createWidget(data);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1)
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, R.id.REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }

    private void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        AppWidgetHostView hostView = mAppWidgetHost.createView(getActivity(), appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        mainlayout.addView(hostView);

        Log.i(TAG, "The widget size is: " + appWidgetInfo.minWidth + "*" + appWidgetInfo.minHeight);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    public void removeWidget(AppWidgetHostView hostView) {
        mAppWidgetHost.deleteAppWidgetId(hostView.getAppWidgetId());
        mainlayout.removeView(hostView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(v.getId() == R.id.btnAddWidget)
            selectWidget();
        else if(v.getId() == R.id.btnRemoveWidget){
            int childCount = mainlayout.getChildCount();
            if(childCount > 1){
                View view = mainlayout.getChildAt(childCount -1);
                if(view instanceof AppWidgetHostView){
                    removeWidget((AppWidgetHostView) view);
                    Toast.makeText(getActivity(), R.string.widget_removed_popup, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Toast.makeText(getActivity(), R.string.no_widgets_popup, Toast.LENGTH_SHORT).show();
        }
        
    }

}

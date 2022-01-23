package com.example.mytripplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


// TODO: pathListMap 을 Adapter 외부에서 관리필요 -> ViewModel로 이전해야함;

public class MainActivity extends AppCompatActivity {
    Context context;
    MainActivityViewModel viewModel;
    public static final int REQUEST_PERMISSIONS = 0;

    @BindView(R.id.sv_main)
    ScrollView sv_main;
    @BindView(R.id.ll_path_list)
    LinearLayout ll_path_list;
    @BindView(R.id.btn_add_path)
    ImageButton btn_add_path;
    @BindView(R.id.btn_remove_path)
    ImageButton btn_remove_path;
    @BindView(R.id.fg_container)
    FragmentContainerView fg_container;

    PathListAdapter pathListAdapter;
    PathListListener pathListListener;
    private String TAG = "MyTripPlanner";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;

        viewModel = new ViewModelProvider(this, new MainActivityViewModelProviderFactory()).get(MainActivityViewModel.class);
        viewModel.init();

        pathListListener = new PathListListener() {
            @Override
            public void onAdded(int index, String path) {
                Toast.makeText(context, String.format("%d번째 아이템 클릭, 주소 = %s", index, path), Toast.LENGTH_SHORT).show();
                LatLng geo;
                try {
                    geo = search(path);
                } catch (Exception e) {
                    Toast.makeText(context, "위치를 찾지 못했습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.addMapList(new LocationItem(index, path, geo));
            }

            @Override
            public void onRemoved(int index) {
                Toast.makeText(context, String.format("%d번째 아이템 삭제", index + 1), Toast.LENGTH_SHORT).show();
                viewModel.removeMapList(index);
            }
        };

        viewModel.getPathMapList().observe(this, locationItems -> pathListAdapter.setLocationItems(locationItems));

        pathListAdapter = new PathListAdapter(this, ll_path_list, pathListListener);
        pathListAdapter.addChild();



        fg_container.setOnTouchListener((view, motionEvent) -> {
            int action = motionEvent.getAction();
            switch (action)
            {
                case MotionEvent.ACTION_DOWN: // Disallow ScrollView to intercept touch events.
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP: // Allow ScrollView to intercept touch events.
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            super.onTouchEvent(motionEvent);
            return true;
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fg_container, new MapsFragment(), null)
                .commit();
    }

    @OnClick (R.id.btn_add_path)
    void addPath () {
        pathListAdapter.addChild();
    }

    @OnClick (R.id.btn_remove_path)
    void removePath () {
        pathListAdapter.reduceChild();
    }

    public LatLng search (String path) throws IndexOutOfBoundsException {
        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> list = null;

        try {
            list = geocoder.getFromLocationName(path, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address;
        try {
            address = list.get(0);
        } catch (Exception e) {
            throw e;
        }

        LatLng geoPoint = new LatLng(address.getLatitude(), address.getLongitude());
        return geoPoint;
    }

}

interface PathListListener {
    void onAdded(int index, String path);
    void onRemoved (int index);
}

class PathListAdapter {
    private int count = 0;
    ArrayList<LocationItem> locationItems;
    private Context context;
    private PathListListener pathListListener;
    LinearLayout linearLayout;

    public PathListAdapter (Context context, LinearLayout linearLayout , PathListListener pathListListener) {
        this.context = context;
        this.pathListListener = pathListListener;
        this.linearLayout = linearLayout;
    }

    public void setLocationItems(ArrayList<LocationItem> locationItems) {
        this.locationItems = locationItems;
        notifyChanged();
    }

    public void addChild () {
        count ++;
        notifyChanged();
    }

    public void reduceChild () {
        if (count > 1) {
            count--;
            pathListListener.onRemoved(count);
            notifyChanged();
        }
    }

    public void notifyChanged () {
        linearLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            linearLayout.addView(getView(i));
        }
    }

    public View getView(int i) {
        View view =  LayoutInflater.from(context).inflate(R.layout.path_item, null, false);

        TextView textView = (TextView) view.findViewById(R.id.tv_pathnum);
        EditText editText = (EditText) view.findViewById(R.id.et_path);
        Button button = (Button) view.findViewById(R.id.btn_path_search);

        textView.setText("경로" + (i+1));

        if (locationItems != null) {
            for (LocationItem locationItem : locationItems) {
                if (locationItem.index == i) {
                    editText.setText(locationItem.name);
                    break;
                }
            }
        }

        button.setOnClickListener(view1 -> {
            pathListListener.onAdded(i, editText.getText().toString());
        });
        return view;
    }

}

// 기능 1. 지도표시, 2 검색지에 지도 마킹, 3. 각 경로 간의 거리계산, 4. 총시간 계산
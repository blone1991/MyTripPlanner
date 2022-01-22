package com.example.mytripplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


// TODO: pathListMap 을 Adapter 외부에서 관리필요 -> ViewModel로 이전해야함;

public class MainActivity extends AppCompatActivity {
    Context context;
    MainActivityViewModel viewModel;
    public static final int REQUEST_PERMISSIONS = 0;

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
                viewModel.addMapList(index, path);
            }

            @Override
            public void onRemoved(int index) {
                Toast.makeText(context, String.format("%d번째 아이템 삭제", index + 1), Toast.LENGTH_SHORT).show();
                viewModel.removeMapList(index);
            }
        };

        viewModel.getPathMapList().observe(this, hashMaps -> pathListAdapter.setPathListMap(hashMaps));

        pathListAdapter = new PathListAdapter(this, ll_path_list, pathListListener);
        pathListAdapter.addChild();

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
}

interface PathListListener {
    void onAdded(int index, String path);
    void onRemoved (int index);
}

class PathListAdapter {
    private int count = 0;
    ArrayList<HashMap<Integer, String>> pathListMap;
    private Context context;
    private PathListListener pathListListener;
    LinearLayout linearLayout;

    public PathListAdapter (Context context, LinearLayout linearLayout , PathListListener pathListListener) {
        this.context = context;
        this.pathListListener = pathListListener;
        this.linearLayout = linearLayout;
    }

    public void setPathListMap(ArrayList<HashMap<Integer, String>> pathListMap) {
        this.pathListMap = pathListMap;
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

        if (pathListMap != null) {
            for (HashMap<Integer, String> hashMap : pathListMap) {
                if (hashMap.containsKey(i)) {
                    editText.setText(hashMap.get(i));
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
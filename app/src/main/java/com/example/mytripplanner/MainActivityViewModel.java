package com.example.mytripplanner;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityViewModel extends ViewModel {
    ArrayList<LocationItem> pathMapList;
    MutableLiveData<ArrayList<LocationItem>> ld_pathMapList;

    public void init () {
        if (pathMapList == null) {
            pathMapList = new ArrayList<>();
        }
        if (ld_pathMapList == null) {
            ld_pathMapList = new MutableLiveData<ArrayList<LocationItem>>();
        }
    }

    public MutableLiveData<ArrayList<LocationItem>> getPathMapList() {
         return ld_pathMapList;
    }

    public void addMapList(LocationItem locationItem) {
        pathMapList.removeIf(locationItem1 -> locationItem1.index == locationItem.index);
        pathMapList.add(locationItem);
        ld_pathMapList.setValue(pathMapList);
    }

    public void removeMapList (int i) {
        pathMapList.removeIf(locationItem -> locationItem.index == i);
        ld_pathMapList.setValue(pathMapList);
    }
}

class MainActivityViewModelProviderFactory implements ViewModelProvider.Factory {
    static ViewModel viewModel;
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            if (viewModel == null) {
                viewModel = modelClass.newInstance();
            }
            return (T) viewModel;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Factory Runtime Error");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("Runtime Error");
        }
    }
}
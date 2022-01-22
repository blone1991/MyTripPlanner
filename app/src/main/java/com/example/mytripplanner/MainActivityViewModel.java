package com.example.mytripplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivityViewModel extends ViewModel {
    ArrayList<HashMap<Integer, String>> pathMapList;
    MutableLiveData<ArrayList<HashMap<Integer, String>>> ld_pathMapList;

    public void init () {
        if (pathMapList == null) {
            pathMapList = new ArrayList<>();
        }
        if (ld_pathMapList == null) {
            ld_pathMapList = new MutableLiveData<ArrayList<HashMap<Integer, String>>>();
        }
    }

    public MutableLiveData<ArrayList<HashMap<Integer, String>>> getPathMapList() {
         return ld_pathMapList;
    }

    public void addMapList(int i, String path) {
        HashMap<Integer, String> hashMap = new HashMap<>();
        hashMap.put(i, path);
        pathMapList.add(hashMap);
        // TODO : Search Address & Mark to Map
        ld_pathMapList.setValue(pathMapList);
    }

    public void removeMapList (int i) {
        pathMapList.iterator()
                .forEachRemaining(hashMap -> hashMap.remove(i));

        // TODO : Remove Marker;
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
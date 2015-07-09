/**
 * Copyright (C) 2014 xiaobudian Inc.
 */
package com.stickercamera.app.camera.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


import com.github.skykai.stickercamera.R;
import com.stickercamera.app.camera.CameraManager;
import com.stickercamera.app.camera.adapter.GalleryAdapter;
import com.stickercamera.app.model.PhotoItem;
import com.stickercamera.base.BaseFragment;

import java.util.ArrayList;

/**
 * @author tongqian.ni
 */
public class AlbumFragment extends BaseFragment {
    private ArrayList<PhotoItem> photos = new ArrayList<PhotoItem>();

    public AlbumFragment() {
        super();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static Fragment newInstance(ArrayList<PhotoItem> photos) {
        Fragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        args.putSerializable("photos", photos);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_album, null);
        photos = (ArrayList<PhotoItem>) getArguments().getSerializable("photos");
        albums = (GridView) root.findViewById(R.id.albums);
        albums.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                PhotoItem photo = photos.get(arg2);
				CameraManager.getInst().processPhotoItem(getActivity(), photo);
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        albums.setAdapter(new GalleryAdapter(getActivity(), photos));
    }

	private GridView albums;
}

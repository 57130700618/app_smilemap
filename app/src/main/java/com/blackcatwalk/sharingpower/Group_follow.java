package com.blackcatwalk.sharingpower;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Group_follow extends Fragment {

    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private List<Group> albumList;

    public Group_follow() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_group_fragment_detail, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        albumList = new ArrayList<>();
        adapter = new GroupAdapter(getContext(), albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new Group_GridSpacingItemDecoration(2, Group_GridSpacingItemDecoration.dpToPx(2,getActivity()), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        Group_GridSpacingItemDecoration.prepareAlbums("follow",adapter,albumList,getActivity());
        return v;
    }
}

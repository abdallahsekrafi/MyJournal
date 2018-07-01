package com.zwir.myjournal.fragments;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.zwir.myjournal.R;
import com.zwir.myjournal.adapter.RecyclerViewDiaryAdapter;
import com.zwir.myjournal.data.Diary;
import com.zwir.myjournal.data.DiaryListViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DiaryListFragment extends Fragment {

    private DiaryListViewModel viewModel;
    private RecyclerViewDiaryAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;
    TextView tvTotalEntries;
    private final String entries=" Items";
    LinearLayout linearLayoutEmptyData;
    public DiaryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_diary_list, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.show();
        linearLayoutEmptyData=view.findViewById(R.id.empty_data);
        tvTotalEntries=actionBar.getCustomView().findViewById(R.id.total_entries);
        recyclerView = view.findViewById(R.id.recycler_list_diary);
        recyclerViewAdapter = new RecyclerViewDiaryAdapter(new ArrayList<Diary>(), getContext(),getActivity().getSupportFragmentManager(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recyclerViewAdapter);
        viewModel = ViewModelProviders.of(this).get(DiaryListViewModel.class);
        viewModel.getItemAndPersonList().observe(DiaryListFragment.this, new Observer<List<Diary>>() {
            @Override
            public void onChanged(@Nullable List<Diary> diaryList) {
                if(diaryList.size()==0) {
                    linearLayoutEmptyData.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else {
                    linearLayoutEmptyData.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                recyclerViewAdapter.addItems(diaryList);
                tvTotalEntries.setText(String.valueOf(diaryList.size())+entries);

            }
        });
        bottomNavigationView=view.findViewById(R.id.nav_list_diary_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        return view;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.log_out:
                    logOut();
                    break;
                case R.id.add_new_diary:
                    replaceFragment();
                    break;
                case R.id.settings:

                    break;
            }
            return true;
        }
    };
    // log out
    void logOut(){
        FirebaseAuth.getInstance().signOut();
        android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        LoginFragment loginFragment=new LoginFragment();
        fragmentTransaction.replace(R.id.main_container,loginFragment,"loginFragment");
        fragmentTransaction.commit();
    }
    // replace fragment
    void replaceFragment(){
        Bundle bundle = new Bundle();
        bundle.putString("action","new_diary");
        android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        NewDiaryFragment newDiaryFragment=new NewDiaryFragment();
        newDiaryFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.main_container,newDiaryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
        tvTotalEntries.setText(String.valueOf(recyclerViewAdapter.getItemCount())+entries);
        if(recyclerViewAdapter.getItemCount()==0) {
            linearLayoutEmptyData.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else {
            linearLayoutEmptyData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }


    }
}

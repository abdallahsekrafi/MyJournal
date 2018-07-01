package com.zwir.myjournal.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zwir.myjournal.R;
import com.zwir.myjournal.data.Diary;
import com.zwir.myjournal.data.DiaryListViewModel;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewDiaryFragment extends Fragment {


    private Diary diary;
    private BottomNavigationView bottomNavigationView;
    public ViewDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        diary= (Diary) bundle.getSerializable("my_diary");
        View view= inflater.inflate(R.layout.fragment_view_diary, container, false);
        ((ImageView)view.findViewById(R.id.img_diary_bm)).setImageResource(diary.getDrawableBookMark(diary.getBookMark()));
        ((TextView)view.findViewById(R.id.tv_view_diary_day)).setText(DateFormat.format("EEEE", diary.getCreatedDate()));
        ((TextView)view.findViewById(R.id.tv_view_diary_date)).setText(DateFormat.format("dd", diary.getCreatedDate()));
        String montYear=DateFormat.format("MMM", diary.getCreatedDate())+", "+
                DateFormat.format("yyyy", diary.getCreatedDate());
        ((TextView)view.findViewById(R.id.tv_view_diary_month)).setText(montYear);
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        ((TextView)view.findViewById(R.id.tv_view_diary_time)).setText(localDateFormat.format(diary.getCreatedDate()));
        ((TextView)view.findViewById(R.id.tv_view_diary_title)).setText(diary.getTitle());
        ((TextView)view.findViewById(R.id.tv_new_diary_summary)).setText(diary.getSummary());
        bottomNavigationView=view.findViewById(R.id.nav_view_diary_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        return view;
    }
    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete_diary_menu:
                    deleteDiary();
                    break;
                case R.id.edit_diary_menu:
                    editDiary();
                    break;
            }
            return true;
        }
    };
    void deleteDiary(){
        DiaryListViewModel viewModel = ViewModelProviders.of(this).get(DiaryListViewModel.class);
        viewModel.deleteItem(diary);
        backToList();
    }
    void backToList(){
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }
    void editDiary(){
        Bundle bundle = new Bundle();
        bundle.putString("action","edit_diary");
        bundle.putSerializable("my_diary2", diary);
        android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        NewDiaryFragment newDiaryFragment=new NewDiaryFragment();
        newDiaryFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.main_container,newDiaryFragment);
        fragmentTransaction.commit();
    }
}

package com.zwir.myjournal.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.zwir.myjournal.R;
import com.zwir.myjournal.adapter.BookMarkArrayAdapter;
import com.zwir.myjournal.data.Diary;
import com.zwir.myjournal.data.DiaryListViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewDiaryFragment extends Fragment {

    private Spinner spDiaryBM;
    private BottomNavigationView bottomNavigationView;
    private TextView tvDiaryDay;
    private TextView tvDiaryDate;
    private TextView tvDiaryMonth;
    private TextView tvDiaryTime;
    private EditText tvDiaryTitle;
    private EditText tvDiarySummary;
    private Date currentDate;
    private String action;
    private Diary diary;
    public NewDiaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        action=bundle.getString("action");
        if (action.equals("edit_diary")){
            diary= (Diary) bundle.getSerializable("my_diary2");
        }
        View view= inflater.inflate(R.layout.fragment_new_diary, container, false);
        spDiaryBM=view.findViewById(R.id.sp_diary_bm);
        bottomNavigationView=view.findViewById(R.id.nav_new_diary_button);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        initBMSpinner(getActivity());
        tvDiaryDay=view.findViewById(R.id.tv_new_diary_day);
        tvDiaryDate=view.findViewById(R.id.tv_new_diary_date);
        tvDiaryMonth=view.findViewById(R.id.tv_new_diary_month);
        tvDiaryTime=view.findViewById(R.id.tv_new_diary_time);
        tvDiaryTitle=view.findViewById(R.id.tv_new_diary_title);
        tvDiarySummary=view.findViewById(R.id.tv_new_diary_summary);
        fillDate();
        return view;
    }
    BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.cancel:
                    backToList();
                    break;
                case R.id.save:
                    saveDiary();
                    break;
            }
            return true;
        }
    };
    void fillDate(){
        if(action.equals("new_diary")){
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());
            try {
                currentDate = df.parse(date);
            } catch (ParseException e) {

            }
        }
        else {
            currentDate=diary.getCreatedDate();
            tvDiaryTitle.setText(diary.getTitle());
            tvDiarySummary.setText(diary.getSummary());
        }

        tvDiaryDay.setText(android.text.format.DateFormat.format("EEEE", currentDate));
        tvDiaryDate.setText(android.text.format.DateFormat.format("dd", currentDate));
        String month=android.text.format.DateFormat.format("MMM", currentDate)+", "+android.text.format.DateFormat.format("yyyy", currentDate);
        tvDiaryMonth.setText(month);
        tvDiaryTime.setText(android.text.format.DateFormat.format("HH:mm", currentDate));
    }
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    void backToList(){
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }
    private void initBMSpinner(Context context) {
        BookMarkArrayAdapter bookMarkArrayAdapter = new BookMarkArrayAdapter(context, Diary.getBookMarkArray());
        spDiaryBM.setAdapter(bookMarkArrayAdapter);
        if (action.equals("new_diary")){
            spDiaryBM.setSelection(0);
        }
        else {
            switch (diary.getBookMark()){
                case Diary.blueBM:
                    spDiaryBM.setSelection(0);
                    break;
                case Diary.yellowBM:
                    spDiaryBM.setSelection(1);
                    break;
                case Diary.orangeBM:
                    spDiaryBM.setSelection(2);
                    break;
                case Diary.redBM:
                    spDiaryBM.setSelection(3);
                    break;
            }
        }

    }
    void saveDiary(){
        String title=tvDiaryTitle.getText().toString().trim();
        String summary=tvDiarySummary.getText().toString().trim();
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(summary)) {
            Crouton.makeText(getActivity(), R.string.new_diary_title_summary, Style.ALERT).show();
        }
        else {
            DiaryListViewModel viewModel = ViewModelProviders.of(this).get(DiaryListViewModel.class);
            if (action.equals("new_diary")){
                Diary diary=new Diary(title,summary,currentDate,getBMName(spDiaryBM.getSelectedItemPosition()));
                viewModel.saveItem(diary);
            }
          else {
               diary.setTitle(title);
               diary.setSummary(summary);
               diary.setBookMark(getBMName(spDiaryBM.getSelectedItemPosition()));
                viewModel.updateItem(diary);
            }
            backToList();
        }
    }
    String getBMName(int index){
        String bm=Diary.blueBM;
        switch (index){
            case 0: bm=Diary.blueBM;
                break;
            case 1: bm=Diary.yellowBM;
                break;
            case 2: bm=Diary.orangeBM;
                break;
            case 3: bm=Diary.redBM;
                break;

        }
        return bm;

    }
}

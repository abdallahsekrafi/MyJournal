package com.zwir.myjournal.adapter;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.zwir.myjournal.R;
import com.zwir.myjournal.data.Diary;
import com.zwir.myjournal.data.DiaryListViewModel;
import com.zwir.myjournal.fragments.NewDiaryFragment;
import com.zwir.myjournal.fragments.ViewDiaryFragment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewDiaryAdapter extends RecyclerView.Adapter<RecyclerViewDiaryAdapter.RecyclerViewHolder>{

    private List<Diary> diaryList;
    private Context mContext;
    private android.support.v4.app.FragmentManager fragmentManager;
    private Fragment fragment;
    public RecyclerViewDiaryAdapter(List<Diary> diaryList, Context mContext, FragmentManager fragmentManager,Fragment fragment) {
        this.diaryList = diaryList;
        this.mContext=mContext;
        this.fragmentManager=fragmentManager;
        this.fragment=fragment;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_diary_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, int position) {
        final Diary diary = diaryList.get(position);
        holder.imgViewBookMark.setImageResource(diary.getDrawableBookMark(diary.getBookMark()));
        holder.tvDiaryDay.setText( DateFormat.format("EEEE", diary.getCreatedDate()));
        holder.tvDiaryDate.setText( DateFormat.format("dd", diary.getCreatedDate()));
        String montYear=DateFormat.format("MMM", diary.getCreatedDate())+", "+
                        DateFormat.format("yyyy", diary.getCreatedDate());
        holder.tvDiaryMonth.setText(montYear);
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        holder.tvDiaryTime.setText(localDateFormat.format(diary.getCreatedDate()));
        holder.tvDiaryTitle.setText(diary.getTitle());
        holder.tvDiarySummary.setText(diary.getSummary());
        // SetUp option listener
        holder.btnOptionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOptionClicked(view,diary);
            }
        });
        holder.diaryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Bundle bundle = new Bundle();
                bundle.putSerializable("my_diary", diary);
                android.support.v4.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                ViewDiaryFragment viewDiaryFragment=new ViewDiaryFragment();
                viewDiaryFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.main_container,viewDiaryFragment,"ViewDiaryFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }
    void btnOptionClicked(View view, final Diary diary){
        Context wrapper = new ContextThemeWrapper(mContext,R.style.popupMenuStyle);
        PopupMenu popup = new PopupMenu(wrapper,view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        try {
            Class<?> classPopupMenu = Class.forName(popup
                    .getClass().getName());
            Field mPopup = classPopupMenu.getDeclaredField("mPopup");
            mPopup.setAccessible(true);
            Object menuPopupHelper = mPopup.get(popup);
            Class<?> classPopupHelper = Class.forName(menuPopupHelper
                    .getClass().getName());
            Method setForceIcons = classPopupHelper.getMethod(
                    "setForceShowIcon", boolean.class);
            setForceIcons.invoke(menuPopupHelper, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId()==R.id.delete_diary_menu){
                    // delete diary
                    deleteDiary(diary);
                }
                else if (menuItem.getItemId()==R.id.edit_diary_menu){
                    // edit diary
                    editDiary(diary);
                }
                return true;
            }
        });
        popup.show();
    }
    void editDiary(Diary diary){
        Bundle bundle = new Bundle();
        bundle.putString("action","edit_diary");
        bundle.putSerializable("my_diary2", diary);
        android.support.v4.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        NewDiaryFragment newDiaryFragment=new NewDiaryFragment();
        newDiaryFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.main_container,newDiaryFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    void deleteDiary(Diary diary){
       DiaryListViewModel viewModel = ViewModelProviders.of(fragment).get(DiaryListViewModel.class);
        viewModel.deleteItem(diary);
        notifyDataSetChanged();
    }
    public void addItems(List<Diary> diaryList) {
        this.diaryList = diaryList;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
       private ImageView imgViewBookMark;
       private TextView tvDiaryDay;
       private TextView tvDiaryDate;
        private TextView tvDiaryMonth;
        private TextView tvDiaryTime;
        private TextView tvDiaryTitle;
        private TextView tvDiarySummary;
        private ImageButton btnOptionMenu;
        private LinearLayout diaryContainer;
        RecyclerViewHolder(View view) {
            super(view);
            imgViewBookMark =view.findViewById(R.id.book_mark);
            tvDiaryDay=view.findViewById(R.id.tv_entries_item_day);
            tvDiaryDate=view.findViewById(R.id.Tv_entries_item_date);
            tvDiaryMonth=view.findViewById(R.id.Tv_entries_item_month);
            tvDiaryTime=view.findViewById(R.id.Tv_entries_item_time);
            tvDiaryTitle=view.findViewById(R.id.Tv_entries_item_title);
            tvDiarySummary=view.findViewById(R.id.Tv_entries_item_summary);
            btnOptionMenu=view.findViewById(R.id.button_option_menu);
            diaryContainer =view.findViewById(R.id.diary_container);
        }
    }
}
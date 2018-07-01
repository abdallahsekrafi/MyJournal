package com.zwir.myjournal;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.zwir.myjournal.fragments.DiaryListFragment;
import com.zwir.myjournal.fragments.LoginFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);
        actionBar.hide();

       android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            DiaryListFragment diaryListFragment=new DiaryListFragment();
            fragmentTransaction.replace(R.id.main_container,diaryListFragment,"diaryListFragment");
            fragmentTransaction.commit();
        }
        else {
            LoginFragment loginFragment=new LoginFragment();
            fragmentTransaction.replace(R.id.main_container,loginFragment,"loginFragment");
            fragmentTransaction.commit();
        }
        setContentView(R.layout.activity_main);
    }
    private boolean internetIsConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

}

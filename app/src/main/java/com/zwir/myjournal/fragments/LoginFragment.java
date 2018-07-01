package com.zwir.myjournal.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.zwir.myjournal.R;
import com.zwir.myjournal.progress.CustomProgressDialog;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText emailInput;
    private EditText passwordInput;
    private TextView navigationMode;
    private TextView forgotPwd;
    private LinearLayout passwordLayout;
    private Button btnSingIn;
    private Button btnSingInWithGoogle;
    private Boolean hideMode=true;
    private String mode="connexion";
    private final String EMAIL_CACHED="emailcached";
    public final int REQUEST_GOOGLE_ACCOUNT = 5;
    private SharedPreferences preferences_LogIn;
    private FirebaseAuth myFireBaseAuth;
    private GoogleApiClient mGoogleApiClient;
    CustomProgressDialog dialog;
    public LoginFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        preferences_LogIn=getActivity().getSharedPreferences("LogInPrefs", Context.MODE_PRIVATE);
        myFireBaseAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View  loginView= inflater.inflate(R.layout.fragment_login, container, false);
        emailInput= loginView.findViewById(R.id.text_email);
        emailInput.setText(preferences_LogIn.getString(EMAIL_CACHED,null));
        emailInput.setSelection(emailInput.getText().length());
        passwordInput = loginView.findViewById(R.id.text_password);
        // check if RTL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isRTL(getActivity())) {
            // Force a right-aligned text entry, otherwise latin character input,
            passwordInput.setTextDirection(View.TEXT_DIRECTION_RTL);
            // Make the "Enter password" hint display on the right hand side
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
        // setUp Button
        btnSingIn = loginView.findViewById(R.id.button_connextion);
        navigationMode = loginView.findViewById(R.id.text_creataccount);
        forgotPwd =loginView.findViewById(R.id.text_reset_password);
        passwordLayout=loginView.findViewById(R.id.password_layout);
        btnSingInWithGoogle = loginView.findViewById(R.id.button_google);
        btnSingInWithGoogle.setOnClickListener(googleListener);
        loginView.findViewById(R.id.button_hide_look_pw).setOnClickListener(hideLookPWListener);
        // setUp password editText if RTL
        passwordInput.addTextChangedListener(new TextWatcher() {

            boolean inputTypeChanged;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                // Workaround https://code.google.com/p/android/issues/detail?id=201471 for Android 4.4+
                if (Build.VERSION.SDK_INT >Build.VERSION_CODES.JELLY_BEAN_MR1 && isRTL(getActivity())) {
                    if (hideMode) {
                        if (s.length() > 0) {
                            if (!inputTypeChanged) {

                                // When a character is typed, dynamically change the EditText's
                                // InputType to PASSWORD, to show the dots and conceal the typed characters.
                                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                                        InputType.TYPE_TEXT_VARIATION_PASSWORD |
                                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                                // Move the cursor to the correct place (after the typed character)
                                passwordInput.setSelection(s.length());

                                inputTypeChanged = true;
                            }
                        } else {

                            // Reset EditText: Make the "Enter password" hint display on the right
                            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

                            inputTypeChanged = false;
                        }
                    }
                }

            }
        });
        // setUp Keyboard option
        emailInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId== EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    switch (mode) {
                        case "reset":
                            loginOrCreate();
                            break;
                        case "connexion":
                            passwordInput.requestFocus();
                            break;
                    }
                }
                return false;
            }
        });
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_NEXT || actionId== EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                {
                    loginOrCreate();
                }
                return false;
            }
        });
        // event listeners
        btnSingIn.setOnClickListener(btnconListener);
        navigationMode.setOnClickListener(modenavicationListener);
        forgotPwd.setOnClickListener(forgotPWListener);
        return loginView;
    }
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Getting application context
         dialog=new CustomProgressDialog(getActivity());
    }
    //btn Sign In Listener
    private View.OnClickListener btnconListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginOrCreate();
        }
    };
    // navigation mode
    private View.OnClickListener modenavicationListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(mode.equals("connexion"))
            {
                btnSingIn.setText(R.string.create_account_button);
                mode="create";
                navigationMode.setText(R.string.login_with_exist_account);
                btnSingInWithGoogle.setVisibility(View.GONE);
            }
            else {
                btnSingIn.setText(R.string.sign_in_login_button);
                mode="connexion";
                navigationMode.setText(R.string.create_new_account_login);
                btnSingInWithGoogle.setVisibility(View.VISIBLE);
            }
        }
    };
    // Forgot password
    View.OnClickListener forgotPWListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mode.equals("reset")){
                btnSingIn.setText(R.string.sign_in_login_button);
                emailInput.requestFocus();
                emailInput.setText(preferences_LogIn.getString(EMAIL_CACHED,null));
                emailInput.setSelection(emailInput.getText().length());
                mode="connexion";
                passwordLayout.setVisibility(View.VISIBLE);
                passwordInput.setText("");
                navigationMode.setText(R.string.create_new_account_login);
                navigationMode.setVisibility(View.VISIBLE);
                forgotPwd.setText(R.string.reset_pwd_link);
                btnSingInWithGoogle.setVisibility(View.VISIBLE);
            }
            else {
                btnSingIn.setText(R.string.ok_button);
                emailInput.setText("");
                emailInput.requestFocus();
                mode="reset";
                passwordLayout.setVisibility(View.GONE);
                navigationMode.setVisibility(View.GONE);
                forgotPwd.setText(R.string.sign_in_login_button);
                btnSingInWithGoogle.setVisibility(View.GONE);
            }
        }
    };
    // Hide/Show password
    View.OnClickListener hideLookPWListener =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ImageButton imageButton=(ImageButton)view;
            if(hideMode)
            {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS );
                imageButton.setImageResource(R.drawable.ic_eye_look);
                hideMode =false;
            }
            else
            {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD |
                        InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                imageButton.setImageResource(R.drawable.ic_eye_hidden);
                hideMode =true;
            }
            passwordInput.setSelection(passwordInput.getText().length());
        }
    };

    // Configure Google Sign In
    private View.OnClickListener googleListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Crouton.makeText(getActivity(), R.string.system_error, Style.ALERT).show();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent,REQUEST_GOOGLE_ACCOUNT);

        }
    };
    // check is RTL
    public static boolean isRTL(Context context) {
        return context.getResources().getBoolean(R.bool.is_right_to_left);
    }
    // setUp login Or Create method
    private void loginOrCreate(){
        final   String email=emailInput.getText().toString().trim();
        final   String password= passwordInput.getText().toString().trim();
        if (mode.equals("reset")){
            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.requestFocus();
                emailInput.setSelection(emailInput.getText().length());
                Crouton.makeText(getActivity(), R.string.message_email_login, Style.ALERT).show();
            }
            else {
                //ProgressDialog
                dialog.show();
                myFireBaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //dismissMe() dialog;
                                dialog.dismiss();
                                if (task.isSuccessful())
                                    Crouton.makeText(getActivity(), R.string.check_your_mailbox, Style.INFO).show();
                                else
                                    Crouton.makeText(getActivity(), R.string.system_error, Style.ALERT).show();
                            }
                        });
            }
        }
        else {

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.requestFocus();
                emailInput.setSelection(emailInput.getText().length());
                Crouton.makeText(getActivity(), R.string.message_email_login, Style.ALERT).show();
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 4) {
                passwordInput.requestFocus();
                passwordInput.setSelection(passwordInput.getText().length());
                Crouton.makeText(getActivity(), R.string.message_password_login, Style.ALERT).show();
                return;
            }
            //ProgressDialog
            dialog.show();

            if (mode.equals("connexion")) {
                myFireBaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //dismissMe() dialog;
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    final FirebaseUser userSignIn = task.getResult().getUser();
                                    if (userSignIn.isEmailVerified()){
                                        SharedPreferences.Editor editor = preferences_LogIn.edit();
                                        editor.putString(EMAIL_CACHED, email);
                                        editor.apply();
                                       replaceFragment();
                                    }
                                    else{
                                        myFireBaseAuth.signOut();
                                        Crouton.makeText(getActivity(), R.string.activate_your_account, Style.INFO).show();
                                    }

                                }
                                else {
                                    Crouton.makeText(getActivity(), R.string.login_failed, Style.ALERT).show();
                                    passwordInput.setText("");
                                }

                            }

                        });
            } else {
                myFireBaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> createdTask) {
                                if (createdTask.isSuccessful()) {
                                    final FirebaseUser userCreated = createdTask.getResult().getUser();
                                    userCreated.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> emailVerificationTask) {
                                                    //dismissMe() dialog;
                                                    dialog.dismiss();
                                                    if (emailVerificationTask.isSuccessful()) {
                                                        Crouton.makeText(getActivity(), R.string.success_account_create, Style.INFO).show();
                                                        passwordInput.setText("");
                                                        btnSingIn.setText(R.string.sign_in_login_button);
                                                        mode = "connexion";
                                                        navigationMode.setText(R.string.create_new_account_login);
                                                        myFireBaseAuth.signOut();
                                                    }
                                                    else {
                                                        myFireBaseAuth.signOut();
                                                        userCreated.delete();
                                                        Crouton.makeText(getActivity(), R.string.failed_account_create, Style.ALERT).show();
                                                    }
                                                }
                                            });
                                }
                                else{
                                    //dismissMe() dialog;
                                    dialog.dismiss();
                                    Crouton.makeText(getActivity(), R.string.failed_account_create, Style.ALERT).show();
                                }
                            }
                        });
            }
        }

    }
    // replace fragment
    void replaceFragment(){
        android.support.v4.app.FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        DiaryListFragment diaryListFragment=new DiaryListFragment();
        fragmentTransaction.replace(R.id.main_container,diaryListFragment,"diaryListFragment");
        fragmentTransaction.commit();

    }
    // setUp onActivityResult for GoogleSignIn
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_ACCOUNT) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                fireBaseAuthWithGoogle(account);
            }
            else {
                if (mGoogleApiClient != null)
                    mGoogleApiClient.stopAutoManage(getActivity());
            }
        }

    }
    // setUp AuthWithGoogle result
    private void fireBaseAuthWithGoogle(final GoogleSignInAccount acct) {
        //ProgressDialog
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        myFireBaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //dismissMe() dialog;
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            replaceFragment();
                        }
                        else
                            Crouton.makeText(getActivity(), R.string.login_failed, Style.ALERT).show();
                    }
                });
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.hide();
    }
}

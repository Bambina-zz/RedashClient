package com.bambina.dashboardViewer.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bambina.dashboardViewer.R;
import com.bambina.dashboardViewer.RedashResponseLoader;
import com.bambina.dashboardViewer.RegexHelper;
import com.bambina.dashboardViewer.model.Redash;
import com.bambina.dashboardViewer.model.RedashResponse;

import io.realm.Realm;

/**
 * Created by hirono-mayuko on 2017/05/11.
 */

public class RegisterActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<RedashResponse> {
    private Context mContext;
    private RedashResponseLoader mLoader = null;

    public Realm mRealm;
    private int selectedRedash;
    private static final String SELECTED_REDASH = "selectedRedash";

    private static final String ID = "id";
    private static final String URL = "url";
    private static final String API_KEY = "apiKey";
    private static final String IS_PROXY = "isProxy";
    private static final String PROXY_DOMAIN = "proxyDomain";
    private static final String PROXY_PORT_NUMBER = "proxyPortNumber";

    // UI references.
    private EditText mUrlView;
    private EditText mApiKeyView;
    private EditText mProxyDomainView;
    private EditText mProxyPortNumberView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        mRealm = Realm.getDefaultInstance();
        selectedRedash = getIntent().getIntExtra(SELECTED_REDASH, 0);

        // Set up the register form.
        mUrlView = (EditText) findViewById(R.id.url);
        mApiKeyView = (EditText) findViewById(R.id.apiKey);
        mProxyDomainView = (EditText) findViewById(R.id.proxyDomain);
        mProxyPortNumberView = (EditText) findViewById(R.id.proxyPortNumber);

        mApiKeyView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
               /* if (id == R.id.register || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }*/
                return false;
            }
        });

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mLoginFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void attemptRegister() {
        if (mLoader != null) {
            return;
        }

        // Reset errors.
        mUrlView.setError(null);
        mApiKeyView.setError(null);
        mProxyDomainView.setError(null);
        mProxyPortNumberView.setError(null);

        // Store values at the time of the register attempt.
        String url = RegexHelper.extractUri(mUrlView.getText().toString());
        String apiKey = RegexHelper.removeNonVisibleChars(mApiKeyView.getText().toString());
        String proxyDomain = RegexHelper.extractDomainName(mProxyDomainView.getText().toString());
        String proxyPortNumber = RegexHelper.extractNumbers(mProxyPortNumberView.getText().toString());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid api key, if the user entered one.
        if (TextUtils.isEmpty(apiKey)) {
            mApiKeyView.setError(getString(R.string.error_field_required));
            focusView = mApiKeyView;
            cancel = true;
        }

        // Check for a valid url.
        if (TextUtils.isEmpty(url)) {
            mUrlView.setError(getString(R.string.error_field_required));
            focusView = mUrlView;
            cancel = true;
        } else if (!isUrlValid(url)) {
            mUrlView.setError(getString(R.string.error_invalid_url));
            focusView = mUrlView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            Bundle b = new Bundle();
            b.putString(URL, url);
            b.putString(API_KEY, apiKey);
            if(proxyDomain.length() == 0 || proxyPortNumber.length() == 0){
                b.putString(IS_PROXY, "false");
            } else {
                b.putString(IS_PROXY, "true");
                b.putString(PROXY_DOMAIN, proxyDomain);
                b.putString(PROXY_PORT_NUMBER, proxyPortNumber);
            }
            getLoaderManager().initLoader(0, b, this).forceLoad();
        }
    }

    private boolean isUrlValid(String url) {
        return url.contains("http");
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<RedashResponse> onCreateLoader(int i, Bundle b) {
        String url = b.getString(URL);
        String apiKey = b.getString(API_KEY);
        String isProxy = b.getString(IS_PROXY);
        String proxyDomain = null;
        String proxyPortNumber = null;

        if (isProxy.equals("true")) {
            proxyDomain = b.getString(PROXY_DOMAIN);
            proxyPortNumber = b.getString(PROXY_PORT_NUMBER);
        }
        mLoader = new RedashResponseLoader(mContext, url, apiKey, proxyDomain, proxyPortNumber);
        mLoader.forceLoad();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<RedashResponse> l, RedashResponse response) {
        Redash r = response.getRedash();

        if(response.isSuccessful()){
            Redash redash = response.getRedash();
            Redash redashSaved = mRealm.where(Redash.class)
                    .equalTo(URL, redash.getUrl())
                    .findFirst();

            mRealm.beginTransaction();
            if(redashSaved == null){
                long id = incrementId(Redash.class);
                r.id = id;
                mRealm.insert(r);
            } else {
                redashSaved.setApiKey(redash.getApiKey());
                if(redash.isProxy()){
                    redashSaved.setProxyUrl(redash.getProxyUrl());
                    redashSaved.setProxyPortNumber(redash.getProxyPortNumber());
                }
                mRealm.insertOrUpdate(redashSaved);
            }
            mRealm.commitTransaction();

            goBackMainActivity();
        } else {
            View focusView;
            if(response.getErrorMessage() == null){
                if(response.isSocketTimeoutException()){
                    mUrlView.setError(getResources().getString(R.string.error_socket_connection));
                    mProxyDomainView.setError(getResources().getString(R.string.error_socket_connection));
                    mProxyPortNumberView.setError(getResources().getString(R.string.error_socket_connection));
                } else {
                    mUrlView.setError(getResources().getString(R.string.error_invalid_url));
                }
                focusView = mUrlView;
            } else {
                mApiKeyView.setError(getResources().getString(R.string.error_invalid_apikey));
                focusView = mApiKeyView;
                System.out.println(response.getErrorMessage());
            }
            focusView.requestFocus();
        }
        showProgress(false);
        mLoader = null;
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(Loader<RedashResponse> l) {}

    public long incrementId(Class c){
        Number n = mRealm.where(c).max(ID);
        long id = (n != null) ? n.longValue() + 1 : 0;
        return id;
    }

    public void goBackMainActivity(){
        Intent i = new Intent(this, MainActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(SELECTED_REDASH, selectedRedash);
        setResult(RESULT_CANCELED, i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}

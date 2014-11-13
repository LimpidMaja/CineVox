package com.limpidgreen.cinevox;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.limpidgreen.cinevox.adapters.EventListAdapter;
import com.limpidgreen.cinevox.dao.CineVoxDBHelper;
import com.limpidgreen.cinevox.dao.EventsContentProvider;
import com.limpidgreen.cinevox.model.Event;
import com.limpidgreen.cinevox.util.Constants;

import java.util.ArrayList;


public class SettingsActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mApplication = ((CineVoxApplication) getApplication());
        mContext = this;


    } // end onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle Friends button click in the Main View.
     *
     * @param v view
     */
    public void handleFriends(View v) {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }


    public void handleLogOut(View v) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            /*
             * (non-Javadoc)
             *
             * @see
             * android.content.DialogInterface.OnClickListener#onClick(android
             * .content.DialogInterface, int)
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        mContext.deleteDatabase(CineVoxDBHelper.DATABASE_NAME);

                        AccountManager mAccountManager = AccountManager.get(mContext);
                        Account[] userAccounts = mAccountManager
                                .getAccountsByType(Constants.ACCOUNT_TYPE);
                        for (Account userAccount : userAccounts) {
                            mAccountManager.removeAccount(userAccount, null, null);
                        } // end for

                        Intent intent = new Intent(mContext, SplashScreenActivity.class);
                        mApplication.setmAccount(null);
                        startActivity(intent);

                        Intent intent2 = new Intent();
                        setResult(Constants.LOG_OUT_RESPONSE_CODE, intent2);
                        finish();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // No button clicked
                        break;
                } // end switch
            } // end onClick()
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.logout_question)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();

    }
}
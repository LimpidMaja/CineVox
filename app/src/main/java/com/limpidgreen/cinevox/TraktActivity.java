package com.limpidgreen.cinevox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.limpidgreen.cinevox.exception.APICallException;
import com.limpidgreen.cinevox.util.Constants;
import com.limpidgreen.cinevox.util.NetworkUtil;

import java.io.IOException;


public class TraktActivity extends Activity {
    /** Application */
    private CineVoxApplication mApplication;
    private Context mContext;
    /** Keep track of the progress dialog so we can dismiss it */
    private ProgressDialog mProgressDialog = null;
    private SaveTraktAsyncTask mSaveTraktAsyncTask;
    private ImportTraktAsyncTask mImportTraktAsyncTask;

    private EditText mTraktUsernameEdit;
    private EditText mTraktPasswordEdit;

    private String mTraktUsername;
    private String mTraktPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trakt);

        mApplication = ((CineVoxApplication) getApplication());
        mContext = this;

        mTraktUsernameEdit = (EditText) findViewById(R.id.trakt_username);
        mTraktPasswordEdit = (EditText) findViewById(R.id.trakt_password);
    } // end onCreate()

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handle Save Trakt button click in the Trakt View.
     *
     * @param v view
     */
    public void handleSaveTrakt(View v) {
        mTraktUsername = mTraktUsernameEdit.getText().toString().trim();
        mTraktPassword = mTraktPasswordEdit.getText().toString().trim();

        if (!mTraktUsername.isEmpty() && !mTraktPassword.isEmpty()) {
            if (mSaveTraktAsyncTask == null) {
                showProgress("Verifying Trakt Account");
                mSaveTraktAsyncTask = new SaveTraktAsyncTask();
                mSaveTraktAsyncTask.execute();
            }
        } else {
            mTraktUsername = null;
            mTraktPassword = null;
            Toast toast = Toast.makeText(this,
                    "Username or Password can not be empty!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Shows the progress UI for a lengthy operation.
     */
    private void showProgress(String msg) {
        mProgressDialog = ProgressDialog.show(this, null,
                msg, true, true, null);
    } // end showProgress()

    /**
     * Hides the progress UI for a lengthy operation.
     */
    private void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        } // end if
    } // end hideProgress()

    /**
     * Handle Import Trakt button click in the Trakt View.
     *
     * @param v view
     */
    public void handleImportTrakt(View v) {
        if (mImportTraktAsyncTask == null) {
            showProgress("Starting Trakt Import");
            mImportTraktAsyncTask = new ImportTraktAsyncTask();
            mImportTraktAsyncTask.execute();
        }
    }

    public void onSaveTraktResult() {
        hideProgress();
        mSaveTraktAsyncTask = null;
        Toast toast = Toast.makeText(this,
                "Trakt Successfully Saved", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onTraktSaveError(String msg) {
        hideProgress();
        mSaveTraktAsyncTask = null;
        if (msg != null) {
            Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this,
                    "There was a problem saving your trakt account!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onImportTraktResult() {
        hideProgress();
        mImportTraktAsyncTask = null;
        Toast toast = Toast.makeText(this,
                "Trakt.tv import started. This could take a while to complete.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onImportTraktError() {
        hideProgress();
        mImportTraktAsyncTask = null;
        Toast toast = Toast.makeText(this,
                "There was a problem importing your trakt account!", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Create Vote Event Task to send an async call to the server to vote for movies of an event.
     *
     * @author MajaDobnik
     *
     */
    private class SaveTraktAsyncTask extends AsyncTask<Void, Void, JsonElement> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected JsonElement doInBackground(Void... value) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.add("movie", new JsonObject());
            jsonObject.addProperty(NetworkUtil.PARAM_TRAKT_USERNAME, mTraktUsername);
            jsonObject.addProperty(NetworkUtil.PARAM_TRAKT_PASSWORD, mTraktPassword);
            try {
                return NetworkUtil.postWebService(jsonObject, NetworkUtil.MOVIES_URI + "/1" + NetworkUtil.TRAKT_URI, mApplication.getAPIToken());
            } catch (APICallException e) {
                Log.e(Constants.TAG, "HTTP ERROR when saving trakt - STATUS:" +  e.getMessage(), e);
                return null;
            } catch (IOException e) {
                Log.e(Constants.TAG, "IOException when saving trakt ", e);
                return null;
            } // end try-catch
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(JsonElement result) {
            if (result != null) {
                Log.i(Constants.TAG, "response: " + result.getAsJsonObject().get("status"));
                if (result.getAsJsonObject().get("status") != null && result.getAsJsonObject().get("status").getAsString().equals("OK")) {
                    onSaveTraktResult();
                } else if (result.getAsJsonObject().get("error") != null) {
                    JsonObject error = result.getAsJsonObject().get("error").getAsJsonObject();
                    //Log.i(Constants.TAG, "errorArray.get(0): " + errorArray.get(0));
                    Log.i(Constants.TAG, "error: " + error.get("trakt_password"));
                    if (error.get("trakt_password") != null) {
                        String msg = error.get("trakt_password").getAsString().replace("\"[", "").replace("]\"", "");
                        onTraktSaveError(msg);
                    } else {
                        onTraktSaveError(null);
                    }
                } else {
                    onTraktSaveError(null);
                } // end if-else
            } else {
                onTraktSaveError(null);
            } // end if
        } // end onPostExecute()
    } // end SaveTraktAsyncTask()

    /**
     * Create Vote Event Task to send an async call to the server to vote for movies of an event.
     *
     * @author MajaDobnik
     *
     */
    private class ImportTraktAsyncTask extends AsyncTask<Void, Void, JsonElement> {
        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected JsonElement doInBackground(Void... value) {
            try {
                return NetworkUtil.getWebService(null, NetworkUtil.TRAKT_IMPORT_URI, mApplication.getAPIToken());
            } catch (APICallException e) {
                Log.e(Constants.TAG, "HTTP ERROR when importing trakt - STATUS:" +  e.getMessage(), e);
                return null;
            } catch (IOException e) {
                Log.e(Constants.TAG, "IOException when importing trakt ", e);
                return null;
            } // end try-catch
        } // end doInBackground()

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(JsonElement result) {
            if (result != null) {
                Log.i(Constants.TAG, "response: " + result.getAsJsonObject().get("status"));
                if (result.getAsJsonObject().get("status") != null && result.getAsJsonObject().get("status").getAsString().equals("OK")) {
                    onImportTraktResult();
                } else {
                    onImportTraktError();
                } // end if-else
            } else {
                onImportTraktError();
            } // end if
        } // end onPostExecute()
    } // end ImportTraktAsyncTask()
}
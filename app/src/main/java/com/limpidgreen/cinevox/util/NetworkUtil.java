/**
 * NetworkUtil.java
 *
 * 9.10.2014
 *
 * Copyright 2014 Maja Dobnik
 * All Rights Reserved
 */
package com.limpidgreen.cinevox.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.limpidgreen.cinevox.exception.APICallException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Utility methods for API communication.
 *
 * @author MajaDobnik
 *
 */
public class NetworkUtil {
    /** POST parameter name for the user's facebook id */
    public static final String PARAM__FACEBOOK_UID = "uid";
    /** POST parameter name for the user's email */
    public static final String PARAM_EMAIL = "email";
    /** POST parameter name for the user's facebook access token */
    public static final String PARAM_ACCESS_TOKEN = "access_token";
    /** POST parameter name for the user's facebook access token expiration date */
    public static final String PARAM_ACCESS_TOKEN_EXPIRES = "expires_at";
    /** POST parameter name for result's info data */
    public static final String PARAM_INFO = "info";

    /** Request timeout */
    public static final int HTTP_REQUEST_TIMEOUT_MS = 30 * 1000;
    /** Base URL for the API Service */
    public static final String BASE_URL = "http://192.168.1.103:3000";
    /** URI for authentication service */
    public static final String AUTH_URI = BASE_URL + "/api/auth/facebook/callback";

    /**
     * Returns true if the Internet connection is available.
     *
     * @return true if the Internet connection is available
     */
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    } // end isNetworkAvailable()

    /**
     * Configures the httpClient to connect to the URL provided.
     */
    public static HttpClient getHttpClient() {
        HttpClient httpClient = new DefaultHttpClient();
        final HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params,
                HTTP_REQUEST_TIMEOUT_MS);
        HttpConnectionParams.setSoTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        ConnManagerParams.setTimeout(params, HTTP_REQUEST_TIMEOUT_MS);
        return httpClient;
    } // end getHttpClient()

    /**
     * Connects to server, authenticates the provided user data and returns an API access key.
     *
     * @param userId
     *            The facebook user id
     * @param accessToken
     *            The facebook access token
     * @param expirationDate
     *            The facebook access token expiration date
     * @return API Access key returned by the server (or null)
     */
    public static String authenticate(String userId, String accessToken, Date expirationDate) {
        final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(PARAM__FACEBOOK_UID, userId));
        params.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN, accessToken));
        params.add(new BasicNameValuePair(PARAM_ACCESS_TOKEN_EXPIRES, String.valueOf(expirationDate.getTime())));

        try {
            String result = postWebService(params, AUTH_URI);

            final JSONObject obj = new JSONObject(result);
            String APIToken = obj.getString(PARAM_ACCESS_TOKEN);
            if (APIToken != null && APIToken.length() > 0) {
                Log.i(Constants.TAG, "API KEY: " + APIToken);
                return APIToken;
            } else {
                Log.e(Constants.TAG,
                        "Error authenticating" + obj.getString(PARAM_INFO));
                return null;
            } // end if-else
        } catch (APICallException e) {
            Log.e(Constants.TAG, "HTTP ERROR when getting API token - STATUS:" +  e.getMessage(), e);
            return null;
        } catch (IOException e) {
            Log.e(Constants.TAG, "IOException when getting API token", e);
            return null;
        } catch (JSONException e) {
            Log.e(Constants.TAG, "JSONException when getting API token", e);
            return null;
        } // end try-catch
    } // end authenticate()

    /**
     * Connects to API server, sends post service.
     *
     * @param params
     * @param url
     * @return server result
     */
    public static String postWebService(ArrayList<NameValuePair> params,
                                        String url) throws APICallException, IOException {
        final HttpResponse resp;
        final HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params);
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        } // end try-catch
        final HttpPost post = new HttpPost(url);
        post.addHeader(entity.getContentType());
        post.setEntity(entity);
        try {
            resp = getHttpClient().execute(post);
            StringBuilder builder = new StringBuilder();
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStream inputStream = (resp.getEntity() != null) ? resp
                        .getEntity().getContent() : null;
                if (inputStream != null) {
                    BufferedReader inputReader = new BufferedReader(
                            new InputStreamReader(inputStream));
                    String line;
                    while ((line = inputReader.readLine()) != null) {
                        builder.append(line);
                    } // end while
                    inputReader.close();
                    inputStream.close();
                } // end if
            } else {
                throw new APICallException(String.valueOf(resp.getStatusLine().getStatusCode()));
            } // end if-else
            return builder.toString();
        } catch (final IOException e) {
            throw  e;
        } finally {
        } // end try-catch-finally
    } // end postWebService()
}

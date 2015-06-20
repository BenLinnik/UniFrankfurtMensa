/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bennystech.unifrankfurtmensa.syncadapter;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.bennystech.unifrankfurtmensa.MensaPlan;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in { SyncService}, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";
    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;
    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }
    /*
        * Specify the code you want to run in the sync adapter. The entire
        * sync adapter runs in a background thread, so you don't have to set
        * up your own background processing.
        */
    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {
    /*
     * Put the data transfer code here.
     */
        Log.i(TAG, "Beginning network synchronization");
        try {
            final URL location = new URL("http://www.studentenwerkfrankfurt.de/index.php?id=583&no_cache=1&type=98");
            Log.i(TAG, "Streaming data from network: " + location);
            Document mainsitehtml = RetrieveHTML(location);
            if (mainsitehtml != null) {
                MensaPlan mensaPlanPiGaumen = new MensaPlan(mainsitehtml);
                Log.d(TAG, "Mensa name: " + mensaPlanPiGaumen.getMensaTitle());
            }
        } catch (MalformedURLException e) {
        Log.e(TAG, "Feed URL is malformed", e);
        syncResult.stats.numParseExceptions++;
        return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        }
        Log.i(TAG, "Network synchronization complete");

    }

    Document RetrieveHTML(URL site) {
        try {
            Log.d(TAG, "checking site " + site.toExternalForm());
            return Jsoup.connect(site.toExternalForm()).get();
        } catch (Exception e) {
            return null;
        }

    }
}

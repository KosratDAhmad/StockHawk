/*
 * Copyright 2016 Kosrat D. Ahmed
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sam_chordas.android.stockhawk.service.HistoricData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by kosrat on 7/14/16.
 */
public class HistoricData {

    private final String BASE_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    private final String END_URL = "/chartdata;type=quote;range=1y/json";

    private ArrayList<StockSymbol> mStockSymbols;
    private StockMeta mStockMeta;
    private Context mContext;
    private HistoricCallback mCallback;

    private static final String JSON_SERIES = "series";
    private static final String JSON_DATE = "Date";
    private static final String JSON_CLOSE = "close";

    private static final String JSON_META = "meta";
    private static final String JSON_COMPANY_NAME = "Company-Name";
    private static final String JSON_FIRST_TRADE = "first-trade";
    private static final String JSON_LAST_TRADE = "last-trade";
    private static final String JSON_CURRENCY = "currency";
    private static final String JSON_CLOSE_PRICE = "previous_close_price";

    /**
     * Interface to interact with the callee class to notify regarding success, or errors if any.
     */
    public interface HistoricCallback {
        void onSuccess(StockMeta stockMeta);

        void onFailure();
    }

    //to indicate errors encountered.
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_ERROR_JSON, STATUS_ERROR_SERVER, STATUS_ERROR_NO_NETWORK})
    public @interface HistoricalDataStatuses {
    }

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR_JSON = 1;
    public static final int STATUS_ERROR_SERVER = 2;
    public static final int STATUS_ERROR_NO_NETWORK = 3;

    public HistoricData(Context context, HistoricCallback callback) {
        mContext = context;
        mCallback = callback;
        mStockSymbols = new ArrayList<>();
    }

    public void getHistoricData(String symbol) {
        final String url = BASE_URL + symbol + END_URL;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    String json = fetchData(url);
                    getStockMetaFromJson(json);
                } catch (IOException e) {
                    e.printStackTrace();
                    setHistoricalDataStatus(STATUS_ERROR_SERVER);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mCallback != null) {
                    @HistoricData.HistoricalDataStatuses
                    int status = PreferenceManager.getDefaultSharedPreferences(mContext)
                            .getInt(mContext.getString(R.string.pref_historic_status_key), -1);
                    if (status == STATUS_OK) {
                        mCallback.onSuccess(mStockMeta);
                    } else {
                        mCallback.onFailure();
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private String fetchData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private void getStockMetaFromJson(String json) {
        try {
            json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
            JSONObject mainObject = new JSONObject(json);

            JSONArray series_data = mainObject.getJSONArray(JSON_SERIES);
            for (int i = 0; i < series_data.length(); i++) {
                JSONObject singleObject = series_data.getJSONObject(i);
                String date = singleObject.getString(JSON_DATE);
                double close = singleObject.getDouble(JSON_CLOSE);
                mStockSymbols.add(new StockSymbol(date, (float) close));
            }

            JSONObject meta_data = mainObject.getJSONObject(JSON_META);
            String companyName = meta_data.getString(JSON_COMPANY_NAME);
            String firstTrade = meta_data.getString(JSON_FIRST_TRADE);
            String lastTrade = meta_data.getString(JSON_LAST_TRADE);
            String currency = meta_data.getString(JSON_CURRENCY);
            double previousClosePrice = meta_data.getDouble(JSON_CLOSE_PRICE);

            mStockMeta = new StockMeta(companyName, firstTrade, lastTrade, currency, previousClosePrice, mStockSymbols);

            if (mCallback != null) {
                setHistoricalDataStatus(STATUS_OK);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            setHistoricalDataStatus(STATUS_ERROR_JSON);
        }
    }

    @SuppressWarnings("ResourceType")
    public void setHistoricalDataStatus(@HistoricalDataStatuses int status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        sp.edit().putInt(mContext.getString(R.string.pref_historic_status_key), status).commit();
    }
}

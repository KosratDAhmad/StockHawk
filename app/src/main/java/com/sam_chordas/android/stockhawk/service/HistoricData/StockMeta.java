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

import java.util.ArrayList;

/**
 * Created by kosrat on 7/14/16.
 */
public class StockMeta {

    private String mStockName;
    private String mFirstTrade;
    private String mLastTrade;
    private String mCurrency;
    private double mBidPrice;
    private ArrayList<StockSymbol> mStockSymbols;

    public StockMeta(String stockName, String firstTrade, String lastTrade, String currency,
                     double bidPrice, ArrayList<StockSymbol> stockSymbols) {
        this.mStockName = stockName;
        this.mFirstTrade = firstTrade;
        this.mLastTrade = lastTrade;
        this.mCurrency = currency;
        this.mBidPrice = bidPrice;
        this.mStockSymbols = stockSymbols;
    }

    public String getStockName() {
        return mStockName;
    }

    public String getFirstTrade() {
        return mFirstTrade;
    }

    public String getLastTrade() {
        return mLastTrade;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public double getBidPrice() {
        return mBidPrice;
    }

    public ArrayList<StockSymbol> getStockSymbols() {
        return mStockSymbols;
    }
}

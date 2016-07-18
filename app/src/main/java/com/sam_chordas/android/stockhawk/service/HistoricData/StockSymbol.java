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

/**
 * Created by kosrat on 7/14/16.
 */
public class StockSymbol {

    private String mDate;
    private float mClose;

    public StockSymbol(String date, float close) {
        mDate = date;
        mClose = close;
    }

    public String getDate() {
        return mDate;
    }

    public float getClose() {
        return mClose;
    }
}

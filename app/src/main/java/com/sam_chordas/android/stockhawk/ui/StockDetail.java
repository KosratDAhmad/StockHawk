package com.sam_chordas.android.stockhawk.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.service.HistoricData.HistoricData;
import com.sam_chordas.android.stockhawk.service.HistoricData.StockMeta;
import com.sam_chordas.android.stockhawk.service.HistoricData.StockSymbol;

import java.util.ArrayList;

/**
 * Created by kosrat on 7/14/16.
 */
public class StockDetail extends AppCompatActivity implements HistoricData.HistoricCallback {

    HistoricData historicData;
    ArrayList<StockSymbol> stockSymbols;

    LineChartView lineChart;
    LinearLayout linearLayout;

    TextView stockName, stockSymbol, firstTrade, lastTrade, currency, tvBidPrice, exchangeName;

    String symbol;
    String bidPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);

        //Binding views
        lineChart = (LineChartView) findViewById(R.id.linechart);
//        lineChart.setNoDataText(getString(R.string.loading_stock_data));

        stockName = (TextView) findViewById(R.id.stock_detail_name);
        stockSymbol = (TextView) findViewById(R.id.stock_detail_symbol);
        firstTrade = (TextView) findViewById(R.id.stock_detail_first);
        lastTrade = (TextView) findViewById(R.id.stock_detail_last);
        currency = (TextView) findViewById(R.id.stock_detail_currency);
        tvBidPrice = (TextView) findViewById(R.id.stock_detail_bid);

        //Getting Values from intents
        symbol = getIntent().getStringExtra(QuoteColumns.SYMBOL);
        bidPrice = getIntent().getStringExtra(QuoteColumns.BIDPRICE);

        //Setting values to the text
        stockSymbol.setText(symbol);
        tvBidPrice.setText(bidPrice);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
            bar.setTitle(String.format(getString(R.string.symbol_detail), symbol));
        }

        historicData = new HistoricData(this, this);

//        if(Utils.isNetworkAvailable(this)) {
        historicData.getHistoricData(symbol);
//        }else{
//            historicData.setHistoricalDataStatus(HistoricData.STATUS_ERROR_NO_NETWORK);
//            onFailure();
//        }
    }

    @Override
    public void onSuccess(StockMeta stockMeta) {

        stockName.setText(stockMeta.getStockName());
        firstTrade.setText(stockMeta.getFirstTrade());
        lastTrade.setText(stockMeta.getLastTrade());
        currency.setText(stockMeta.getCurrency());

        LineSet dataset = new LineSet();
        ArrayList<StockSymbol> stockSymbols = stockMeta.getStockSymbols();

        for (int i = 0; i < stockSymbols.size(); i++) {
            String label = stockSymbols.get(i).getDate();
            float value = stockSymbols.get(i).getClose();
            dataset.addPoint(label, value);
        }

        dataset.setColor(Color.parseColor("#758cbb"))
                .setFill(Color.parseColor("#2d374c"))
                .setDotsColor(Color.parseColor("#758cbb"))
                .setThickness(4)
                .setDashed(new float[]{10f, 10f})
                .beginAt(5);
        lineChart.addData(dataset);
        lineChart.setAxisThickness(3);
        lineChart.setXLabels(AxisController.LabelPosition.OUTSIDE);
        lineChart.setYLabels(AxisController.LabelPosition.INSIDE);
        lineChart.show();
    }

    @Override
    public void onFailure() {

//        String errorMessage = "";
//
//        @HistoricData.HistoricalDataStatuses
//        int status = PreferenceManager.getDefaultSharedPreferences(this)
//                .getInt(HistoricData.HISTORICAL_DATA_STATUS, -1);
//
//        switch (status) {
//            case HistoricData.STATUS_ERROR_JSON:
//                errorMessage += getString(R.string.data_error_json);
//                break;
//            case HistoricData.STATUS_ERROR_NO_NETWORK:
//                errorMessage += getString(R.string.data_no_internet);
//                break;
//            case HistoricData.STATUS_ERROR_PARSE:
//                errorMessage += getString(R.string.data_error_parse);
//                break;
//            case HistoricData.STATUS_ERROR_UNKNOWN:
//                errorMessage += getString(R.string.data_unknown_error);
//                break;
//            case HistoricData.STATUS_ERROR_SERVER:
//                errorMessage += getString(R.string.data_server_down);
//                break;
//            case HistoricData.STATUS_OK:
//                errorMessage += getString(R.string.data_no_error);
//                break;
//            default:
//                break;
//        }
//
//        lineChart.setNoDataText(errorMessage);
//
//        final Snackbar snackbar = Snackbar
//                .make(linearLayout, getString(R.string.no_data_show) + errorMessage, Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.retry, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        historicData.getHistoricData(symbol);
//                    }
//                })
//                .setActionTextColor(Color.GREEN);
//
//        View subview = snackbar.getView();
//        TextView tv = (TextView) subview.findViewById(android.support.design.R.id.snackbar_text);
//        tv.setTextColor(Color.RED);
//        snackbar.show();
    }
}

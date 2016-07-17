package com.sam_chordas.android.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by kosrat on 7/15/16.
 */
public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                        new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);

                if (data.moveToPosition(position)) {

                    String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
                    String bid = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
                    String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
                    String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));

                    views.setTextViewText(R.id.widget_stock_symbol, symbol);
                    views.setTextViewText(R.id.widget_bid_price, bid);

                    if (Utils.showPercent) {
                        views.setTextViewText(R.id.widget_change, percentChange);
                    } else {
                        views.setTextViewText(R.id.widget_change, change);
                    }

                    if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                        views.setInt(R.id.widget_change, "setBackgroundResource",
                                android.graphics.Color.BLACK);
                    } else {
                        views.setInt(R.id.widget_change, "setBackgroundResource",
                                R.drawable.percent_change_pill_red);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                        views.setContentDescription(R.id.widget_stock_symbol,
                                getString(R.string.stock_symbol_content_description) + " " + symbol);
                        views.setContentDescription(R.id.widget_bid_price,
                                getString(R.string.bid_price_content_description) + " " + bid);
                        if (Utils.showPercent) {
                            views.setContentDescription(R.id.widget_change,
                                    getString(R.string.change_content_description) + " " + percentChange);
                        } else {
                            views.setContentDescription(R.id.widget_change,
                                    getString(R.string.change_content_description) + " " + change);
                        }
                    }

                    final Intent fillInIntent = new Intent();
                    fillInIntent.putExtra(QuoteColumns.SYMBOL, data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                    fillInIntent.putExtra(QuoteColumns.BIDPRICE, data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                    views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                }
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(data.getColumnIndex(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

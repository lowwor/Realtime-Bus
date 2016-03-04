package com.lowwor.realtimebus.ui.widget;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

public class LimitArrayAdapter<T> extends ArrayAdapter<T> {
 
    final int LIMIT = 3;
    //overload other constructors you're using 
    public LimitArrayAdapter(Context context, int textViewResourceId,
                             List<T> objects) {
        super(context, textViewResourceId, objects);
    } 
 
    @Override 
    public int getCount() { 
        return Math.min(LIMIT, super.getCount());
    } 
 
} 
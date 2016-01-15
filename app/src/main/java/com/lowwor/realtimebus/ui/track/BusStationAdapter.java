package com.lowwor.realtimebus.ui.track;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.lowwor.realtimebus.R;
import com.lowwor.realtimebus.databinding.ItemStationBinding;
import com.lowwor.realtimebus.data.model.BusStation;
import com.lowwor.realtimebus.viewmodel.BusStationViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lowworker on 2015/10/15.
 */
public class BusStationAdapter extends RecyclerView.Adapter<BusStationAdapter.BindingHolder >  {

    private List<BusStation> mBusStations;
    private Context mContext;

    public BusStationAdapter(Context context ) {
        mContext = context;
        mBusStations = new ArrayList<>();
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemStationBinding busStationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_station,
                parent,
                false);
        return new BindingHolder(busStationBinding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        ItemStationBinding busStationBinding = holder.binding;
        busStationBinding.setViewModel(new BusStationViewModel(mContext, mBusStations.get(position)));
    }

    public void setItems(List<BusStation> busStations) {
        mBusStations = busStations;
        notifyDataSetChanged();
    }

    public void addItem(BusStation busStation) {
        if (!mBusStations.contains(busStation)) {
            mBusStations.add(busStation);
            notifyItemInserted(mBusStations.size() - 1);
        } else {
            mBusStations.set(mBusStations.indexOf(busStation), busStation);
            notifyItemChanged(mBusStations.indexOf(busStation));
        }
    }


    @Override
    public int getItemCount() {
        return mBusStations.size();
    }

    public  static class BindingHolder  extends RecyclerView.ViewHolder {
        private ItemStationBinding binding;

        public BindingHolder(ItemStationBinding binding) {
            super(binding.cardView);
            this.binding = binding;

        }
    }
}

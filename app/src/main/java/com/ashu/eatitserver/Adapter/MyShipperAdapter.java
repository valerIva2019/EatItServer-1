package com.ashu.eatitserver.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ashu.eatitserver.Callback.IRecyclerClickListener;
import com.ashu.eatitserver.EventBus.UpdateShipperEvent;
import com.ashu.eatitserver.Model.ShipperModel;
import com.ashu.eatitserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyShipperAdapter extends RecyclerView.Adapter<MyShipperAdapter.MyViewHolder>  {

    Context context;
    List<ShipperModel> shipperModelList;

    public MyShipperAdapter(Context context, List<ShipperModel> shipperModelList) {
        this.context = context;
        this.shipperModelList = shipperModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyShipperAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_shipper, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_name.setText(shipperModelList.get(position).getName());
        holder.txt_phone.setText(shipperModelList.get(position).getPhone());
        holder.btn_enable.setChecked(shipperModelList.get(position).isActive());

        holder.btn_enable.setOnCheckedChangeListener((compoundButton, b) -> EventBus.getDefault().postSticky(new UpdateShipperEvent(shipperModelList.get(position), b)));

    }

    @Override
    public int getItemCount() {
        return shipperModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Unbinder unbinder;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txt_name;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btn_enable)
        SwitchCompat btn_enable;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }

    }

}

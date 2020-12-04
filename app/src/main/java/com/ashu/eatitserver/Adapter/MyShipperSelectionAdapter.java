package com.ashu.eatitserver.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class MyShipperSelectionAdapter extends RecyclerView.Adapter<MyShipperSelectionAdapter.MyViewHolder>  {

    Context context;
    List<ShipperModel> shipperModelList;
    private ImageView lastCheckedImageView = null;
    private ShipperModel selectedShipper = null;

    public MyShipperSelectionAdapter(Context context, List<ShipperModel> shipperModelList) {
        this.context = context;
        this.shipperModelList = shipperModelList;
    }

    @NonNull
    @Override
    public MyShipperSelectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyShipperSelectionAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_shipper_selected, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyShipperSelectionAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(shipperModelList.get(position).getName());
        holder.txt_phone.setText(shipperModelList.get(position).getPhone());
        holder.setListener((view, pos) -> {
            if (lastCheckedImageView  != null) {
                lastCheckedImageView.setImageResource(0);
            }
            holder.img_checked.setImageResource(R.drawable.ic_baseline_done_24);
            lastCheckedImageView = holder.img_checked;
            selectedShipper = shipperModelList.get(pos);
        });
    }

    public ShipperModel getSelectedShipper() {
        return selectedShipper;
    }

    @Override
    public int getItemCount() {
        return shipperModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txt_name;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_phone)
        TextView txt_phone;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_checked)
        ImageView img_checked;

        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }

}

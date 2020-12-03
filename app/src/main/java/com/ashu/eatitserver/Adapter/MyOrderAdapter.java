package com.ashu.eatitserver.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.FtsOptions;

import com.ashu.eatitserver.Callback.IRecyclerClickListener;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.EventBus.CategoryClick;
import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.R;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    Context context;
    List<OrderModel> orderModelList;
    SimpleDateFormat simpleDateFormat;

    public MyOrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOrderAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(orderModelList.get(position).getCartItemList().get(0).getFoodImg())
                .into(holder.img_food_image);
        holder.txt_order_number.setText(orderModelList.get(position).getKey());

        Common.setSpanStringColor("Order Date: ", simpleDateFormat.format(orderModelList.get(position).getCreateDate()),
                holder.txt_time, Color.parseColor("#333639"));
        Common.setSpanStringColor("Order Status: ", Common.convertStatusToString(orderModelList.get(position).getOrderStatus()),
                holder.txt_order_status, Color.parseColor("#00579A"));
        Common.setSpanStringColor("Name: ",orderModelList.get(position).getUserName(),
                holder.txt_name, Color.parseColor("#00574B"));
        Common.setSpanStringColor("Num of Items: ",orderModelList.get(position).getCartItemList() == null ? "0":
                String.valueOf(orderModelList.get(position).getCartItemList().size()),
                holder.txt_num_item, Color.parseColor("#4B647D"));

    }

    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Unbinder unbinder;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txt_name;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_time)
        TextView txt_time;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_order_status)
        TextView txt_order_status;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_num_item)
        TextView txt_num_item;

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

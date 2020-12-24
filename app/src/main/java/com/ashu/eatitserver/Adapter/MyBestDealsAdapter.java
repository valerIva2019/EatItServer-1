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
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.EventBus.CategoryClick;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.R;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyBestDealsAdapter extends RecyclerView.Adapter<MyBestDealsAdapter.MyViewHolder> {

    Context context;
    List<BestDealModel> bestDealModelList;

    public MyBestDealsAdapter(Context context, List<BestDealModel> bestDealModelList) {
        this.context = context;
        this.bestDealModelList = bestDealModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Glide.with(context).load(bestDealModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(bestDealModelList.get(position).getName()));

        //event
        holder.setListener((view, pos) -> {
//            Common.categorySelected = categoryModelList.get(pos);
//            EventBus.getDefault().postSticky(new CategoryClick(true, categoryModelList.get(pos)));
        });

    }

    @Override
    public int getItemCount() {
        return bestDealModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_category)
        ImageView category_image;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_category)
        TextView category_name;

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

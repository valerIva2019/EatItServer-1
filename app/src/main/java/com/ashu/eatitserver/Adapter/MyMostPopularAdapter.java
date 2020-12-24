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
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;
import com.ashu.eatitserver.R;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyMostPopularAdapter extends RecyclerView.Adapter<MyMostPopularAdapter.MyViewHolder> {

    Context context;
    List<PopularCategoryModel> popularCategoryModelList;

    public MyMostPopularAdapter(Context context, List<PopularCategoryModel> popularCategoryModelList) {
        this.context = context;
        this.popularCategoryModelList = popularCategoryModelList;
    }

    @NonNull
    @Override
    public MyMostPopularAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyMostPopularAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_category_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyMostPopularAdapter.MyViewHolder holder, int position) {

        Glide.with(context).load(popularCategoryModelList.get(position).getImage())
                .into(holder.category_image);
        holder.category_name.setText(new StringBuilder(popularCategoryModelList.get(position).getName()));

        //event
        holder.setListener((view, pos) -> {
//            Common.categorySelected = categoryModelList.get(pos);
//            EventBus.getDefault().postSticky(new CategoryClick(true, categoryModelList.get(pos)));
        });

    }

    @Override
    public int getItemCount() {
        return popularCategoryModelList.size();
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

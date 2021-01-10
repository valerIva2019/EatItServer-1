package com.ashu.eatitserver.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashu.eatitserver.Callback.IRecyclerClickListener;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.FoodModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;
import com.ashu.eatitserver.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyFoodListAdapter extends RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder> {

    private Context context;
    private List<FoodModel> foodModelList;

    private ExpandableLayout lastExpandable;


    public MyFoodListAdapter(Context context, List<FoodModel> foodModelList) {
        this.context = context;
        this.foodModelList = foodModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(foodModelList.get(position).getImage()).into(holder.img_food_image);
        holder.txt_food_price.setText(new StringBuilder("$")
                .append(foodModelList.get(position).getPrice()));
        holder.txt_food_name.setText(new StringBuilder()
                .append(foodModelList.get(position).getName()));

        //event
        holder.setListener((view, pos) -> {
            Common.selectedFood = foodModelList.get(pos);
            Common.selectedFood.setKey(String.valueOf(pos));

            if (lastExpandable != null && lastExpandable.isExpanded()) lastExpandable.collapse();

            if (!holder.expandable_layout.isExpanded()) {
                holder.expandable_layout.setSelected(true);
                holder.expandable_layout.expand();
            } else {
                holder.expandable_layout.collapse();
                holder.expandable_layout.setSelected(false);
            }
            lastExpandable = holder.expandable_layout;
        });

        holder.btn_best_deal.setOnClickListener(v -> makeFoodToBestDealOfRestaurant(foodModelList.get(position)));

        holder.btn_most_popular.setOnClickListener(v -> makeFoodToPopularOfRestaurant(foodModelList.get(position)));
    }

    private void makeFoodToPopularOfRestaurant(FoodModel foodModel) {
        PopularCategoryModel popularCategoryModel = new PopularCategoryModel();
        popularCategoryModel.setName(foodModel.getName());
        popularCategoryModel.setMenu_id(Common.categorySelected.getMenu_id());
        popularCategoryModel.setFood_id(foodModel.getId());
        popularCategoryModel.setImage(foodModel.getImage());

        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.MOST_POPULAR)
                .child(popularCategoryModel.getMenu_id() + "_" + popularCategoryModel.getFood_id())
                .setValue(popularCategoryModel)
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Create Most Popular successfully !", Toast.LENGTH_SHORT).show());

    }

    private void makeFoodToBestDealOfRestaurant(FoodModel foodModel) {
        BestDealModel bestDealModel = new BestDealModel();
        bestDealModel.setName(foodModel.getName());
        bestDealModel.setMenu_id(Common.categorySelected.getMenu_id());
        bestDealModel.setFood_id(foodModel.getId());
        bestDealModel.setImage(foodModel.getImage());

        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.BEST_DEALS)
                .child(bestDealModel.getMenu_id() + "_" + bestDealModel.getFood_id())
                .setValue(bestDealModel)
                .addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Create Best deal successfully !", Toast.LENGTH_SHORT).show());

    }

    @Override
    public int getItemCount() {
        return foodModelList.size();
    }

    public FoodModel getItemAtPosition(int pos) {
        return foodModelList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_food_name)
        TextView txt_food_name;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_food_price)
        TextView txt_food_price;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_food_image)
        ImageView img_food_image;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.expandable_layout)
        ExpandableLayout expandable_layout;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btn_best_deal)
        Button btn_best_deal;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.btn_most_popular)
        Button btn_most_popular;


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

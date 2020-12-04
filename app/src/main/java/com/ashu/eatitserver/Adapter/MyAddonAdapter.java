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
import com.ashu.eatitserver.EventBus.SelectAddonModel;
import com.ashu.eatitserver.EventBus.SelectSizeModel;
import com.ashu.eatitserver.EventBus.UpdateAddonModel;
import com.ashu.eatitserver.EventBus.UpdateSizeModel;
import com.ashu.eatitserver.Model.AddonModel;
import com.ashu.eatitserver.Model.SizeModel;
import com.ashu.eatitserver.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyAddonAdapter extends RecyclerView.Adapter<MyAddonAdapter.MyViewHolder> {

    Context context;
    List<AddonModel> addonModels;
    UpdateAddonModel updateAddonModel;
    int editPos;

    public MyAddonAdapter(Context context, List<AddonModel> addonModels) {
        this.context = context;
        this.addonModels = addonModels;
        editPos = -1;
        updateAddonModel = new UpdateAddonModel();


    }

    @NonNull
    @Override
    public MyAddonAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyAddonAdapter.MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_size_addon_display, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyAddonAdapter.MyViewHolder holder, int position) {
        holder.txt_name.setText(addonModels.get(position).getName());
        holder.txt_price.setText(String.valueOf(addonModels.get(position).getPrice()));

        //event
        holder.img_delete.setOnClickListener(view -> {
            addonModels.remove(position);
            notifyItemRemoved(position);
            updateAddonModel.setAddonModel(addonModels); //set for event
            EventBus.getDefault().postSticky(updateAddonModel); //send event

        });

        holder.setListener((view, pos) -> {
            editPos = position;
            EventBus.getDefault().postSticky(new SelectAddonModel(addonModels.get(pos)));
        });
    }

    @Override
    public int getItemCount() {
        return addonModels.size();
    }

    public void addNewSize(AddonModel addonModel) {
        addonModels.add(addonModel);
        notifyItemInserted(addonModels.size() - 1);
        updateAddonModel.setAddonModel(addonModels);
        EventBus.getDefault().postSticky(updateAddonModel);
    }

    public void editSize(AddonModel addonModel) {
        if (editPos != -1) {
            addonModels.set(editPos, addonModel);
            notifyItemChanged(editPos);
            editPos = -1; //resetting variable after update
            updateAddonModel.setAddonModel(addonModels);
            EventBus.getDefault().postSticky(updateAddonModel);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_name)
        TextView txt_name;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.txt_price)
        TextView txt_price;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.img_delete)
        ImageView img_delete;

        Unbinder unbinder;
        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> listener.onItemClickListener(view, getAdapterPosition()));
        }
    }
}


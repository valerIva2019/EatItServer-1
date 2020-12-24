package com.ashu.eatitserver.ui.best_deals;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashu.eatitserver.Adapter.MyBestDealsAdapter;
import com.ashu.eatitserver.Adapter.MyCategoriesAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Common.MySwiperHelper;
import com.ashu.eatitserver.EventBus.MenuItemBack;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.ui.category.CategoryViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BestDealsFragment extends Fragment {

    private BestDealsViewModel bestDealsViewModel;
    Unbinder unbinder;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_best_deal)
    RecyclerView recycler_best_deal;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyBestDealsAdapter adapter;
    List<BestDealModel> bestDealModels;

    public static BestDealsFragment newInstance() {
        return new BestDealsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        bestDealsViewModel =
                new ViewModelProvider(this).get(BestDealsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_best_deals, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        bestDealsViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        bestDealsViewModel.getBestDealsListMutable().observe(getViewLifecycleOwner(), list -> {
            dialog.dismiss();
            bestDealModels = list;
            adapter = new MyBestDealsAdapter(getContext(), bestDealModels);
            recycler_best_deal.setAdapter(adapter);
            recycler_best_deal.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }
    private void initViews() {

        setHasOptionsMenu(true);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_best_deal.setLayoutManager(layoutManager);
        recycler_best_deal.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}
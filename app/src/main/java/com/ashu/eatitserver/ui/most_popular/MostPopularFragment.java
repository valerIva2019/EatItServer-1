package com.ashu.eatitserver.ui.most_popular;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.ashu.eatitserver.Adapter.MyBestDealsAdapter;
import com.ashu.eatitserver.Adapter.MyMostPopularAdapter;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.ui.best_deals.BestDealsViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class MostPopularFragment extends Fragment {

    private MostPopularViewModel mostPopularViewModel;
    Unbinder unbinder;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_most_popular)
    RecyclerView recycler_most_popular;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyMostPopularAdapter adapter;
    List<PopularCategoryModel> popularCategoryModels;

    public static MostPopularFragment newInstance() {
        return new MostPopularFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mostPopularViewModel =
                new ViewModelProvider(this).get(MostPopularViewModel.class);
        View root = inflater.inflate(R.layout.fragment_best_deals, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        mostPopularViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), "" + s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        mostPopularViewModel.getPopularCategoryModel().observe(getViewLifecycleOwner(), list -> {
            dialog.dismiss();
            popularCategoryModels = list;
            adapter = new MyMostPopularAdapter(getContext(), popularCategoryModels);
            recycler_most_popular.setAdapter(adapter);
            recycler_most_popular.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initViews() {
        setHasOptionsMenu(true);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_most_popular.setLayoutManager(layoutManager);
        recycler_most_popular.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }


}
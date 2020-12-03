package com.ashu.eatitserver.ui.order;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashu.eatitserver.Adapter.MyCategoriesAdapter;
import com.ashu.eatitserver.Adapter.MyOrderAdapter;
import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.ui.category.CategoryViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class OrderFragment extends Fragment {

    private OrderViewModel orderViewModel;

    Unbinder unbinder;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_order)
    RecyclerView recycler_order;

    LayoutAnimationController layoutAnimationController;
    MyOrderAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();

        orderViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show());
        orderViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(), orderModels -> {
            if (orderModels != null) {
                adapter = new MyOrderAdapter(getContext(), orderModels);
                recycler_order.setAdapter(adapter);
                recycler_order.setLayoutAnimation(layoutAnimationController);
            }
        });
        return root;
    }

    private void initViews() {
        recycler_order.setHasFixedSize(true);
        recycler_order.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

    }
}
package com.ashu.eatitserver.ui.shipper;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashu.eatitserver.Adapter.MyOrderAdapter;
import com.ashu.eatitserver.Adapter.MyShipperAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.EventBus.ChangeMenuClick;
import com.ashu.eatitserver.EventBus.UpdateShipperEvent;
import com.ashu.eatitserver.Model.FoodModel;
import com.ashu.eatitserver.Model.ShipperModel;
import com.ashu.eatitserver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ShipperFragment extends Fragment {

    private ShipperViewModel shipperViewModel;
    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_shipper)
    RecyclerView recycler_shipper;

    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyShipperAdapter adapter;
    List<ShipperModel> shipperModelList;
    private List<ShipperModel> saveShipperBeforeSearchList;

    public static ShipperFragment newInstance() {
        return new ShipperFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_shipper, container, false);
        shipperViewModel = new ViewModelProvider(this).get(ShipperViewModel.class);
        unbinder = ButterKnife.bind(this, itemView);
        initViews();
        shipperViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        shipperViewModel.getShipperMutableList().observe(getViewLifecycleOwner(), shippers -> {
            dialog.dismiss();
            shipperModelList = shippers;
            if (saveShipperBeforeSearchList == null)
                saveShipperBeforeSearchList = shippers;
            adapter = new MyShipperAdapter(getContext(), shipperModelList);
            recycler_shipper.setAdapter(adapter);
            recycler_shipper.setLayoutAnimation(layoutAnimationController);
        });
        return itemView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.food_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));


        //event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearchShipper(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        //clear text when clear text is pressed
        ImageView closeButton = searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(view -> {
            EditText ed = searchView.findViewById(R.id.search_src_text);
            //Clear Text
            ed.setText("");
            searchView.setQuery("", false);
            searchView.onActionViewCollapsed();
            menuItem.collapseActionView();
            if (saveShipperBeforeSearchList != null)
                shipperViewModel.getShipperMutableList().setValue(saveShipperBeforeSearchList);
        });
    }

    private void startSearchShipper(String s) {
        List<ShipperModel> resultShipper = new ArrayList<>();
        for (int i = 0; i < shipperModelList.size(); i++) {

            ShipperModel shipperModel = shipperModelList.get(i);
            if (shipperModel.getPhone().toLowerCase().contains(s.toLowerCase())) {
                resultShipper.add(shipperModel);
            }
        }
        shipperViewModel.getShipperMutableList().setValue(resultShipper);
    }

    private void initViews() {
        setHasOptionsMenu(true);
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_shipper.setHasFixedSize(true);
        recycler_shipper.setLayoutManager(layoutManager);
        recycler_shipper.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateShipperEvent.class))
            EventBus.getDefault().removeStickyEvent(UpdateShipperEvent.class);

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onUpdateShipperActive(UpdateShipperEvent event) {
        Map<String, Object> updateData = new HashMap<>();

        updateData.put("active", event.isActive());
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.SHIPPER_REF)
                .child(event.getShipperModel().getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Update state to : " + event.isActive(), Toast.LENGTH_SHORT).show());
    }

}
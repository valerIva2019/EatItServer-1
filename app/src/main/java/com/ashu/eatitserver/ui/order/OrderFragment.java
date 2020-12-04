package com.ashu.eatitserver.ui.order;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
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
import com.ashu.eatitserver.Common.BottomSheetOrderFragment;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Common.MySwiperHelper;
import com.ashu.eatitserver.EventBus.AddOnSizeEditEvent;
import com.ashu.eatitserver.EventBus.ChangeMenuClick;
import com.ashu.eatitserver.EventBus.LoadOrderEvent;
import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.Model.FoodModel;
import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.SizeAddonEditActivity;
import com.ashu.eatitserver.ui.category.CategoryViewModel;
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_SchemaVersionFactory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.txt_order_filter)
    TextView txt_order_filter;

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

                txt_order_filter.setText(new StringBuilder("Orders (").append(orderModels.size())
                .append(")"));
            }
        });
        return root;
    }

    private void initViews() {

        setHasOptionsMenu(true);

        recycler_order.setHasFixedSize(true);
        recycler_order.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        //Get Size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_order, width/6) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Directions", 30, 0, Color.parseColor("#9b0000"),
                        pos -> {

                        }));
                buf.add(new MyButton(getContext(), "Call", 30, 0, Color.parseColor("#560027"),
                        pos -> Dexter.withActivity(getActivity())
                                .withPermission(Manifest.permission.CALL_PHONE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        OrderModel orderModel = adapter.getItemAtPosition(pos);
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_DIAL);
                                        intent.setData(Uri.parse("tel: " + orderModel.getUserPhone()));
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                        Toast.makeText(getContext(), "You must accept "+permissionDeniedResponse.getPermissionName(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                    }
                                }).check()));
                buf.add(new MyButton(getContext(), "Remove", 30, 0, Color.parseColor("#12005e"),
                        pos -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("DELETE")
                                    .setMessage("Do you really want to delete this order?")
                                    .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                                    .setPositiveButton("DELETE", (dialogInterface, i) -> {
                                        OrderModel orderModel = adapter.getItemAtPosition(pos); //get item in adapter
                                        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                                                .child(orderModel.getKey())
                                                .removeValue()
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show()).addOnSuccessListener(aVoid -> {
                                                    adapter.removeItem(pos);
                                                    adapter.notifyItemRemoved(pos);
                                                    txt_order_filter.setText(new StringBuilder("Orders (").append(adapter.getItemCount()).append(")"));
                                                    dialogInterface.dismiss();
                                            Toast.makeText(getContext(), "Order has been deleted !!", Toast.LENGTH_SHORT).show();

                                        });
                                    });

                            AlertDialog deleteDialog = builder.create();
                            deleteDialog.show();

                            Button negativeButton = deleteDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeButton.setTextColor(Color.GRAY);
                            Button positiveButton = deleteDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(Color.RED);

                        }));
                buf.add(new MyButton(getContext(), "Edit", 30, 0, Color.parseColor("#336699"),
                        pos -> {

                        }));
            }
        };

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.order_filter_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                BottomSheetOrderFragment bottomSheetOrderFragment = BottomSheetOrderFragment.getInstance();
                bottomSheetOrderFragment.show(getActivity().getSupportFragmentManager(), "OrderFilter");
                break;
        }


        return true;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent.class))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent.class);

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
    public void onLoadOrderEvent(LoadOrderEvent event) {
        orderViewModel.loadOrderByStatus(event.getStatus());
    }
}
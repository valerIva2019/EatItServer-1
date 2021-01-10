package com.ashu.eatitserver.ui.discount;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import com.ashu.eatitserver.Adapter.MyDiscountAdapter;
import com.ashu.eatitserver.Adapter.MyFoodListAdapter;
import com.ashu.eatitserver.Adapter.MyShipperAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Common.MySwiperHelper;
import com.ashu.eatitserver.EventBus.AddOnSizeEditEvent;
import com.ashu.eatitserver.EventBus.ToastEvent;
import com.ashu.eatitserver.Model.DiscountModel;
import com.ashu.eatitserver.Model.FoodModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.SizeAddonEditActivity;
import com.ashu.eatitserver.ui.food_list.FoodListViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class DiscountFragment extends Fragment {

    private DiscountViewModel discountViewModel;

    private Unbinder unbinder;

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_discount)
    RecyclerView recycler_discount;

    LayoutAnimationController layoutAnimationController;
    MyDiscountAdapter adapter;
    private List<DiscountModel> discountModelList;
    private AlertDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        discountViewModel =
                new ViewModelProvider(this).get(DiscountViewModel.class);
        View root = inflater.inflate(R.layout.fragment_discount, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        discountViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        discountViewModel.getDiscountMutableLiveData().observe(getViewLifecycleOwner(), list -> {
            dialog.dismiss();

            if (list == null)
                discountModelList = new ArrayList<>();
            else
                discountModelList = list;

            adapter = new MyDiscountAdapter(getContext(), discountModelList);
            recycler_discount.setAdapter(adapter);
            recycler_discount.setLayoutAnimation(layoutAnimationController);
        });
        return root;
    }

    private void initViews() {
        setHasOptionsMenu(true);
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_discount.setHasFixedSize(true);
        recycler_discount.setLayoutManager(layoutManager);
        recycler_discount.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_discount, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#333639"),
                        pos -> {
                            Common.discountSelected = discountModelList.get(pos);
                            showDeleteDialog();

                        }));
                buf.add(new MyButton(getContext(), "Update", 30, 0, Color.parseColor("#414243"),
                        pos -> {
                            Common.discountSelected = discountModelList.get(pos);
                            showUpdateDialog();
                        }));
            }
        };

    }

    private void showUpdateDialog() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar selectedDate = Calendar.getInstance();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_discount, null);
        EditText edt_code = itemView.findViewById(R.id.edt_code);
        EditText edt_percent = itemView.findViewById(R.id.edt_percent);
        EditText edt_valid = itemView.findViewById(R.id.edt_valid);
        ImageView img_calendar = itemView.findViewById(R.id.pickDate);

        edt_code.setText(Common.discountSelected.getKey());
        edt_code.setEnabled(false);
        edt_percent.setText(Common.discountSelected.getPercent());
        edt_valid.setText(simpleDateFormat.format(Common.discountSelected.getUntilDate()));

        DatePickerDialog.OnDateSetListener listener = ((view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edt_valid.setText(simpleDateFormat.format(selectedDate.getTime()));
        });
        img_calendar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), listener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("UPDATE", (dialogInterface, i) -> {

                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("percent", Integer.parseInt(edt_percent.getText().toString()));
                    updateData.put("untilDate", selectedDate.getTimeInMillis());

                    updateDiscount(updateData);
                });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog updateDialog = builder.create();
        updateDialog.show();

    }

    private void updateDiscount(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.DISCOUNT)
                .child(Common.discountSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        discountViewModel.loadDiscount();
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new ToastEvent(Common.ACTION.UPDATE, true));
                    }
                });
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        showDeleteDialog();
        builder.setTitle("DELETE")
                .setMessage("Do you want to delete this food ?")
                .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("DELETE", (dialogInterface, i) -> deleteDiscount());

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
    }

    private void deleteDiscount() {
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.DISCOUNT)
                .child(Common.discountSelected.getKey())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        discountViewModel.loadDiscount();
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new ToastEvent(Common.ACTION.DELETE, true));
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.discount_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_create)
            showAddDialog();
        return super.onOptionsItemSelected(item);

    }

    private void showAddDialog() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar selectedDate = Calendar.getInstance();

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_discount, null);
        EditText edt_code = itemView.findViewById(R.id.edt_code);
        EditText edt_percent = itemView.findViewById(R.id.edt_percent);
        EditText edt_valid = itemView.findViewById(R.id.edt_valid);
        ImageView img_calendar = itemView.findViewById(R.id.pickDate);

//        edt_code.setText(Common.discountSelected.getKey());
//        edt_code.setEnabled(false);
//        edt_percent.setText(Common.discountSelected.getPercent());
//        edt_valid.setText(simpleDateFormat.format(Common.discountSelected.getUntilDate()));

        DatePickerDialog.OnDateSetListener listener = ((view, year, month, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, month);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            edt_valid.setText(simpleDateFormat.format(selectedDate.getTime()));
        });
        img_calendar.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(getContext(), listener, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("CREATE", (dialogInterface, i) -> {

                    DiscountModel model = new DiscountModel();
                    model.setKey(edt_code.getText().toString());
                    model.setPercent(Integer.parseInt(edt_percent.getText().toString()));
                    model.setUntilDate(selectedDate.getTimeInMillis());
                    createDiscount(model);
                });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog updateDialog = builder.create();
        updateDialog.show();

    }

    private void createDiscount(DiscountModel model) {
        FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.DISCOUNT)
                .child(model.getKey())
                .setValue(model)
                .addOnFailureListener(e -> Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        discountViewModel.loadDiscount();
                        adapter.notifyDataSetChanged();
                        EventBus.getDefault().post(new ToastEvent(Common.ACTION.CREATE, true));
                    }
                });
    }


}
package com.ashu.eatitserver.ui.best_deals;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashu.eatitserver.Adapter.MyBestDealsAdapter;
import com.ashu.eatitserver.Adapter.MyCategoriesAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Common.MySwiperHelper;
import com.ashu.eatitserver.EventBus.MenuItemBack;
import com.ashu.eatitserver.EventBus.ToastEvent;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.ui.category.CategoryViewModel;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BestDealsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private BestDealsViewModel bestDealsViewModel;
    Unbinder unbinder;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_best_deal)
    RecyclerView recycler_best_deal;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyBestDealsAdapter adapter;
    List<BestDealModel> bestDealModels;
    ImageView img_best_deals;

    private Uri imageUri = null;
    FirebaseStorage storage;
    StorageReference storageReference;

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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setHasOptionsMenu(true);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_best_deal.setLayoutManager(layoutManager);
        recycler_best_deal.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_best_deal, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#333639"),
                        pos -> {
                            Common.bestDealsSelected = bestDealModels.get(pos);
                            showDeleteDialog();
                        }));

                buf.add(new MyButton(getContext(), "Update", 30, 0, Color.parseColor("#560027"),
                        pos -> {
                            Common.bestDealsSelected = bestDealModels.get(pos);
                            showUpdateDialog();
                        }));
            }
        };
    }

    private void showDeleteDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Do you really want to delete this ?");
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("DELETE", (dialogInterface, i) -> deleteBestDeals());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBestDeals() {
        FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS)
                .child(Common.bestDealsSelected.getKey())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    bestDealsViewModel.loadBestDeals();
                    EventBus.getDefault().postSticky(new ToastEvent(false, false));
                });
    }

    private void showUpdateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category, null);
        EditText edt_category_name =  itemView.findViewById(R.id.edt_category_name);
        img_best_deals = itemView.findViewById(R.id.img_category);

        //Set Data
        edt_category_name.setText(new StringBuilder().append(Common.bestDealsSelected.getName()));
        Glide.with(getContext()).load(Common.bestDealsSelected.getImage()).into(img_best_deals);


        //Set Event
        img_best_deals.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("UPDATE", (dialogInterface, i) -> {

                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("name", edt_category_name.getText().toString());

                    if (imageUri != null) {
                        dialog.setMessage("Uploading");
                        dialog.show();

                        String unique_name = UUID.randomUUID().toString();
                        StorageReference imageFolder = storageReference.child("images/" + unique_name);


                        imageFolder.putFile(imageUri)
                                .addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }).addOnCompleteListener(task -> {
                            dialog.dismiss();
                            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                                updateData.put("image", uri.toString());
                                updateBestDeals(updateData);
                            });
                        }).addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            dialog.setMessage("Uploading: " + progress + "%");
                        });
                    } else {
                        updateBestDeals(updateData);
                    }
                });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void updateBestDeals(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS)
                .child(Common.bestDealsSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    bestDealsViewModel.loadBestDeals();
                    EventBus.getDefault().postSticky(new ToastEvent(true, false));
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                img_best_deals.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}
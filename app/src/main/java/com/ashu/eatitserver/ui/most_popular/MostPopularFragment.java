package com.ashu.eatitserver.ui.most_popular;

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
import com.ashu.eatitserver.Adapter.MyMostPopularAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Common.MySwiperHelper;
import com.ashu.eatitserver.EventBus.MenuItemBack;
import com.ashu.eatitserver.EventBus.ToastEvent;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;
import com.ashu.eatitserver.R;
import com.ashu.eatitserver.ui.best_deals.BestDealsViewModel;
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
    private static final int PICK_IMAGE_REQUEST = 1234;
    ImageView img_most_popular;

    private Uri imageUri = null;
    FirebaseStorage storage;
    StorageReference storageReference;
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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        setHasOptionsMenu(true);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //dialog.show();
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recycler_most_popular.setLayoutManager(layoutManager);
        recycler_most_popular.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));

        MySwiperHelper mySwiperHelper = new MySwiperHelper(getContext(), recycler_most_popular, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                buf.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#333639"),
                        pos -> {
                            Common.mostPopularSelected = popularCategoryModels.get(pos);
                            showDeleteDialog();
                        }));

                buf.add(new MyButton(getContext(), "Update", 30, 0, Color.parseColor("#560027"),
                        pos -> {
                            Common.mostPopularSelected = popularCategoryModels.get(pos);
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
                .setPositiveButton("DELETE", (dialogInterface, i) -> deleteMostPopular());

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteMostPopular() {
        FirebaseDatabase.getInstance().getReference(Common.MOST_POPULAR)
                .child(Common.mostPopularSelected.getKey())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mostPopularViewModel.loadMostPopular();
                    EventBus.getDefault().postSticky(new ToastEvent(false, false));
                });
    }

    private void showUpdateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category, null);
        EditText edt_category_name =  itemView.findViewById(R.id.edt_category_name);
        img_most_popular = itemView.findViewById(R.id.img_category);

        //Set Data
        edt_category_name.setText(new StringBuilder().append(Common.mostPopularSelected.getName()));
        Glide.with(getContext()).load(Common.mostPopularSelected.getImage()).into(img_most_popular);


        //Set Event
        img_most_popular.setOnClickListener(view -> {
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
                                updateMostPopular(updateData);
                            });
                        }).addOnProgressListener(snapshot -> {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            dialog.setMessage("Uploading: " + progress + "%");
                        });
                    } else {
                        updateMostPopular(updateData);
                    }
                });

        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void updateMostPopular(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance().getReference(Common.MOST_POPULAR)
                .child(Common.mostPopularSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mostPopularViewModel.loadMostPopular();
                    EventBus.getDefault().postSticky(new ToastEvent(true, false));
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                imageUri = data.getData();
                img_most_popular    .setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}
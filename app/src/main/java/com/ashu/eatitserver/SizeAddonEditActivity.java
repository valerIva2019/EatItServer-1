package com.ashu.eatitserver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashu.eatitserver.Adapter.MyAddonAdapter;
import com.ashu.eatitserver.Adapter.MySizeAdapter;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.EventBus.AddOnSizeEditEvent;
import com.ashu.eatitserver.EventBus.SelectAddonModel;
import com.ashu.eatitserver.EventBus.SelectSizeModel;
import com.ashu.eatitserver.EventBus.UpdateAddonModel;
import com.ashu.eatitserver.EventBus.UpdateSizeModel;
import com.ashu.eatitserver.Model.AddonModel;
import com.ashu.eatitserver.Model.SizeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SizeAddonEditActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.tool_bar)
    Toolbar toolbar;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_name)
    TextView edt_name;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.edt_price)
    TextView edt_price;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_create)
    Button btn_create;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.btn_edit)
    Button btn_edit;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.recycler_addon_size)
    RecyclerView recycler_addon_size;

    MySizeAdapter adapter;
    MyAddonAdapter addonAdapter;
    private int foodEditPosition = -1;
    private boolean needSave = false;
    private boolean isAddon = false;


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_create)
    void onCreateNew() {
        if (!isAddon) {
            if (adapter != null) {
                SizeModel sizeModel = new SizeModel();
                sizeModel.setName(edt_name.getText().toString());
                sizeModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                adapter.addNewSize(sizeModel);
            }
        } else {
            if (addonAdapter != null) {
                AddonModel addonModel = new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.addNewSize(addonModel);
            }
        }
    }
    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.btn_edit)
    void onEdit() {
        if (!isAddon) {
            if (adapter != null) {
                SizeModel sizeModel = new SizeModel();
                sizeModel.setName(edt_name.getText().toString());
                sizeModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                adapter.editSize(sizeModel);
            }
        } else {
            if (addonAdapter != null) {
                AddonModel addonModel = new AddonModel();
                addonModel.setName(edt_name.getText().toString());
                addonModel.setPrice(Long.valueOf(edt_price.getText().toString()));
                addonAdapter.editSize(addonModel);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addon_size_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveData();
                break;
            case android.R.id.home:
            {
                if (needSave) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Cancel")
                            .setMessage("Do you want to close without saving ?")
                            .setNegativeButton("CANCEL", (dialogInterface, i) -> dialogInterface.dismiss()).setPositiveButton("OK", (dialogInterface, i) -> {
                                needSave = false;
                                closeActivity();
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    closeActivity();
                }
            }
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveData() {
        if (foodEditPosition != -1) {
            Common.categorySelected.getFoods().set(foodEditPosition, Common.selectedFood); //Save food to category

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("foods", Common.categorySelected.getFoods());

            FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)
                    .child(Common.categorySelected.getMenu_id())
                    .updateChildren(updateData)
                    .addOnFailureListener(e -> Toast.makeText(SizeAddonEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(SizeAddonEditActivity.this, "Reload success", Toast.LENGTH_SHORT).show();
                            needSave = false;
                            edt_price.setText("");
                            edt_name.setText("");
                        }
                    });
        }
    }

    private void closeActivity() {
        edt_name.setText("");
        edt_price.setText("0");
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_addon_edit);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recycler_addon_size.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_addon_size.setLayoutManager(layoutManager);
        recycler_addon_size.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().removeStickyEvent(UpdateSizeModel.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddonSIzeReceived(AddOnSizeEditEvent event) {
        if (!event.isAddon()) {
            if (Common.selectedFood.getSize() != null) {
                adapter = new MySizeAdapter(this, Common.selectedFood.getSize());
                foodEditPosition = event.getPos(); //Save food edit position
                recycler_addon_size.setAdapter(adapter);

                isAddon= event.isAddon();
            }
        } else {
            if (Common.selectedFood.getAddon() != null) {
                addonAdapter = new MyAddonAdapter(this, Common.selectedFood.getAddon());
                foodEditPosition = event.getPos(); //Save food edit position
                recycler_addon_size.setAdapter(addonAdapter);

                isAddon= event.isAddon();
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSizeModelUpdate(UpdateSizeModel event) {
       if (event.getSizeModelList() != null) {
           needSave = true;
           Common.selectedFood.setSize(event.getSizeModelList());
       }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onAddonModelUpdate(UpdateAddonModel event) {
        if (event.getAddonModel() != null) {
            needSave = true;
            Common.selectedFood.setAddon(event.getAddonModel());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSelectSizeModel(SelectSizeModel event) {
        if (event.getSizeModel() != null) {
            edt_name.setText(event.getSizeModel().getName());
            edt_price.setText(String.valueOf(event.getSizeModel().getPrice()));

            btn_edit.setEnabled(true);
        } else {
            btn_edit.setEnabled(false);
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSelectAddonModel(SelectAddonModel event) {
        if (event.getAddonModel() != null) {
            edt_name.setText(event.getAddonModel().getName());
            edt_price.setText(String.valueOf(event.getAddonModel().getPrice()));

            btn_edit.setEnabled(true);
        } else {
            btn_edit.setEnabled(false);
        }
    }
}
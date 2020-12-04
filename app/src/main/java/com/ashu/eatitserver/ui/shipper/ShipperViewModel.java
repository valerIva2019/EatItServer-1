package com.ashu.eatitserver.ui.shipper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ashu.eatitserver.Callback.IShipperLoadCallbackListener;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.Model.ShipperModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShipperViewModel extends ViewModel implements IShipperLoadCallbackListener {
    private MutableLiveData<List<ShipperModel>> shipperMutableList;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IShipperLoadCallbackListener shipperLoadCallbackListener;

    public ShipperViewModel() {
        shipperLoadCallbackListener = this;
    }

    public MutableLiveData<List<ShipperModel>> getShipperMutableList() {
        if (shipperMutableList == null) {
            shipperMutableList = new MutableLiveData<>();
            loadShipper();
        }
        return shipperMutableList;
    }

    private void loadShipper() {
        List<ShipperModel> tempList = new ArrayList<>();
        DatabaseReference shipperRef = FirebaseDatabase.getInstance().getReference(Common.SHIPPER_REF);
        shipperRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot shipperSnapshot : snapshot.getChildren()) {
                    ShipperModel shipperModel = shipperSnapshot.getValue(ShipperModel.class);
                    shipperModel.setKey(shipperSnapshot.getKey());
                    tempList.add(shipperModel);
                }
                shipperLoadCallbackListener.onShipperLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                shipperLoadCallbackListener.onShipperLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onShipperLoadSuccess(List<ShipperModel> shipperModels) {
        if (shipperMutableList != null) {
            shipperMutableList.setValue(shipperModels);
        }
    }

    @Override
    public void onShipperLoadFailed(String message) {
        messageError.setValue(message);
    }
}
package com.ashu.eatitserver.ui.best_deals;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ashu.eatitserver.Callback.IBestDealsCallbackListener;
import com.ashu.eatitserver.Callback.ICategoryCallbackListener;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BestDealsViewModel extends ViewModel implements IBestDealsCallbackListener {
    private MutableLiveData<List<BestDealModel>> bestDealsListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IBestDealsCallbackListener bestDealsCallbackListener;

    public BestDealsViewModel() {
        bestDealsCallbackListener = this;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealsListMutable() {
        if (bestDealsListMutable == null)
            bestDealsListMutable = new MutableLiveData<>();

        loadBestDeals();

        return bestDealsListMutable;
    }

    public void loadBestDeals() {
        List<BestDealModel> tempList = new ArrayList<>();
        DatabaseReference bestDealsRef = FirebaseDatabase.getInstance().getReference(Common.RESTAURANT_REF)
                .child(Common.currentServerUser.getRestaurant())
                .child(Common.BEST_DEALS);
        bestDealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    BestDealModel model = itemSnapshot.getValue(BestDealModel.class);
                    model.setKey(itemSnapshot.getKey());
                    tempList.add(model);
                }
                bestDealsCallbackListener.onBestDealsLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bestDealsCallbackListener.onBestDealsLoadFailed(error.getMessage());
            }
        });
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    @Override
    public void onBestDealsLoadSuccess(List<BestDealModel> bestDealModels) {
        bestDealsListMutable.setValue(bestDealModels);
    }

    @Override
    public void onBestDealsLoadFailed(String message) {
        messageError.setValue(message);
    }
}
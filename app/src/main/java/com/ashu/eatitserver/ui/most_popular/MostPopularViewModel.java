package com.ashu.eatitserver.ui.most_popular;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ashu.eatitserver.Callback.IBestDealsCallbackListener;
import com.ashu.eatitserver.Callback.IMostPopularCallbackListener;
import com.ashu.eatitserver.Common.Common;
import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MostPopularViewModel extends ViewModel implements IMostPopularCallbackListener {
    private MutableLiveData<List<PopularCategoryModel>> popularCategoryListMutable;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IMostPopularCallbackListener mostPopularCallbackListener;

    public MostPopularViewModel() {
        mostPopularCallbackListener = this;
    }

    public MutableLiveData<List<PopularCategoryModel>> getPopularCategoryModel() {
        if (popularCategoryListMutable == null)
            popularCategoryListMutable = new MutableLiveData<>();

        loadMostPopular();

        return popularCategoryListMutable;
    }

    public void loadMostPopular() {
        List<PopularCategoryModel> tempList = new ArrayList<>();
        DatabaseReference mostPopularRef = FirebaseDatabase.getInstance().getReference(Common.MOST_POPULAR);
        mostPopularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    PopularCategoryModel model = itemSnapshot.getValue(PopularCategoryModel.class);
                    model.setKey(itemSnapshot.getKey());
                    tempList.add(model);
                }
                mostPopularCallbackListener.onMostPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostPopularCallbackListener.onMostPopularLoadFailed(error.getMessage());
            }
        });
    }
    public MutableLiveData<String> getMessageError() {
        return messageError;
    }
    @Override
    public void onMostPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels) {
        popularCategoryListMutable.setValue(popularCategoryModels);
    }

    @Override
    public void onMostPopularLoadFailed(String message) {
        messageError.setValue(message);
    }
}
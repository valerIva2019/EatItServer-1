package com.ashu.eatitserver.Callback;

import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.PopularCategoryModel;

import java.util.List;

public interface IMostPopularCallbackListener {
    void onMostPopularLoadSuccess(List<PopularCategoryModel> popularCategoryModels);
    void onMostPopularLoadFailed(String message);
}

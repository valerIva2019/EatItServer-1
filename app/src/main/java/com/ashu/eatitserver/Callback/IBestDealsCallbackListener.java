package com.ashu.eatitserver.Callback;

import com.ashu.eatitserver.Model.BestDealModel;
import com.ashu.eatitserver.Model.CategoryModel;

import java.util.List;

public interface IBestDealsCallbackListener {
    void onBestDealsLoadSuccess(List<BestDealModel> bestDealModels);
    void onBestDealsLoadFailed(String message);
}

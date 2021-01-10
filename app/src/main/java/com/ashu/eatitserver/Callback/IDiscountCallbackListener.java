package com.ashu.eatitserver.Callback;

import com.ashu.eatitserver.Model.CategoryModel;
import com.ashu.eatitserver.Model.DiscountModel;

import java.util.List;

public interface IDiscountCallbackListener {
    void onListDiscountLoadSuccess(List<DiscountModel> discountModelList);
    void onListDiscountLoadFailed(String message);
}

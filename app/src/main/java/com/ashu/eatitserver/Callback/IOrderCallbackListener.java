package com.ashu.eatitserver.Callback;


import com.ashu.eatitserver.Model.OrderModel;

import java.util.List;

public interface IOrderCallbackListener {
    void onOrderLoadSuccess(List<OrderModel> categoryModels);
    void onOrderLoadFailed(String message);
}

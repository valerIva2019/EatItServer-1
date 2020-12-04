package com.ashu.eatitserver.Callback;

import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.Model.ShipperModel;

import java.util.List;

public interface IShipperLoadCallbackListener {
    void onShipperLoadSuccess(List<ShipperModel> shipperModels);
    void onShipperLoadFailed(String message);
}

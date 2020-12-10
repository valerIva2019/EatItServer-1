package com.ashu.eatitserver.Callback;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.RadioButton;

import com.ashu.eatitserver.Model.OrderModel;
import com.ashu.eatitserver.Model.ShipperModel;
import com.ashu.eatitserver.Model.ShippingOrderModel;

import java.util.List;

public interface ISingleShippingOrderCallbackListener {
    void onSingleShippingOrderLoadSuccess(ShippingOrderModel shippingOrderModel);
}

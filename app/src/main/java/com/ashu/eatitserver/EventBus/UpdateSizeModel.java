package com.ashu.eatitserver.EventBus;

import android.util.Size;

import com.ashu.eatitserver.Model.SizeModel;

import java.util.List;

public class UpdateSizeModel {

    private List<SizeModel> sizeModelList;

    public UpdateSizeModel() {
    }

    public UpdateSizeModel(List<SizeModel> sizeModelList) {
        this.sizeModelList = sizeModelList;
    }

    public List<SizeModel> getSizeModelList() {
        return sizeModelList;
    }

    public void setSizeModelList(List<SizeModel> sizeModelList) {
        this.sizeModelList = sizeModelList;
    }
}

package com.ashu.eatitserver.EventBus;

import com.ashu.eatitserver.Model.SizeModel;

public class SelectSizeModel {
    private SizeModel sizeModel;

    public SelectSizeModel(SizeModel sizeModel) {
        this.sizeModel = sizeModel;
    }

    public SizeModel getSizeModel() {
        return sizeModel;
    }

    public void setSizeModel(SizeModel sizeModel) {
        this.sizeModel = sizeModel;
    }
}

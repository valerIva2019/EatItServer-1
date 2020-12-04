package com.ashu.eatitserver.EventBus;

import com.ashu.eatitserver.Model.AddonModel;
import com.ashu.eatitserver.Model.SizeModel;

import java.util.List;

public class UpdateAddonModel {

    private List<AddonModel> addonModels;

    public UpdateAddonModel() {
    }

    public UpdateAddonModel(List<AddonModel> addonModels) {
        this.addonModels = addonModels;
    }

    public List<AddonModel> getAddonModel() {
        return addonModels;
    }

    public void setAddonModel(List<AddonModel> addonModels) {
        this.addonModels = addonModels;
    }
}

package com.ashu.eatitserver.EventBus;

import com.ashu.eatitserver.Model.AddonModel;
import com.ashu.eatitserver.Model.SizeModel;

public class SelectAddonModel {
    private AddonModel addonModel;

    public SelectAddonModel(AddonModel addonModel) {
        this.addonModel = addonModel;
    }

    public AddonModel getAddonModel() {
        return addonModel;
    }

    public void setAddonModel(AddonModel addonModel) {
        this.addonModel = addonModel;
    }
}

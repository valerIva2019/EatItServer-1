package com.ashu.eatitserver.EventBus;

public class ChangeMenuClick {
    boolean isFromFoodList;

    public ChangeMenuClick(boolean isFromFoodList) {
        this.isFromFoodList = isFromFoodList;
    }

    public boolean isFromFoodList() {
        return isFromFoodList;
    }

    public void setFromFoodList(boolean fromFoodList) {
        isFromFoodList = fromFoodList;
    }
}

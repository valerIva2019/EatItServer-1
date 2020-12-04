package com.ashu.eatitserver.EventBus;

public class AddOnSizeEditEvent {
    private boolean addon;
    private int pos;

    public AddOnSizeEditEvent(boolean addon, int pos) {
        this.addon = addon;
        this.pos = pos;
    }

    public boolean isAddon() {
        return addon;
    }

    public void setAddon(boolean addon) {
        this.addon = addon;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }


}

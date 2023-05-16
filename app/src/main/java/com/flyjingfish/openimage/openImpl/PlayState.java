package com.flyjingfish.openimage.openImpl;

public class PlayState {
    int state;
    int position;
    boolean consume;

    public PlayState(int state, int position) {
        this.state = state;
        this.position = position;
    }

    public PlayState(int position) {
        this.position = position;
    }
}

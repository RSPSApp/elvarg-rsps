package com.runescape.collection;

import net.runelite.rs.api.RSNode;

public class Linkable implements RSNode {

    public long key;
    public Linkable previous;
    public Linkable next;

    public final void unlink() {
        if (next == null) {
        } else {
            next.previous = previous;
            previous.next = next;
            previous = null;
            next = null;
        }
    }

    @Override
    public RSNode getNext() {
        return next;
    }

    @Override
    public long getHash() {
        return key;
    }

    @Override
    public RSNode getPrevious() {
        return previous;
    }

    @Override
    public void onUnlink() {

    }

}

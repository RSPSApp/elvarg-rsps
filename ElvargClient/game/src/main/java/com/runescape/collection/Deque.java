package com.runescape.collection;

import net.runelite.rs.api.RSNode;
import net.runelite.rs.api.RSNodeDeque;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public final class Deque implements RSNodeDeque {

    private final Linkable head;
    private Linkable current;

    public Deque() {
        head = new Linkable();
        head.previous = head;
        head.next = head;
    }

    public void insertHead(Linkable linkable) {
        if (linkable.next != null)
            linkable.unlink();
        linkable.next = head.next;
        linkable.previous = head;
        linkable.next.previous = linkable;
        linkable.previous.next = linkable;
    }

    public void insertTail(Linkable linkable) {
        if (linkable.next != null)
            linkable.unlink();
        linkable.next = head;
        linkable.previous = head.previous;
        linkable.next.previous = linkable;
        linkable.previous.next = linkable;
    }

    public Linkable popHead() {
        Linkable node = head.previous;
        if (node == head) {
            return null;
        } else {
            node.unlink();
            return node;
        }
    }

    public Linkable reverseGetFirst() {
        Linkable node = head.previous;
        if (node == head) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Linkable getFirst() {
        Linkable node = head.next;
        if (node == head) {
            current = null;
            return null;
        } else {
            current = node.next;
            return node;
        }
    }

    public Linkable reverseGetNext() {
        Linkable node = current;
        if (node == head) {
            current = null;
            return null;
        } else {
            current = node.previous;
            return node;
        }
    }

    public Linkable getNext() {
        Linkable node = current;
        if (node == head) {
            current = null;
            return null;
        }
        current = node.next;
        return node;
    }

    @Override
    public RSNode getCurrent() {
        return null;
    }

    @Override
    public RSNode getSentinel() {
        return null;
    }

    @Override
    public RSNode last() {
        return null;
    }

    @Override
    public RSNode previous() {
        return null;
    }

    @Override
    public void addFirst(RSNode val) {

    }

    @Override
    public RSNode removeLast() {
        return null;
    }

    public void clear() {
        if (head.previous == head)
            return;
        do {
            Linkable node = head.previous;
            if (node == head)
                return;
            node.unlink();
        } while (true);
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return null;
    }
}

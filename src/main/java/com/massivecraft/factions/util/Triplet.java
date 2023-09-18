package com.massivecraft.factions.util;

public class Triplet<Left, Middle, Right> {
    public static <Left, Middle, Right> Triplet<Left, Middle, Right> of(Left left, Middle middle, Right right) {
        return new Triplet<>(left, middle, right);
    }

    private final Left left;
    private final Middle middle;
    private final Right right;

    private Triplet(Left left, Middle middle, Right right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public Left getLeft() {
        return this.left;
    }

    public Middle getMiddle() {
        return middle;
    }

    public Right getRight() {
        return this.right;
    }
}

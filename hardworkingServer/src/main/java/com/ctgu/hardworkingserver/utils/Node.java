package com.ctgu.hardworkingserver.utils;

import com.ctgu.hardworkingserver.entity.BDetails;
import lombok.Data;
import lombok.ToString;


public class Node {
    private int key;
    private String value;
    private BDetails bDetail;
    private Node left;
    private Node right;
    private Node parent;
    private int height;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public BDetails getbDetail() {
        return bDetail;
    }

    public void setbDetail(BDetails bDetail) {
        this.bDetail = bDetail;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }



}

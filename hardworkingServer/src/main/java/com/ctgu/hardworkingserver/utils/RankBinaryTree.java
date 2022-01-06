package com.ctgu.hardworkingserver.utils;

import com.ctgu.hardworkingserver.entity.BDetails;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class RankBinaryTree {

//    逆中序遍历
    public static List<BDetails> InOrderTraverse(Node root)
    {
        Node node = root;
        List<BDetails> list = new ArrayList<>();
        ArrayDeque<Node> stack = new ArrayDeque();
        while(node != null || !stack.isEmpty()){
            if(node != null){
                stack.push(node);
                node = node.getRight();
            }else {
                node = stack.pop();
                if (node.getbDetail() != null)
                {
                    list.add(node.getbDetail());

                }

                node = node.getLeft();
            }
        }
        return list;
    }

    public static List<BDetails> InOrderTraverseSout(Node root,String tagId)
    {
        Node node = root;
        List<BDetails> list = new ArrayList<>();
        ArrayDeque<Node> stack = new ArrayDeque();
        while(node != null || !stack.isEmpty()){
            if(node != null){
                stack.push(node);
                node = node.getRight();
            }else {
                node = stack.pop();
                BDetails bDetails = node.getbDetail();
                if (bDetails != null)
                {
                    if (bDetails.getTag1()!=null &&bDetails.getTag1().equals(tagId)){
                        list.add(bDetails);
                    }else if (bDetails.getTag2()!=null && bDetails.getTag2().equals(tagId)){
                        list.add(bDetails);
                    }else if (bDetails.getTag3()!=null && bDetails.getTag3().equals(tagId)){
                        list.add(bDetails);
                    }else if (bDetails.getTag4()!=null && bDetails.getTag4().equals(tagId)){
                        list.add(bDetails);
                    }else if (bDetails.getTag5()!=null && bDetails.getTag5().equals(tagId)){
                        list.add(bDetails);list.add(bDetails);
                    }
                }
                node = node.getLeft();
            }
        }
        return list;
    }


    public static Node TreeSearch(Node root, String value)   //递归方法查找结点
    {

        Node node = root;

        ArrayDeque<Node> stack = new ArrayDeque();
        while(node != null || !stack.isEmpty()){
            if(node != null){
                stack.push(node);
                node = node.getRight();
            }else {
                node = stack.pop();
                if(node.getValue()!=null)
                if (node.getValue().equals(value))
                    break;
                node = node.getLeft();
            }
        }
        return node;
//        if( root==null || k==root.getKey())
//        {
//            return root;
//        }
//        if(k< root.getKey())
//            return TreeSearch(root.getLeft(),k);
//        else
//            return TreeSearch(root.getRight(),k);
    }

    public static Node IterativeTreeSearch(Node root, int k, String value)    //循环迭代查找结点
    {
        Node temp=root;
        while(temp!=null)
        {
            if (value.equals(temp.getValue()))
                break;
            if(k<temp.getKey())
            {
                temp=temp.getLeft();
            }
            else
            {
                temp=temp.getRight();
            }
        }
        return  temp;
    }

    public static Node Minimum(Node root)
    {
        Node temp=root;
            while(temp!=null)
            {
                Node node = temp.getLeft();
                if (node == null) break;
                temp = node;
            }
        return temp;
    }

    public static Node Maximum(Node root)
    {
        Node temp=root;

            while(temp!=null)
            {
                Node node = temp.getRight();
                if (node == null) break;
                temp = node;
            }

        return temp;
    }

    public static Node Tree_Successor(Node node)   //查找结点的中序遍历的后继结点
    {
        if(node.getRight()!=null)
        {
            return Maximum(node);
        }
        Node parent=node.getParent();
        Node temp=node;
        while(parent!=null && node==parent.getRight())    //左子节点比双亲结点先遍历，所以左子节点是前驱
        {
            temp=parent;
            parent=temp.getParent();
        }
        return temp;
    }

    public static void Node_Insert(Node root, Node k)
    {
        if(root.getKey()==Integer.MIN_VALUE)
        {
            root.setKey(k.getKey());
            root.setbDetail(k.getbDetail());
            root.setValue(k.getValue());
            return;
        }
        Node parent = null;
        Node temp = root;

        while(temp!=null)
        {
            parent=temp;

            if(k.getKey()< temp.getKey())
            {
                temp=temp.getLeft();
            }
            else temp=temp.getRight();
        }

        k.setParent(parent);
        if(parent==null)
        {
            root=k;
        }
        else if(k.getKey()<parent.getKey())
        {
            parent.setLeft(k);
        }
        else parent.setRight(k);
    }

    public static void Delete(Node root,Node z)
    {
        if(z.getLeft()==null&&z.getRight()!=null)
        {
            TransPlant(root,z,z.getRight());
        }
        else if(z.getRight()==null&&z.getLeft()!=null)
        {
            TransPlant(root,z,z.getLeft());
        }
        else if(z.getLeft()==null&&z.getRight()==null)
        {
            TransPlant(root,z,null);
        }
        else {
            Node parent=Minimum(z.getRight());
            if(parent.getParent()!=z)
            {
                TransPlant(root,parent,parent.getRight());
                parent.setRight(z.getRight());
                parent.getRight().setParent(parent);
            }
            TransPlant(root,z,parent);
            parent.setLeft(z.getLeft());
            parent.getLeft().setParent(parent);
        }
    }

    private static void TransPlant(Node root, Node u, Node v)
    {
        if(u.getParent()==null)
        {
            root=v;
        }
        else if(u.getParent().getLeft()==u)
        {
            u.getParent().setLeft(v);
        }
        else u.getParent().setRight(v);

        if(u!=null&&v!=null)
        {
            v.setParent(u.getParent());
        }
    }



}

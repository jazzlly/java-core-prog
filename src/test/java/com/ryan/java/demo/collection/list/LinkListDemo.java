package com.ryan.java.demo.collection.list;

public class LinkListDemo {
    /**
     * http://calvin1978.blogcn.com/articles/collection.html
     *
     * 以双向链表实现。链表无容量限制，但双向链表本身使用了更多空间，
     * 每插入一个元素都要构造一个额外的Node对象，也需要额外的链表指针操作。
     *
     * 按下标访问元素－get（i）、set（i,e） 要悲剧的部分遍历链表将指针移动到位
     * （如果i>数组大小的一半，会从末尾移起）。
     *
     * 插入、删除元素时修改前后节点的指针即可，不再需要复制移动。
     * 但还是要部分遍历链表的指针才能移动到下标所指的位置。
     *
     * 只有在链表两头的操作－add（）、addFirst（）、removeLast（）
     * 或用iterator（）上的remove（）倒能省掉指针的移动。
     *
     * Apache Commons 有个TreeNodeList，里面是棵二叉树，可以快速移动指针到位。
     */
}

package com.ryan.java.demo.collection.list;

public class ArrayListDemo {

    /**
     * http://calvin1978.blogcn.com/articles/collection.html
     *
     * 以数组实现。节约空间，但数组有容量限制。超出限制时会增加50%容量，
     * 用System.arraycopy（）复制到新的数组。因此最好能给出数组大小的预估值。
     * 默认第一次插入元素时创建大小为10的数组。
     *
     * 按数组下标访问元素－get（i）、set（i,e） 的性能很高，这是数组的基本优势。
     * 如果按下标插入元素、删除元素－add（i,e）、 remove（i）、remove（e），
     * 则要用System.arraycopy（）来复制移动部分受影响的元素，性能就变差了。
     *
     * 越是前面的元素，修改时要移动的元素越多。
     * 直接在数组末尾加入元素－常用的add（e），删除最后一个元素则无影响。
     */
}

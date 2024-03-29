package com.ryan.java.demo.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setRemoveAssertJRelatedElementsFromStackTrace;

@Slf4j
public class D002StringBuilderTest {

    @Test
    public void constPool_test() {
        String foo = "abc";
        String bar = new String("abc");
        String intern = new String("abc").intern();
        String wah = "abc";
        String x = "a" + "b" + "c";
        String y = "ab" + "c";

        assertThat(foo).isEqualTo(bar);
        assertThat(foo).isEqualTo(intern);
        assertThat(foo).isEqualTo(wah);

        // new 出来的String不在常量池里
        assertThat(foo).isNotSameAs(bar);

        // intern(), "abc" 都在常量池里面
        assertThat(foo).isSameAs(intern);
        assertThat(foo).isSameAs(wah);
        assertThat(foo).isSameAs(x);
        assertThat(foo).isSameAs(y);

        // 老师，这章学习到了 Java 8 以后，字符串常量池被移到了堆中，那么，
        //      如果通过 String.intern() 产生了大量的字符串常量，JVM 会对它们进行垃圾回收吗？
        //  作者回复:会，具体时机我也没注意
    }

    @Test
    public void smoke() {
        StringBuilder sb = new StringBuilder();
        sb.append(123);
        sb.append("foo");
        sb.append('b');
        sb.append("ar");
        sb.append(1 == 1);

        assertThat(sb.toString()).isEqualTo("123foobartrue");
        assertThat(sb.length()).isEqualTo("123foobartrue".length());

        // insert word at beginning
        sb.insert(0, "wahaha");
        assertThat(sb.toString()).isEqualTo("wahaha123foobartrue");

        // delete last char
        sb.deleteCharAt(sb.length()-1);
        assertThat(sb.toString()).isEqualTo("wahaha123foobartru");

        sb.delete(sb.length()-3, sb.length());
        assertThat(sb.toString()).isEqualTo("wahaha123foobar");

        /**
         * StringBuffer允许多线程进行并发访问, 效率稍低
         *  实现方式是所有接口都加上了syncrhonized
         *
         * StringBuilder只能用于单线程操作
         *
         *  两者都继承了AstractStringBuilder
         *      内部使用了可变长的char数组， jdk9是byte数组
         *      初始大小为16
         *
         *      数组容量将超过上限后，会进行扩容。
         *      扩容后大小是 (array.capacity + 1)*2
         */
    }

    @Test
    public void internalArrayTest() {
        StringBuilder builder = new StringBuilder();

        // 内部数组初始大小为16
        assertThat(builder.capacity()).isEqualTo(16);

        for (int i = builder.length(); i < builder.capacity(); i++) {
            builder.append("a");
        }

        // 达到了上限
        assertThat(builder.length()).isEqualTo(16);

        // 开始扩容, 扩容后的数组大小: sizeof(builder.length() + 1) * 2
        builder.append("foo");

        System.out.println(builder.length());
        System.out.println(builder.capacity());

        for (int i = builder.length(); i < builder.capacity(); i++) {
            builder.append("a");
        }
        builder.append("foo");

        System.out.println(builder.length());
        System.out.println(builder.capacity());
    }

    @Test
    public void encoding_test() {
        /**
         * 可以思考一下，很多字符串操作，比如 getBytes()/
         * /String​(byte[] bytes) 等都是隐含着使用平台默认编码
         * 这是一种好的习惯？
         *
         * getBytes和String相关的转换时根据业务需要建议指定编码方式，
         * 如果不指定则看看JVM参数里有没有指定file.encoding参数，
         * 如果JVM没有指定，那使用的默认编码就是运行的操作系统环境的编码了，
         * 那这个编码就变得不确定了。常见的编码iso8859-1是单字节编码，UTF-8是变长的编码。
         */
    }

    /**
     * 1 String
     *
     * (1) String的创建机理
     * 由于String在Java世界中使用过于频繁，Java为了避免在一个系统中产生大量的String对象，引入了字符串常量池。其运行机制是：创建一个字符串时，首先检查池中是否有值相同的字符串对象，如果有则不需要创建直接从池中刚查找到的对象引用；如果没有则新建字符串对象，返回对象引用，并且将新创建的对象放入池中。但是，通过new方法创建的String对象是不检查字符串池的，而是直接在堆区或栈区创建一个新的对象，也不会把对象放入池中。上述原则只适用于通过直接量给String对象引用赋值的情况。
     *
     * 举例：String str1 = "123"; //通过直接量赋值方式，放入字符串常量池
     * String str2 = new String(“123”);//通过new方式赋值方式，不放入字符串常量池
     *
     * 注意：String提供了inter()方法。调用该方法时，如果常量池中包括了一个等于此String对象的字符串（由equals方法确定），则返回池中的字符串。否则，将此String对象添加到池中，并且返回此池中对象的引用。
     *
     *
     * (2) String的特性
     * [A] 不可变。是指String对象一旦生成，则不能再对它进行改变。不可变的主要作用在于当一个对象需要被多线程共享，并且访问频繁时，可以省略同步和锁等待的时间，从而大幅度提高系统性能。不可变模式是一个可以提高多线程程序的性能，降低多线程程序复杂度的设计模式。
     *
     * [B] 针对常量池的优化。当2个String对象拥有相同的值时，他们只引用常量池中的同一个拷贝。当同一个字符串反复出现时，这个技术可以大幅度节省内存空间。
     *
     * 2 StringBuffer/StringBuilder
     *
     * StringBuffer和StringBuilder都实现了AbstractStringBuilder抽象类，拥有几乎一致对外提供的调用接口；其底层在内存中的存储方式与String相同，都是以一个有序的字符序列（char类型的数组）进行存储，不同点是StringBuffer/StringBuilder对象的值是可以改变的，并且值改变以后，对象引用不会发生改变;两者对象在构造过程中，首先按照默认大小申请一个字符数组，由于会不断加入新数据，当超过默认大小后，会创建一个更大的数组，并将原先的数组内容复制过来，再丢弃旧的数组。因此，对于较大对象的扩容会涉及大量的内存复制操作，如果能够预先评估大小，可提升性能。
     *
     * 唯一需要注意的是：StringBuffer是线程安全的，但是StringBuilder是线程不安全的。可参看Java标准类库的源代码，StringBuffer类中方法定义前面都会有synchronize关键字。为此，StringBuffer的性能要远低于StringBuilder。
     *
     *
     * 3 应用场景
     *
     * [A]在字符串内容不经常发生变化的业务场景优先使用String类。例如：常量声明、少量的字符串拼接操作等。如果有大量的字符串内容拼接，避免使用String与String之间的“+”操作，因为这样会产生大量无用的中间对象，耗费空间且执行效率低下（新建对象、回收对象花费大量时间）。
     *
     * [B]在频繁进行字符串的运算（如拼接、替换、删除等），并且运行在多线程环境下，建议使用StringBuffer，例如XML解析、HTTP参数解析与封装。
     *
     * [C]在频繁进行字符串的运算（如拼接、替换、删除等），并且运行在单线程环境下，建议使用StringBuilder，例如SQL语句拼装、JSON封装等。
     */
}

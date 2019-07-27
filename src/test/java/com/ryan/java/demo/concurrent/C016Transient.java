package com.ryan.java.demo.concurrent;

import lombok.Data;
import org.junit.Test;

import java.io.*;

/**
 * transient关键字：
 *  1）一旦变量被transient修饰，变量将不再是对象持久化的一部分，该变量内容在序列化后无法获得访问。
 *  2）transient关键字只能修饰变量，而不能修饰方法和类。
 *      注意，本地变量是不能被transient关键字修饰的。
 *
 *      变量如果是用户自定义类变量，则该类需要实现Serializable接口。
 *          fixme: 好像试过不是这样，见FooBar类?
 *  3）被transient关键字修饰的变量不再能被序列化，
 *      一个静态变量不管是否被transient修饰，均不能被序列化。
 */
public class C016Transient {

    @Test
    public void smoke() {
        User user = new User();
        user.setUsername("Alexia");
        user.setPasswd("123456");

        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUsername());
        System.out.println("password: " + user.getPasswd());

        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream("/tmp/user.txt"));
            os.writeObject(user); // 将User对象写进文件
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(
                    "/tmp/user.txt"));
            user = (User) is.readObject(); // 从流中读取User的数据
            is.close();

            System.out.println("\nread after Serializable: ");
            System.out.println("username: " + user.getUsername());
            System.out.println("password: " + user.getPasswd());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

@Data
class User implements Serializable {
    private static final long serialVersionUID = 8294180014912103005L;

    private String username;
    private transient String passwd;

    private transient FooBar bar;
}

class FooBar {
    boolean foo;
    int abc;
}

/**
 * 对象的序列化可以通过实现两种接口来实现，
 *  1. 若实现的是Serializable接口，则所有的序列化将会自动进行，
 *  2. 若实现的是Externalizable接口，则没有任何东西可以自动序列化，
 *      需要在writeExternal方法中进行手工指定所要序列化的变量，这与是否被transient修饰无关。
 *      因此第二个例子输出的是变量content初始化的内容，而不是null。
 */
class ExternalizableTest implements Externalizable {

    private transient String content = "是的，我将会被序列化，不管我是否被transient关键字修饰";

    public ExternalizableTest() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(content);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        content = (String) in.readObject();
    }

    public static void main(String[] args) throws Exception {

        ExternalizableTest et = new ExternalizableTest();
        ObjectOutput out = new ObjectOutputStream(new FileOutputStream(
                new File("/tmp/test")));
        out.writeObject(et);

        ObjectInput in = new ObjectInputStream(new FileInputStream(new File(
                "/tmp/test")));
        et = (ExternalizableTest) in.readObject();
        System.out.println(et.content);

        out.close();
        in.close();
    }
}

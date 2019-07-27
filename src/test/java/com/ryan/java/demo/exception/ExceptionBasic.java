package com.ryan.java.demo.exception;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionBasic {

}

// Error和Exception都继承于Throwable
// Error是不可恢复的错误
// 堆内存溢出
class Error2 extends OutOfMemoryError {
}
// stack溢出， 递归太深了
class Error3 extends StackOverflowError {
}
class Error4 extends NoClassDefFoundError {
}
class Error5 extends ExceptionInInitializerError {
}

// Exception可以分为检查类和非检查类异常
// 检查类必须在源代码中显示声明捕获处理
// 非检查类异常就是运行时异常
//  RuntimeException

class CheckedException1 extends IOException {
}

class UncheckedException1 extends RuntimeException {
}
class UncheckedException2 extends NullPointerException {
}
class UncheckedException3 extends ArrayIndexOutOfBoundsException {
}
class UncheckedException4 extends ClassCastException {
}

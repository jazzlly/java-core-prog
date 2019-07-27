package com.ryan.java.demo.string;

import org.junit.Test;

import java.io.Console;
import java.util.Scanner;

public class D003Scanner {
    // In intellij idea, system.in could only be captured
    // by main function instead of Junit function
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("What's your name?");
        String name = scanner.nextLine();
        System.out.println("Your name is: " + name);

        System.out.println("Please input a word: ");
        System.out.println(scanner.next());

        System.out.println("Please input a integer: ");
        System.out.println(scanner.nextInt());

        /* fixme: why console is null
        Console console = System.console();
        System.out.println("please input the password!");
        String pwd = console.readLine("Password:");
        System.out.println("The password is: " + pwd);
        */


    }
}

package com.ryan.java.demo.test;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import static java.time.LocalDate.ofYearDay;
import static org.assertj.core.api.Assertions.*;

// https://www.baeldung.com/assertJ-java-8-features
// http://www.vogella.com/tutorials/AssertJ/article.html
// http://joel-costigliola.github.io/assertj/
public class AssertJDemo {

    @Test
    public void descrilbeError() {
        String str = "foo";
        // as() is used to describe the test and will be shown before the error message
        assertThat(str.length())
                .as("Demo messge for as() function: check %s length failed!", str)
                .isEqualTo(4);
    }

    @Test
    public void stringTest() {
        String actual = "foo";
        String expected = "foo";

        assertThat(actual).isEqualTo(expected);

        // chaining string specific assertions
        assertThat("Frodo").startsWith("Fro")
                .endsWith("do")
                .isEqualToIgnoringCase("frodo");
    }

    @Test
    public void dateTest() {
        Date birthday = new Date(89, 1, 2);
        Date today = new Date();

        assertThat(birthday).isBefore(today);
    }

    @Test
    public void listTest() {
        List<String> userList = Arrays.asList("foo", "bar", "zar", "wahaha");

        assertThat(userList).isNotNull().isNotEmpty();
        assertThat(userList).contains("foo", atIndex(0))
                .contains("zar", atIndex(2))
                .containsOnlyOnce("foo", "bar", "zar")
                .doesNotContain("kakaka");

        // not pass, Expecting:
        //  <["foo", "bar", "zar", "wahaha"]>
        // assertThat(userList).containsOnly("foo", "bar");

        assertThat(userList).filteredOn(s -> s.contains("a"))
                .containsOnly("bar", "zar", "wahaha")
                .contains("bar", "zar");

        List<String> stringList = Arrays.asList("a", "ab", "abc");
        assertThat(stringList)
                .extracting(String::length)
                .contains(3, 2, 1);

        assertThat(stringList)
                .extracting(String::length, String::toUpperCase)
                .contains(tuple(1, "A"),
                        tuple(2, "AB"),
                        tuple(3, "ABC"));

        assertThat(stringList).filteredOn(s -> s.length() >=3)
                .containsOnly("abc")
                .extracting(String::length)
                .containsOnly(3);
    }

    @Test
    public void mapTest() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "x");
        map.put("b", "y");
        map.put("c", "z");

        // check for multiple keys at once
        assertThat(map).containsKeys("a", "b", "c");
        // check if the value of a certain key satisfies a condition
        // assertThat(map).hasEntrySatisfying(key, String::isEmpty);
        // check if all entries of an other map are contained in a map
        // assertThat(map).containsAllEntriesOf(expectedSubset);
    }

    @Test
    public void listErrorMsg() {
        List<String> list = new ArrayList<>();
        assertThat(list).contains("foo");
    }

    @Test
    public void optionalTest() {
        Optional<String> givenOptional = Optional.of("something");
        assertThat(givenOptional)
                .isPresent()
                .hasValue("something");
    }

    @Test
    public void predicateTest() {
        Predicate<String> predicate = s -> s.length() > 4;

        assertThat(predicate)
                .accepts("aaaaa", "bbbbb")
                .rejects("a", "b")
                .acceptsAll(Arrays.asList("aaaaa", "bbbbb"))
                .rejectsAll(Arrays.asList("a", "b"));
    }

    @Test
    public void localDateTest() {
        LocalDate givenLocalDate = LocalDate.of(2016, 7, 8);
        LocalDate todayDate = LocalDate.now();

        assertThat(givenLocalDate)
                .isBefore(LocalDate.of(2020, 7, 8))
                .isAfterOrEqualTo(LocalDate.of(1989, 7, 8));

        assertThat(todayDate)
                .isAfter(LocalDate.of(1989, 7, 8))
                .isToday();
    }

    @Test
    public void localDateTimeTest() {
        LocalDateTime givenLocalDate = LocalDateTime.of(2016, 7, 8, 12, 0);
        assertThat(givenLocalDate)
                .isBefore(LocalDateTime.of(2020, 7, 8, 11, 2));
    }

    @Test
    public void flatExtractingTest() {
        List<LocalDate> givenList = Arrays.asList(ofYearDay(2016, 5), ofYearDay(2015, 6));

        assertThat(givenList)
                .flatExtracting(LocalDate::getYear)
                .contains(2015);

        assertThat(givenList)
                .flatExtracting(Object::getClass)
                .contains(LocalDate.class);

        assertThat(givenList)
                .flatExtracting(LocalDate::getYear, LocalDate::getDayOfMonth)
                .contains(2015, 6);
    }

    @Test
    public void satisfiesSingleTest() {
        String givenString = "someString";
        assertThat(givenString)
                .satisfies(s -> {
                    assertThat(s).isNotEmpty();
                    assertThat(s).hasSize(10);
                });
    }

    @Test
    public void satisfiesListAllMatch() {
        List<String> strings = Arrays.asList("foo", "bar", "zar");
        assertThat(strings).allSatisfy(s -> {
            assertThat(s).hasSize(3);
        });
    }

    @Test
    public void satisfiesListAnyMatch() {
        List<String> strings = Arrays.asList("foo", "bar", "zar", "wahaha");
        assertThat(strings).anySatisfy(s -> {
            assertThat(s).hasSize(6);
        });
    }

    @Test
    public void satisfiesListOnlyOneMatchTest() {
        List<String> givenList = Arrays.asList("");
        assertThat(givenList)
                .hasOnlyOneElementSatisfying(s -> assertThat(s).isEmpty());
    }

    @Test
    public void matchTest() {
        String emptyString = "";
        assertThat(emptyString).matches(String::isEmpty);
    }
}

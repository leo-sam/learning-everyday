package org.example.stream;

import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author huangsy
 */
public class Java8Stream {

    public static void main(String[] args) {
        streamListToString();
        streamListToMap();
        removeDuplicationByStream();
    }

    /**
     * 把List转化成String
     */
    public static void streamListToString() {
        List<String> list1 = Arrays.asList("文学","小说","历史","言情","科幻","悬疑");

        List<String> list2 = Arrays.asList("文学","小说","历史","言情","科幻","悬疑");

        //方案一：使用String.join()函数，给函数传递一个分隔符合一个迭代器，一个StringJoiner对象会帮助我们完成所有的事情
        String string1 = String.join("-",list1);
        System.out.println("string1 = " + string1);
        //方案二：采用流的方式来写
        String string2 = list2.stream().collect(Collectors.joining(","));
        System.out.println("string2 = " + string2);
    }

    /**
     * 把List转化成Map
     */
    public static void streamListToMap() {
        User u1 = new User("1", true, "a");
        User u2 = new User("2", false, "b");
        User u3 = new User("3", false, "c");
        List<User> userList = Arrays.asList(u1, u2, u3);

        Map<String, User> userMap = userList.stream().collect(Collectors.toMap(User::getName, v -> v));
        System.out.println("userMap = " + userMap);

        // 排除bean为空
        User u4 = null;
        List<User> userList2 = Arrays.asList(u1, u2, u3, u4);
        Map<String, User> userMapWithoutNull = userList2.stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(User::getName, v -> v));
        System.out.println("userMapWithoutNull = " + userMapWithoutNull);
    }

    /**
     * 使用java8新特性stream实现List去重(有序)
     */
    public static void removeDuplicationByStream() {
        User u1 = new User("1", true, "a");
        User u2 = new User("2", false, "b");
        List<User> userList = Arrays.asList(u1, u2, u2);
        List<User> newList = userList.stream().distinct().collect(Collectors.toList());
        System.out.println("newList = " + newList);
    }
    /**
     * 模拟Bean
     */
    @Data
    public static class User {
        private String about;

        private boolean lock;

        private String name;

        public User() {
        }

        public User(String about, boolean lock, String name) {
            super();
            this.about = about;
            this.lock = lock;
            this.name = name;
        }
    }
}

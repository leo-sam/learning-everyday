package org.example.stream;

import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huangsy
 */
public class Java8Stream {

    public static void main(String[] args) {
        streamListToString();
        streamListToMap();
        removeDuplicationByStream();
        streamListCompare();
        streamListCollectors();
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
        User u1 = new User(1, true, "a");
        User u2 = new User(2, false, "b");
        User u3 = new User(3, false, "c");
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
     * 根据List中的某一个字段进行大小比较
     */
    public static void streamListCompare() {
        User u1 = new User(1, true, "a");
        User u2 = new User(2, false, "b");
        User u3 = new User(3, false, "c");
        List<User> userList = Arrays.asList(u1, u2, u3);

        // max
        userList.stream().map(User::getId).max(Comparator.comparing(Integer::intValue))
                .ifPresent(e -> System.out.println("Max: " + e));
        // min
        userList.stream().map(User::getId).min(Comparator.comparing(Integer::intValue))
                .ifPresent(e -> System.out.println("Min: " + e));
    }

    /**
     * Collector 工具库：Collectors
     * 感谢原作者:旅行者yky
     * 地址:https://blog.csdn.net/y_k_y/article/details/84633001
     */
    public static void streamListCollectors() {
        Student s1 = new Student("aa", 10,1);
        Student s2 = new Student("bb", 20,2);
        Student s3 = new Student("cc", 10,3);
        List<Student> list = Arrays.asList(s1, s2, s3);

        //装成list
        // [10, 20, 10]
        List<Integer> ageList = list.stream().map(Student::getAge).collect(Collectors.toList());
        System.out.println("ageList = " + ageList);

        //转成set
        // [20, 10]
        Set<Integer> ageSet = list.stream().map(Student::getAge).collect(Collectors.toSet());
        System.out.println("ageSet = " + ageSet);

        //转成map,注:key不能相同，否则报错
        // {cc=10, bb=20, aa=10}
        Map<String, Integer> studentMap = list.stream().collect(Collectors.toMap(Student::getName, Student::getAge));
        System.out.println("studentMap = " + studentMap);

        //字符串分隔符连接
        // (aa,bb,cc)
        String joinName = list.stream().map(Student::getName).collect(Collectors.joining(",", "(", ")"));
        System.out.println("joinName = " + joinName);

        //聚合操作
        //1.学生年龄小于20的数量
        // 2
        long count = list.stream().filter(s -> s.age < 20).count();
        //2.最大年龄 (最小的minBy同理)
        // 20
        Integer maxAge = list.stream().map(Student::getAge).max(Integer::compare).get();
        //3.所有人的年龄
        // 40
        int sumAge = list.stream().mapToInt(Student::getAge).sum();
        //4.平均年龄
        // 13.333333333333334
        Double averageAge = list.stream().collect(Collectors.averagingDouble(Student::getAge));
        System.out.println("count:" + count + ",max:" + maxAge + ",sum:" + sumAge + ",average:" + averageAge);

        // 带上以上所有方法
        DoubleSummaryStatistics statistics = list.stream().collect(Collectors.summarizingDouble(Student::getAge));
        System.out.println("DoubleSummaryStatistics count:" + statistics.getCount() + ",max:" + statistics.getMax() + ",sum:" + statistics.getSum() + ",average:" + statistics.getAverage());

        //分组
        Map<Integer, List<Student>> ageMap = list.stream().collect(Collectors.groupingBy(Student::getAge));
        System.out.println("ageMap = " + ageMap);
        //多重分组,先根据类型分再根据年龄分
        Map<Integer, Map<Integer, List<Student>>> typeAgeMap = list.stream().collect(Collectors.groupingBy(Student::getType, Collectors.groupingBy(Student::getAge)));
        System.out.println("typeAgeMap = " + typeAgeMap);
        //分区
        //分成两部分，一部分大于10岁，一部分小于等于10岁
        Map<Boolean, List<Student>> partMap = list.stream().collect(Collectors.partitioningBy(v -> v.getAge() > 10));
        System.out.println("partMap = " + partMap);
        //规约
        //40
        Integer allAge = list.stream().map(Student::getAge).reduce(Integer::sum).get();
        System.out.println("allAge = " + allAge);
    }

    /**
     * 使用java8新特性stream实现List去重(有序)
     */
    public static void removeDuplicationByStream() {
        User u1 = new User(1, true, "a");
        User u2 = new User(2, false, "b");
        List<User> userList = Arrays.asList(u1, u2, u2);
        List<User> newList = userList.stream().distinct().collect(Collectors.toList());
        System.out.println("newList = " + newList);
    }
    /**
     * 模拟Bean
     */
    @Data
    public static class User {
        private int id;

        private boolean lock;

        private String name;

        public User() {
        }

        public User(int id, boolean lock, String name) {
            super();
            this.id = id;
            this.lock = lock;
            this.name = name;
        }
    }
    @Data
    public static class Student {

        private String name;
        private int age;
        private int type;

        public Student() {
        }

        public Student(String name, int age, int type) {
            super();
            this.age = age;
            this.type = type;
            this.name = name;
        }
    }
}

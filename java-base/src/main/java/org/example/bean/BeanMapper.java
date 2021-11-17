package org.example.bean;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Data;
import org.dozer.DozerBeanMapper;

/**
 * 简单封装Dozer, 实现深拷贝转换Bean<->Bean 的Mapper.
 *
 * 实现:
 * 1. 持有Mapper的单例.
 * 2. 返回值类型转换.
 * 3. 批量转换Collection中的所有对象.
 * 4. 区分创建新的目标对象和将源对象值复制到已存在的目标对象两种方法.
 *
 * @author core
 */
public class BeanMapper {

    public static void main(String[] args) {
        User u1 = new User("1", true, null);
        User u2 = new User();

        BeanMapper.copy(u1, u2);
        System.out.println("u2 "+ u2);
    }
    /**
     * DozerBeanMapper的单例
     */
    private enum DozerHolder {
        // MAPPER
        MAPPER;

        private final DozerBeanMapper dozer;

        DozerHolder() {
            this.dozer = new DozerBeanMapper();
            dozer.setMappingFiles(Collections.singletonList("dozerJdk8Converters.xml"));
        }

        public DozerBeanMapper getMapper() {
            return this.dozer;
        }
    }

    /**
     * 转换对象的类型.
     *
     * @param source 源对象
     * @param destinationClass 目标类型
     *
     * @return 复制后的目标类型对象
     */
    public static <T, M> T map(M source, Class<T> destinationClass) {
        if (Objects.isNull(source) || Objects.isNull(destinationClass)) {
            throw new IllegalArgumentException();
        }

        return DozerHolder.MAPPER.getMapper().map(source, destinationClass);
    }

    /**
     * 转换Collection中对象的类型.
     *
     * @param sourceList 源集合
     * @param destinationClass 目标类型
     *
     * @return 目标类型的集合
     */
    public static <T, M> List<T> mapList(Collection<M> sourceList, Class<T> destinationClass) {
        if (Objects.isNull(sourceList) || Objects.isNull(destinationClass) || sourceList.isEmpty()) {
            throw new IllegalArgumentException();
        }

        return Optional.of(sourceList)
                .orElse(new ArrayList<>())
                .stream().map(m -> map(m, destinationClass))
                .collect(Collectors.toList());
    }

    /**
     * 将源对象的值深拷贝到目标对象中.
     *
     * @param source 源对象
     * @param destinationObject 目标对象
     */
    public static <T, M> void copy(M source, T destinationObject) {
        if (Objects.isNull(source) || Objects.isNull(destinationObject)) {
            throw new IllegalArgumentException();
        }

        DozerHolder.MAPPER.getMapper().map(source, destinationObject);
    }

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
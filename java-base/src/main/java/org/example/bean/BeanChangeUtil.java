package org.example.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author huang
 */
public class BeanChangeUtil<T> {
    public static <T> void main(String[] args) {
        User u1 = new User("1", true, null);
        User u2 = new User("2", false, "b");
        BeanChangeUtil<T> t = new BeanChangeUtil<>();
        String str = t.contrastObj(u1, u2);
        if ("".equals(str)) {
            System.out.println("未有改变");
        } else {
            System.out.println(str);
        }
    }

    /**
     * contrastObj
     * @param oldBean old bean
     * @param newBean new bean
     * @return change
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String contrastObj(Object oldBean, Object newBean) {
        T pojo1 = (T) oldBean;
        T pojo2 = (T) newBean;
        JSONObject changeJson = new JSONObject();
        try {
            // 通过反射获取类的类类型及字段属性
            Class clazz = pojo1.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int i = 1;

            for (Field field : fields) {
                // 排除序列化属性
                if ("serialVersionUID".equals(field.getName())) {
                    continue;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                // 获取对应属性值
                Method getMethod = pd.getReadMethod();
                Object o1 = getMethod.invoke(pojo1);
                Object o2 = getMethod.invoke(pojo2);
                if (o1 == null && o2 == null) {
                    continue;
                }
                if (!Objects.equals(o1, o2)) {
                    ArrayList<Object> objects = new ArrayList<>();
                    objects.add(o1);
                    objects.add(o2);
                    changeJson.put(field.getName(), objects);
                    i++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changeJson.toString();
    }

    @Data
    static class User {
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

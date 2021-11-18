package cn.itcast.unSafeTest;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTest {
    public static void main(String[] args) {
        //获取对象中volatile的成员变量
        try {
            Field id = Student.class.getDeclaredField("id");
            Field name = Student.class.getDeclaredField("name");

            //私有的必须要设置可访问
            id.setAccessible(true);
            name.setAccessible(true);
            //获取成员变量相对对象的偏移量
            Unsafe unsafe = UnsafeAccessor.getUnsafe();

            long idOffset = UnsafeAccessor.unsafe.objectFieldOffset(id);
            long nameOffset = UnsafeAccessor.unsafe.objectFieldOffset(name);
            //使用unsafe对象进行cas操作
            Student student = new Student();
            boolean casLong = UnsafeAccessor.unsafe.compareAndSwapLong(student, idOffset, 0L, 100L);
            boolean casName = UnsafeAccessor.unsafe.compareAndSwapObject(student, nameOffset, null, "叶源");
            System.out.println(casLong);
            System.out.println(casName);
            System.out.println(student.getId());
            System.out.println(student.getName());

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }







    }
}

package cn.itcast.unSafeTest;


import sun.misc.Unsafe;

import java.lang.reflect.Field;

//LockSupport 、原子类型底层都是用 UnSafe对象
public class UnsafeAccessor {
    static Unsafe unsafe;
    //Unsafe 对象只能反射获取
    static {
        try {
            //获取域
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            //设置可访问
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        }catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }
    static Unsafe getUnsafe() {

        return unsafe;
    }
}

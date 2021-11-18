package cn.itcast.unSafeTest;

import lombok.Data;

@Data
public class Student {
     volatile int id;
     volatile String name;
}

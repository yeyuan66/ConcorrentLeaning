package cn.itcast.account;

import cn.itcast.account.impl.DecimalAccountCas;
import cn.itcast.account.impl.DecimalAccountLock;
import cn.itcast.account.impl.DecimalAccountUnSafe;
import com.sun.org.apache.xpath.internal.operations.String;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;
import java.lang.*;
public class AccountTest {
    public static void main(String[] args) {
        DecimalAccountUnSafe accountUnSafe = new DecimalAccountUnSafe(new BigDecimal(10000));
        DecimalAccount.work(accountUnSafe);

        DecimalAccountLock decimalAccountLock = new DecimalAccountLock(new BigDecimal(10000));
        DecimalAccount.work(decimalAccountLock);

        DecimalAccountCas decimalAccountCas = new DecimalAccountCas(new AtomicReference<BigDecimal>(new BigDecimal(10000)));
        DecimalAccount.work(decimalAccountCas);

        boolean[] booleans = new boolean[2];
        for (boolean aBoolean : booleans) {
            System.out.println(aBoolean);




        }



    }
}

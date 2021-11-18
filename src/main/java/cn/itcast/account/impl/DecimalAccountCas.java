package cn.itcast.account.impl;

import cn.itcast.account.DecimalAccount;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

public class DecimalAccountCas implements DecimalAccount {

    AtomicReference<BigDecimal> balance ;

    public DecimalAccountCas(AtomicReference<BigDecimal> balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance.get();
    }

    @Override
    public void decrement(BigDecimal amount) {
        //在死循环中不停CAS
        while (true){
            BigDecimal balance = this.getBalance();
            BigDecimal next = balance.subtract(amount);
            if(this.balance.compareAndSet(balance,next))break;
        }
    }
}

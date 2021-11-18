package cn.itcast.account.impl;

import cn.itcast.account.DecimalAccount;

import java.math.BigDecimal;

public class DecimalAccountLock implements DecimalAccount {
    private final Object lock = new Object();
    private BigDecimal balance;

    public DecimalAccountLock(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void decrement(BigDecimal amount) {
        synchronized (lock){
            BigDecimal balance = this.getBalance();
            this.balance = balance.subtract(amount);
        }
    }
}

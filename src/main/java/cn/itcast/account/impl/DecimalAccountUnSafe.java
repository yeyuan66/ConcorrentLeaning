package cn.itcast.account.impl;

import cn.itcast.account.DecimalAccount;

import java.math.BigDecimal;

public class DecimalAccountUnSafe implements DecimalAccount {
    private BigDecimal balance;

    public DecimalAccountUnSafe(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void decrement(BigDecimal amount) {
        BigDecimal balance = this.getBalance();
        this.balance = balance.subtract(amount);
    }

    public static void main(String[] args) {
        DecimalAccountUnSafe accountUnSafe = new DecimalAccountUnSafe(new BigDecimal(100));
        DecimalAccount.work(accountUnSafe);
    }
}

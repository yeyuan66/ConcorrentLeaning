package cn.itcast.JUCLeaning.aqs;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;

public class MySync extends AbstractQueuedSynchronizer {

    //独占方式获取资源
    @Override
    protected boolean tryAcquire(int acquires) {
        if(acquires == 1){
            //设置获取锁
            if(compareAndSetState(0,1)){
                //当前为独占线程
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
        }
        return false;
    }

    //独占方式释放资源
    @Override
    protected boolean tryRelease(int acquires) {
        if (acquires == 1){
            if (getState() != 0){
                throw new IllegalMonitorStateException();
            }
            //置空独占线程
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }
        return false;
    }

    protected Condition newCondition() {
        return new ConditionObject();
    }

    @Override
    protected boolean isHeldExclusively(){
        return getState() == 1;
    }
}

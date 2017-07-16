package com.yingbu.nettools.securitynettransfer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Yingbu on 2017/7/15.
 * Thread-Pool Manager
 */
public class ThreadPool {
    private static ThreadPool currentThreadPool;

    private ExecutorService poolService;
    private ExecutorService poolWorker;
    private int THREAD_POOL_SIZE = 32;

    public static ThreadPool currentThreadPool(){
        if(currentThreadPool == null){
            currentThreadPool = new ThreadPool();
        }
        return currentThreadPool;
    }

    private ThreadPool() {
        poolService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        poolWorker = Executors.newFixedThreadPool(THREAD_POOL_SIZE * 3); //ClientThread -> DecryptForwardThread and EncryptForwardThread and 50% redundancy.
    }

    public void submitClientThread(Runnable thread){
        poolService.submit(thread);
    }

    public void submitWorkThread(Runnable thread){
        poolWorker.submit(thread);
    }

    public void shutdown(){
        try {
            System.out.println("attempt to shutdown executor");
            poolService.shutdown();
            poolService.awaitTermination(5, TimeUnit.SECONDS);
            poolWorker.shutdown();
            poolWorker.awaitTermination(5,TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!poolService.isTerminated()) {
                System.err.println("poolService:cancel non-finished tasks");
            }
            if (!poolWorker.isTerminated()) {
                System.err.println("poolWorker:cancel non-finished tasks");
            }

            poolService.shutdownNow();
            poolWorker.shutdownNow();
            System.out.println("shutdown finished");
        }
    }
}

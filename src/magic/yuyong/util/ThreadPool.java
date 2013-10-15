package magic.yuyong.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	private ExecutorService threadPool;
	private static final int DEFAULT_THREAD_NUM = 3;
	
	public ThreadPool() {
		this(DEFAULT_THREAD_NUM);
	}
	
	public ThreadPool(int threadNum) {
		super();
		threadPool = Executors.newFixedThreadPool(threadNum);
	}
	
	public void discard(){
		threadPool.shutdownNow();
	}
	
	public void execute(Runnable command){
		threadPool.execute(command);
	}
	
}

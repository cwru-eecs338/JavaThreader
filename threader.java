import java.util.*;
import java.util.concurrent.Semaphore;

public class threader 
{
	public static void main(String[] args) 
	{
		new ReadersWriters();
	}
}

class ReadersWriters 
{
	public static final int NUMREADERS = 10;
	public static final int NUMWRITERS = 5;
	
	public ReadersWriters()
	{
		SharedData shared = new SharedData();
		ReaderThread[] readers = new ReaderThread[NUMREADERS];
		WriterThread[] writers = new WriterThread[NUMWRITERS];
		Thread t = new Thread();
		for(int i = 0; i < NUMREADERS; i++)
		{
			readers[i] = new ReaderThread(shared, i);
			readers[i].start();
		}
		
		for(int i = 0; i < NUMWRITERS; i++)
		{
			writers[i] = new WriterThread(shared, i);
			writers[i].start();
		}
		try
		{
			for(int i = 0; i < NUMREADERS; i++)
			{
				readers[i].join();
			}
			
			for(int i = 0; i < NUMWRITERS; i++)
			{
				writers[i].join();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}	
	}		
}

class SharedData
{
	public Semaphore wrt;
	public Semaphore mutex;
	public int readCount;
	
	public SharedData()
	{
		wrt = new Semaphore(1);
		mutex = new Semaphore(1);
		readCount = 0;
	}
}

class ReaderThread extends Thread
{
	int ID;
	SharedData shared;
	Random rand;
	public ReaderThread(SharedData shared, int num)
	{
		ID = num;
		this.shared = shared;	
		rand = new Random();
	}
	public void run()
	{
		try
		{
			Thread.sleep(rand.nextInt(100)); //Idle for 1-100ms
			
			shared.mutex.acquire();
			shared.readCount++;
			if(shared.readCount == 1)
			{
				shared.wrt.acquire();
			}
			shared.mutex.release();
			
			//Reader CS
			System.out.printf("Reader %d enters CS\n", ID);
			Thread.sleep(rand.nextInt(10)); //Simulate a read operation that takes 1-10ms
			System.out.printf("Reader %d exits CS\n", ID);
			
			shared.mutex.acquire();
			shared.readCount--;
			if(shared.readCount == 0)
			{
				shared.wrt.release();
			}
			shared.mutex.release();
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}

class WriterThread extends Thread
{
	int ID;
	SharedData shared;
	Random rand;
	public WriterThread(SharedData shared, int num)
	{
		ID = num;
		this.shared = shared;
		rand = new Random();
	}
	public void run()
	{
		try
		{
			Thread.sleep(rand.nextInt(200)); //Idle for 1-200ms
			
			shared.wrt.acquire();
			
			//Writer CS
			System.out.printf("Writer %d enters CS\n", ID);
			Thread.sleep(rand.nextInt(50)); //Simulate a write operation that takes 1-50ms
			System.out.printf("Writer %d exits CS\n", ID);
			
			shared.wrt.release();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());			
		}
	}
}

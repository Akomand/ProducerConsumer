import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProducerConsumer {
	
   private static Semaphore empty = new Semaphore(5);
   private static Semaphore full  = new Semaphore(0);
   private static Semaphore mutex = new Semaphore(1);
   
   private static ArrayList <Integer> boundedBuffer = new ArrayList <Integer>(5);
   
   // PRODUCER CLASS
   static class Producer extends Thread{

	   private int count = 0;
	   private int value;
	   private int id;

	   Random random = new Random();

	   // Constructor
	   public Producer(int id) {	
           this.id = id;	
        }

       @Override
	   public void run() {
	   	try {
	   		while(count < 100) {
	
	   		
	   		Thread.sleep(random.nextInt(500));	// random sleep time between 0 and 0.5 sec
	   		
	   		empty.acquire();
	   		mutex.acquire(); // lock
	   		
	   		value = random.nextInt(100000);
	   		boundedBuffer.add(value); //add the value to the boundedBuffer
	   		System.out.println("Producer produced " + value);
	   		
	   		
	   		mutex.release(); // release lock
	   		full.release(); 
			count++;
	   		
	   		}
	   	} catch (Exception e) { 
			   e.printStackTrace(); 
			}
	   }
	   	
	   }
   
   
   // CONSUMER CLASS
   static class Consumer extends Thread{
	
	   private int count = 0;
	   private int id;

	   Random random = new Random();

	   // Constructor
	   public Consumer(int id) {
		   this.id = id;
		}

	   @Override
	   public void run() {
	   	
	   	try {
	   		while(count < 100) {
	   		Thread.sleep(random.nextInt(500));		// random sleep time between 0 and 0.5 sec
	   		
			full.acquire();
	   		mutex.acquire();

	   		
	   		System.out.println("\tConsumer consumed " + boundedBuffer.get(0));
            boundedBuffer.remove(0);
	   		
	   		mutex.release();
	   		empty.release();
	   		
			count++;
	   		}
	   		
	   	} catch (Exception e) {
	   		e.printStackTrace();
	   	}
	   }
	   	
	   }


		// MAIN PROGRAM - TEST 
	   public static void main(String[] args) throws InterruptedException {
	   try {
		
		   if(args.length > 3 || args.length < 3) {
		   System.out.println("Invalid args. Try again.");
		   System.exit(0);
		   }

		   int sleepTime = Integer.parseInt(args[0]);
		   int producerCount = Integer.parseInt(args[1]);
		   int consumerCount = Integer.parseInt(args[2]);
		   
		   if (sleepTime <= 0 || producerCount <= 0 || consumerCount <= 0) {
			   System.out.println("Invalid args. Try again.");
			   System.exit(0);
		   }
	   
		   System.out.println("Using arguments from command line"); 	
	   	   System.out.println("Sleep time = " + sleepTime);
		   System.out.println("Producer threads = " + producerCount);
		   System.out.println("Consumer threads = " + consumerCount + "\n");
	 
		   for (int i = 0; i < producerCount; i++) {
			   Producer producer = new Producer(i);
               producer.start();
           }

		   for (int j = 0; j < consumerCount; j++) {
			   Consumer consumer = new Consumer(j);
               consumer.start();
           }
	   
		   Thread.sleep(sleepTime*1000);		// sleepTime second run
		   System.exit(1);
       
	   } catch (Exception e) {
			System.out.println("Invalid args. Try again.");
			System.exit(0);
		}
   }
   
}
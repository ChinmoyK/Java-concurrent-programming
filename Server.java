import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
import java.util.concurrent.Semaphore;
import java.text.SimpleDateFormat;
import java.util.Date;
  
// Server class 
public class Server{ 
    static int cookie_count = 100;
    static int snacks_count = 100;

    static int tea_profit = 0;
    static int coffee_profit = 0;
    static int cookie_profit = 0;
    static int snacks_profit = 0;

    static int tea_price = 10;
    static int coffee_price = 15;
    static int cookie_price = 17;
    static int snacks_price = 30;

    static int threshold = 10;
    static int cf=0;
    static int sf=0;

    public static void main(String[] args) throws IOException  
    {  
        ServerSocket ss = new ServerSocket(5000); 
        while (true)  
        { 
            Socket s = null;            
            try 
            { 
                s = ss.accept();                  
                System.out.println("A new client is connected : " + s); 

                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());         
                System.out.println("Assigning new thread for this client");

                Thread t = new ClientHandler(s, dis, dos);  
                t.start(); 
                  
            } 
            catch (Exception e){ 
                s.close(); 
                e.printStackTrace(); 
            } 
        } 
    } 
} 
   
class ClientHandler extends Thread  
{ 
     
    Server ser = new Server();

    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket s;

    static Semaphore semaphore = new Semaphore(1); 
      
    // Constructor 
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos){ 
        this.s = s; 
        this.dis = dis; 
        this.dos = dos; 
    } 
  
    @Override
    public void run(){     
        String received; 
        String toreturn;     
            try { 
                dos.writeUTF(ser.cookie_count+","+ser.snacks_count);                   
                received = dis.readUTF(); 

                System.out.println(received);
                String[] value = received.split(",");

                int tea = Integer.parseInt(value[0]);
                int coffee = Integer.parseInt(value[1]);
                int cookies = Integer.parseInt(value[2]);
                int snacks = Integer.parseInt(value[3]); 
                String customer_name = value[4];

                System.out.println(customer_name +" Requested: " + tea + " "+ coffee +  " "+ cookies +" "+ snacks);

                try{
                    semaphore.acquire();
                    Thread.sleep(15000);
                    String threadname = Thread.currentThread().getName();
                    try{
                        if(ser.cookie_count >= cookies && ser.snacks_count >= snacks){
                            ser.cookie_count-=cookies;
                            ser.snacks_count-=snacks;                   

                            ser.tea_profit+= tea*ser.tea_price;
                            ser.coffee_profit+=coffee*ser.coffee_price;
                            ser.cookie_profit+=cookies*ser.cookie_price;
                            ser.snacks_profit+=snacks*ser.snacks_price;
                            int total = tea*ser.tea_price + coffee*ser.coffee_price + cookies*ser.cookie_price + snacks*ser.snacks_price;
                            int total_profit = ser.tea_profit + ser.coffee_profit+ ser.cookie_profit + ser.snacks_profit;

                            System.out.println("tea profit: " + ser.tea_profit);
                            System.out.println("coffee profit: " + ser.coffee_profit);
                            System.out.println("cookies profit: " + ser.cookie_profit);
                            System.out.println("snacks profit: " + ser.snacks_profit);

                            File file1 = new File("sales_log.txt");
                            String filename1= "sales_log.txt";
                            FileWriter fw1 = new FileWriter(filename1,true); //the true will append the new data
                            fw1.write(customer_name+"\n" + "-----------------\n" +
                               "Tea: " +tea*ser.tea_price +
                               "\nCoffee: " + coffee*ser.coffee_price +
                               "\nCookies: " + cookies*ser.cookie_price +
                               "\nSnacks: " + snacks*ser.snacks_price + 
                               "\nTotal: " + total + 
                                "\n \n \n");
                            fw1.close();

                            File file2 = new File("profit_sales.txt");
                            String filename2= "profit_sales.txt";
                            FileWriter fw2 = new FileWriter(filename2); 
                            fw2.write("-------------------\n" +
                                "\nTea profit: " + ser.tea_profit +
                                "\nCoffee profit: "+ ser.coffee_profit +
                                "\nCookies profit: " + ser.cookie_profit + 
                                "\nSnacks profit: " + ser.snacks_profit +
                                "\nTotal profit: " + total_profit +
                                "\n-------------------\n");
                            fw2.close();

                            //For generating sales by day
                            Date date = new Date() ;
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd") ;
                            File file3 = new File(dateFormat.format(date) + ".txt");
                            String filename3 = dateFormat.format(date) + ".txt";
                            FileWriter fw3 = new FileWriter(filename3); 
                            fw3.write("Sales for "+ dateFormat.format(date) +
                                "\n-------------------\n" +
                                "Tea profit: " + ser.tea_profit +
                                "\nCoffee profit: "+ ser.coffee_profit +
                                "\nCookies profit: " + ser.cookie_profit + 
                                "\nSnacks profit: " + ser.snacks_profit +
                                "\nTotal profit: " + total_profit +
                                "\n-------------------\n");
                            fw3.close();

                            //For generating sales by month
                            Date date2 = new Date() ;
                            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM") ;
                            File file4 = new File(dateFormat2.format(date2) + ".txt");
                            String filename4 = dateFormat2.format(date2) + ".txt";
                            FileWriter fw4 = new FileWriter(filename4); 
                            fw4.write("Sales for "+ dateFormat2.format(date2) +
                                "\n-------------------\n" +
                                "Tea profit: " + ser.tea_profit +
                                "\nCoffee profit: "+ ser.coffee_profit +
                                "\nCookies profit: " + ser.cookie_profit + 
                                "\nSnacks profit: " + ser.snacks_profit +
                                "\nTotal profit: " + total_profit +
                                "\n-------------------\n");
                            fw4.close();

                        }
                        else{
                            System.out.println("The customer order exceeded stock");
                        }
                    }catch(Exception e){
                        System.out.println("I was interrupted");
                    }

                    finally{
                        semaphore.release();
                        System.out.println(threadname + " has released the Lock!!");
                    }
                }catch(Exception e1){
                    System.out.println("I was interrupted here");
                }    

                if(ser.cookie_count < ser.threshold && ser.cf == 0){
                    File file = new File("purchase_list.txt");
                    String filename= "purchase_list.txt";
                    FileWriter fw = new FileWriter(filename,true); 
                    fw.write("Purchase more cookies\n");
                    fw.close();
                    ser.cf = 1;
                }
                if(ser.snacks_count < ser.threshold && ser.sf == 0){
                    String filename= "purchase_list.txt";
                    FileWriter fw = new FileWriter(filename,true); 
                    fw.write("Purchase more snacks\n");
                    fw.close();
                    ser.sf = 1;
                }

                System.out.println("cookie_count: " + ser.cookie_count);
                System.out.println("snacks_count: " + ser.snacks_count); 
 
                this.s.close(); 
            
            } catch (IOException e) { 
                e.printStackTrace(); 
            } 
                 
        try
        { 
            this.dis.close(); 
            this.dos.close(); 
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
} 
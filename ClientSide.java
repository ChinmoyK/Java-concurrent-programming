import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;  
import java.awt.event.*;
import java.net.*;
import java.util.Calendar;
public class ClientSide extends JFrame implements ActionListener{ 

    JFrame jf; 
    JLabel l1,l2,quant,cookie,snacks;  
    JCheckBox cb1,cb2,cb3,cb4;  
    JButton place_order;  
    JTextField roomtf,btf1,btf2,btf3,btf4;
    int c_count,s_count;
    
    ClientSide(){  
        l1=new JLabel("Food Delivery System");  
        l1.setBounds(50,50,300,20); 
        l1.setFont(new Font("Serif", Font.BOLD, 18));

        l2=new JLabel("Enter room/lab no.");  
        l2.setBounds(200,80,200,20);  
        l2.setFont(new Font("Serif", Font.BOLD, 18));

        roomtf = new JTextField();
        roomtf.setBounds(200,100,150,20);

        quant=new JLabel("Enter Quantity");  
        quant.setBounds(50,180,150,20);
        quant.setFont(new Font("Serif", Font.BOLD, 18));

        btf1 = new JTextField();
        btf1.setBounds(200,180,60,20);

        btf2 = new JTextField();
        btf2.setBounds(400,180,60,20);

        btf3 = new JTextField();
        btf3.setBounds(600,180,60,20);

        btf4 = new JTextField();
        btf4.setBounds(800,180,60,20);

        cb1=new JCheckBox("Tea @ 10");  
        cb1.setBounds(200,150,150,20);
        cb1.setFont(new Font("Serif", Font.BOLD, 15));

        cb2=new JCheckBox("Coffee @ 15");  
        cb2.setBounds(400,150,150,20);  
        cb2.setFont(new Font("Serif", Font.BOLD, 15));

        cb3=new JCheckBox("Cookies @ 17");  
        cb3.setBounds(600,150,150,20);
        cb3.setFont(new Font("Serif", Font.BOLD, 15));

        cb4=new JCheckBox("Snacks @ 30");  
        cb4.setBounds(800,150,150,20);  
        cb4.setFont(new Font("Serif", Font.BOLD, 15));

        place_order=new JButton("Place Order");  
        place_order.setBounds(200,250,120,30);  
        place_order.addActionListener(this); 

        cookie = new JLabel("Only "+c_count+" cookies left");
        cookie.setBounds(600,250,150,20); 

        snacks = new JLabel("Only "+s_count+" snacks left");
        snacks.setBounds(800,250,150,20);

        add(l1);add(l2);add(cb1);add(cb2);add(cb3);add(cb4);add(roomtf);add(place_order);add(quant);add(btf1);add(btf2);add(btf3);add(btf4); 

        setSize(1000,500);  
        setLayout(null);  
        setVisible(true);  

        setDefaultCloseOperation(EXIT_ON_CLOSE);  
       
    } 
   
    public void actionPerformed(ActionEvent e){  
        float amount=0; 

        String room = roomtf.getText();
        String amt1 = btf1.getText();
        String amt2 = btf2.getText();
        String amt3 = btf3.getText();
        String amt4 = btf4.getText();  

        String send_msg = ""; //string message that will be sent to the server side 
        String msg="-----"+ room +"-----\n\n"; 
        String customer_name = room;

        if(cb1.isSelected()){  
            send_msg+=amt1+",";
            amount+=10*Integer.parseInt(amt1);  
            msg+="Tea: @10 X "+amt1+ "\n";  
        } 
        else
            send_msg+="0,"; 

        if(cb2.isSelected()){  
            send_msg+=amt2+",";
            amount+=15*Integer.parseInt(amt2);  
            msg+="Coffee: @15 X "+amt2 + "\n";  
        }  
        else
            send_msg+="0,";

        if(cb3.isSelected()){  
            send_msg+=amt3+",";
            amount+=17*Integer.parseInt(amt3); 
            msg+="Cookie: @17 X "+amt3 + "\n";  
        } 
        else
            send_msg+="0,"; 

        if(cb4.isSelected()){  
            send_msg+=amt4+",";
            amount+=30*Integer.parseInt(amt4);  
            msg+="Snacks : @30 X "+amt4+ "\n";  
        } 
        else
            send_msg+="0,"; 

        send_msg+=customer_name;     

        try
        {    
            // getting localhost ip 
            InetAddress ip = InetAddress.getByName("localhost"); 
            Socket s = new Socket(ip, 5000); 

            DataInputStream dis = new DataInputStream(s.getInputStream()); 
            DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
      
            String cookie_snacks = dis.readUTF(); 
            String[] value = cookie_snacks.split(",");
            c_count = Integer.parseInt(value[0]);
            s_count = Integer.parseInt(value[1]);
           
            String tosend = send_msg; 
            dos.writeUTF(tosend); 
            //System.out.println(tosend);
            
            dis.close(); 
            dos.close(); 
        }
        catch(Exception g){ 
            g.printStackTrace(); 
        } 

        int flag = 0;
        String blank = "";
        int c1,s1;

        if(amt3.equals(blank))
            c1 = 0;
        else
            c1 = Integer.parseInt(amt3);


        if(amt4.equals(blank))
            s1 = 0;
        else
            s1 = Integer.parseInt(amt4);


        if(c_count < c1 && s_count >= s1){
            JOptionPane.showMessageDialog(this,"Cannot place your order:\n  Cookies out of stock!");
            flag = 1;
        }
        else if(c_count >= c1 && s_count < s1){
            JOptionPane.showMessageDialog(this,"Cannot place your order:\n  Snacks out of stock!");
            flag = 1;
        }
        else if(c_count < c1 && s_count < s1){
            JOptionPane.showMessageDialog(this,"Cannot place your order:\n  Cookies out of order!\n  Snacks out of stock!");
            flag = 1;
        }


        if(flag==0){
            msg+="------------\n";  
            int mins = 3;
            Calendar now = Calendar.getInstance();
            String items[] = send_msg.split(",");
            now.add(Calendar.MINUTE, 2 + Integer.parseInt(items[0]) + Integer.parseInt(items[1]));
            String the_time = now.get(Calendar.HOUR_OF_DAY) + ":"+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);
            msg+="Estimated time of delivery = " + the_time + " \n";
            JOptionPane.showMessageDialog(this,msg+"Total: "+amount);  
        }

        System.out.println(send_msg);
    } 

    public static void main(String[] args) {  
       
        //I have created two threads for clients as i will be testing the server from the same machine(my computer) 
        // i.e I have simulated two clients 
        Client c1 = new Client("1stClient");
        c1.start();

        System.out.println(c1.threadName + " started");
        Client c2 = new Client("2ndClient");

        try{
            Thread.sleep(100);
        }
        catch(Exception e){
            System.out.println("Problem here");
        }
        c2.start();
        System.out.println(c2.threadName + " started");
    }  
}       

class Client extends Thread{

    String threadName;
    Client(String name){
        this.threadName = name;
    }

    public void run(){
        ClientSide fe1 =  new ClientSide();
        JLabel j1 =new JLabel(this.threadName);
        j1.setBounds(50,70,300,20);
        fe1.add(j1);      
    }
}
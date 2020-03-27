import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Scanner;

public class Room_Rent
{
    static LocalDate ldt=LocalDate.now();
    static String t_date=ldt.getDayOfMonth()+"-"+ldt.getMonth()+"-"+ldt.getYear()+" ("+ldt.getDayOfWeek()+")";
    static Scanner in=new Scanner(System.in);

    static boolean databse=false;
    static boolean recipt=false;
    static boolean ins=false;
    static int tem;
    static String name;
    static int tid;
    static int room_no;
    static int p_unit;
    static int l_unit;
    static int total_unit;
    static int amount;
    static int rent=4200;
    static int total;
    public static void start()
    {
        System.out.println("--------------RMS--------------");
        System.out.println("01: Enter rent");
        System.out.println("02: Get Database");
        System.out.println("03: Admin");
        System.out.println("04: Get Recipt");
        System.out.println("ENTER INDEX TO Select OPERATION");
        boolean run=true;
        System.out.println("to exit ENTER index 999: ");
        while (run)
        {   System.out.println("Enter choice !");
            int choice= in.nextInt();
            if (choice==999)
            {
                run=false;
                System.out.println("Exiting & closing db connection...");
                break;
            }
            else if(choice==1)
            {
                String name_room = null;

                System.out.println("Enter Data");
                System.out.println("Room no: ");
                room_no= in.nextInt();
                int lstdif=0;

                try{
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    Connection con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","1234asdf");
                    Statement stmt=con.createStatement();
                    ResultSet a3=stmt.executeQuery("select name from rent_dir where room="+room_no);
                    while(a3.next())
                        name_room=a3.getString(1);

                    ResultSet a1=stmt.executeQuery("select max(p_unit) from rent where room_no="+room_no);
                    while(a1.next())
                        l_unit=a1.getInt(1);

                    ResultSet a2=stmt.executeQuery("select total_unit from rent where rownum <2 and room_no="+room_no+"order by tid desc");
                    while(a2.next())
                        lstdif=a2.getInt(1);
                    con.close();
                }catch(Exception e){ System.out.println("Error code:"+e);}

                System.out.println("Name: "+name_room);        //automatically get name from database
                System.out.println("Last Unit: "+l_unit);          ///automaticallt used from databses
                System.out.println("Present Unit: ");
                p_unit= in.nextInt();
                total_unit=p_unit-l_unit;
                amount=total_unit*6+150;
                total=amount+rent;
                System.out.println("Total rent: "+total);
                if(lstdif<total_unit)
                {
                    int per;
                    per=((total_unit-lstdif)*100)/lstdif;
                    System.out.println("From last month Electricity consumption: ");
                    System.out.println("INCREASED by "+(total_unit-lstdif)+" units  "+per+" %");
                }
                else
                {
                    int per;
                    per=((lstdif-total_unit)*100)/total_unit;
                    System.out.println("From last month Electricity consumption: ");
                    System.out.println("DECREASED by "+(lstdif-total_unit)+" units  "+per+" %");
                }
                ins=true;
            }
            else if(choice==2){databse=true;}
            else if(choice==4)
            {
                System.out.println("Enter Room no: ");
                tem=in.nextInt();
                recipt=true;
                break;
            }

        }

    }

    public void data()
    {
    ////////////////////
    }

    public static void main(String ...dd)
    {
        System.out.println(t_date);
        start();

        /////////////////////////////////////////////////////////////
        //DATABASE SAVE DATA
        try{
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con= DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","1234asdf");
            Statement stmt=con.createStatement();
            ResultSet a1=stmt.executeQuery("SELECT max(tid) FROM rent");
            while(a1.next())
                tid=a1.getInt(1)+1;
            if(recipt)
            {
                ResultSet a3=stmt.executeQuery("select name from rent_dir where room="+tem);
                while(a3.next())
                    name=a3.getString(1);
                ResultSet a4=stmt.executeQuery("select * from rent where room_no="+tem);
                while (a4.next())
                {
                    System.out.println(":::::::RECIPT::::::");
                    System.out.println("Name: "+name);
                    System.out.println("Date: "+a4.getString(3));
                    System.out.println("Present Unit: "+a4.getInt(4));
                    System.out.println("Last Unit: "+a4.getInt(5));
                    System.out.println("Total Unit: "+a4.getInt(6));
                    System.out.println("Amount: "+a4.getInt(7));
                    System.out.println("Rent: "+a4.getInt(8));
                    System.out.println("Total Bill: "+a4.getInt(9));
                    System.out.println("Next Due: "+ldt.plusMonths(1));
                    System.out.println("---------------------");
                }
            }
            else if(ins)
            {
                String exe="INSERT into rent(tid,room_no, t_date,p_unit, l_unit,total_unit,amount,rent, total) values("+tid+", "+room_no+ ", '"+t_date+"' ,"+p_unit+","+l_unit+","+total_unit+","+amount+","+rent+","+total+")";
                ResultSet rs=stmt.executeQuery(exe);
                System.out.println("Data successfully SAVED !");
            }
            System.out.println("----------------------");

            ResultSet a2=stmt.executeQuery("select * from rent order by tid");
            while(a2.next() && databse)
                System.out.println("TID: "+a2.getInt(1)+" Room:"+a2.getInt(2)+"  Date: "+a2.getString(3)+" Present Unit: "+a2.getInt(4)+" Last Unit: "+a2.getInt(5)+" Total Unit: "+a2.getInt(6)+" Amount: "+a2.getInt(7)+" Rent: "+a2.getInt(8)+" Total: "+a2.getInt(9));
            con.close();
        }catch(Exception e){ System.out.println("Error code:"+e);}
    }
}

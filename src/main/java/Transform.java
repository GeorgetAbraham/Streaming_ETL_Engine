import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.net.*;
import java.util.TimerTask;
import java.util.Timer;
import java.util.HashMap;

public class Transform{
    public String select_csvdata(){
        String command="select Data from source_data_dump where Type=\"csv\"";
        return command;
    }

    public static Connection get_dbconnect(String username,String passwrd) throws SQLException {
        Connection connect=DriverManager.getConnection("jdbc:mysql://localhost:3306/testDB",username,passwrd);
        return connect;
    }

    public static void print_csvfile(ArrayList<HashMap> csv_file){
        int n=csv_file.size();
        for(int i=0;i<n;i++){
            for(Object s: csv_file.get(i).keySet()){
                System.out.println("key value is "+s+"value: "+ csv_file.get(i).get(s));
            }
        }

    }

    public ArrayList<HashMap> get_csvhashmap(String username,String passwrd){
//        the function takes a set of results from the databse of type csv and all the results are then transformed into hashmaps at one go
//        to get only one entry change the select query in above function
        ArrayList<HashMap> csv_file=new ArrayList<>();
        try {
            Connection connect=get_dbconnect(username,passwrd);
            Statement stmt=connect.createStatement();
            Transform trans=new Transform();
            ResultSet rs=stmt.executeQuery(trans.select_csvdata());     //rs is a set of all the results obtained from query
//            csv_file has the csv rows in arraylist
            while(rs.next()){
                String txt=rs.getString("Data");
                String csv_lines[]=txt.split("\n");

                int csv_n=csv_lines.length;                             //no. of entries in the csv_file is csv_n
                if(csv_lines.length>=2){
                    String column_names[]=csv_lines[0].split(",");
                    int attr_n=column_names.length;                     //attr_n means number of attributes

                    for(int i=1;i<csv_n;i++){                           //entering every row in below loop
                        String csv_row[]=csv_lines[i].split(",");
                        HashMap <String,String> hmap =new HashMap<String,String> ();
                        for(int j=0;j<attr_n;j++) {
                            hmap.put(column_names[j], csv_row[j]);
                        }
                        csv_file.add(hmap);                             // adding each row to the csv_file arraylist

                    }

                }
            }
            return csv_file;
//            trans.select_data();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return csv_file;
    }

    public static void main(String[] args){
        Transform holder=new Transform();
        ArrayList<HashMap> csv_file= holder.get_csvhashmap("root","xxxx");
        print_csvfile(csv_file);

    }
}
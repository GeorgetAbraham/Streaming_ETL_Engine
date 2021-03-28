import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.net.*;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

public class Engine{
    public static String insert_instruction(String[] entry_details) {
//        given all the required details for column values,this function returns the insert statement to be run in the query
//        Independent of data types,url etc
        String s="INSERT INTO source_data_dump VALUES ('";
        int n=entry_details.length;
        s+=entry_details[0]+"'";
        for(int i=1;i<n;i++){
            s=s+','+"'"+entry_details[i]+"'";
        }
        s=s+")";
        return s;
    }

    public static String get_URl_jsondata(String link) throws IOException {
        URL json_data=new URL(link);
        BufferedReader in=new BufferedReader(new InputStreamReader(json_data.openStream()));
        String line_input="";
        while(true){
            String holder=in.readLine();
//            holder=holder.replace("\n","");
            if(holder==null){
                break;
            }
            line_input=line_input+holder;

        }
//REPLACE ALL \n with ""
        line_input=line_input.replace("\n","");
//        multiple line strings are allowed
//        String a="abcnd\n";
//        String b="ejndsn\n";
//        String c=a+b;
//        System.out.println(c);
        return line_input;

    }

    public static String get_URl_csvdata(String link) throws IOException {
        URL json_data=new URL(link);
        BufferedReader in=new BufferedReader(new InputStreamReader(json_data.openStream()));
        String line_input="";

//        ACTUAL CODE TO RUN TO GET COMPLETE CSV FILE
        while(true){
            String holder=in.readLine();
//            holder=holder+"\n";
//            holder=holder.replace("\n","");
            if(holder==null){
                break;
            }
            line_input=line_input+holder+"\n";
        }

        return line_input;

    }




    public static void data_dump(Element eElement){
//    need to write function for getting data from URL
        try{
            Class.forName("com.mysql.jdbc.Driver");
//            1.to connect to database
//INSTRUCTION:Database name is testDB in my case,change accordingly,similarly change username and password according to ur system
//INSTRUCTION:BEFORE THAT CREATE A DATABASE THAT IS NAMED testDB ON YOUR SYSTEM
            Connection connect=DriverManager.getConnection("jdbc:mysql://localhost:3306/testDB","root","Rahulmon1!");
            System.out.println("Successfully set up Connection to database\n");


//            2.creating a statement
            Statement stmt=connect.createStatement();

//            3.executing query
//            INSTRUCTION :IN INSERT INTO MAKE SURE U ARE ADDING TO CORRECT TABLE

            String[] entry_details=new String[5];
//            entry_details are values used to insert into the table
//             Source_Data_ID ,Type ,URL_to_Loc ,Date_Time,Data are the attribute names;
            Date date=Calendar.getInstance().getTime();
            DateFormat dtfrmt=new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            String dt_time=dtfrmt.format(date);
//            java date object needs to be instantiated for evry time ,it does not update already existing object

            entry_details[1]=eElement.getElementsByTagName("type").item(0).getTextContent();
            entry_details[2]=eElement.getElementsByTagName("URL").item(0).getTextContent();
            entry_details[3]=dt_time;
            entry_details[0]=entry_details[2]+entry_details[3];


//            trying to get data from
//            entry_details[4]="DUMMY FOR NOW LATER ADD DATA FROM CURL COMMAND";
            if(entry_details[1].equals("csv")){
                entry_details[4]=get_URl_csvdata(entry_details[2]);
            }
            else if(entry_details[1].equals("json")){
                entry_details[4]=get_URl_jsondata(entry_details[2]);
            }

            System.out.println("The data downloaded from URL is the following:\n"+entry_details[4]);
            String instrctn=insert_instruction(entry_details);
            System.out.println(instrctn);
            stmt.executeUpdate(instrctn);

            connect.close();
        }catch(Exception e){ System.out.println(e);}
    }



    public static void main(String args[]) throws ParserConfigurationException, SAXException, IOException
    {

        try{
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
//          INSTRUCTION:  ADD THE APPROPRIATE FILE PATH FOR THE XML FILE
            Document document=builder.parse(new File("./src/settings.xml"));

            NodeList nodes_list = document.getElementsByTagName("source");
            // source is the name of the element
            System.out.println("The total number of data sources is: "+nodes_list.getLength());

            for(int i=0;i<nodes_list.getLength();i++){
                Node nod = nodes_list.item(i);
                if (nod.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) nod;
                    System.out.println("Data Format: "+ eElement.getElementsByTagName("type").item(0).getTextContent());
                    System.out.println("URL source: "+ eElement.getElementsByTagName("URL").item(0).getTextContent());
//                    INSTRUCTION:GETtEXTCONTECT GIVES A sTRING
//                    trying to call data_dump
//                    THIS function runs the data dump
                    data_dump(eElement);
                }

            }
        }
        catch(Exception e){
            System.out.println(e);
        }


    }
}

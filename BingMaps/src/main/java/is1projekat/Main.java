/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package is1projekat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author neman
 */
public class Main {
    
    public static final String part1 = "http://dev.virtualearth.net/REST/V1/Routes/Driving?o=xml&wp.0=";
    public static final String part2 = "&wp.1=";
    public static final String part3 = "&ra=routeSummariesOnly&key=key goes here";
    
    public static void main(String[] args) {
        try
        {
            ServerSocket serverSocket = new ServerSocket(5001);
            while(true)
            {
                try 
                {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String loc1 = in.readLine();
                    String loc2 = in.readLine();
                    
                    loc1 = loc1.replace(" ", "%20");
                    loc2 = loc2.replace(" ", "%20");
                    String request = part1+loc1+part2+loc2+part3;
                    HttpResponse response = Request.Get(request).execute().returnResponse();
                    HttpEntity r_entity = response.getEntity();
                    String xmlString = EntityUtils.toString(r_entity);
                    xmlString = xmlString.substring(1);
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    InputSource is = new InputSource(new StringReader(xmlString));
                    Document parse = builder.parse(is);
                    String answer = parse.getElementsByTagName("TravelDuration").item(0).getTextContent();
                    
                    PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(),true);
                    pout.println(answer);
                    clientSocket.close();
                } 
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

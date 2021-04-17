/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import youtubeSearch.Search;

/**
 *
 * @author neman
 */
public class Main {
    
    static Search youtube;
    
    public static void main(String[] args) {
        try
        {
            youtube = new Search();
            ServerSocket serverSocket = null;
            try
            {
                serverSocket = new ServerSocket(5000);
                while(true)
                {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String songName = in.readLine();
                    PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(),true);
                    pout.println(youtube.Get(songName));
                    clientSocket.close();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            if(serverSocket != null)
                serverSocket.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}

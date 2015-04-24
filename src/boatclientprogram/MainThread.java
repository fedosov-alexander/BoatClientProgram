/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package boatclientprogram;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexander
 */
public class MainThread extends Thread {
    private MainFrame mMainFrame;
    private Socket mSocket;
    private DataInputStream mDataIn;
    private DataOutputStream mDataOut;
    private byte [] mMap;
    private int mMapWidth;
    private int mMapHeight;
    private int [] mPreviousXCoords = null;
    private int [] mPreviousYCoords= null;
    private int [] mCurrentXCoords= null;
    private int [] mCurrentYCoords= null;
    public MainThread(MainFrame mainFrame, Socket sock){
        mMainFrame = mainFrame;
        mSocket = sock;
        try { 
            mDataIn=new DataInputStream(mSocket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {        
            mDataOut=new DataOutputStream(mSocket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void readSocket(){
        byte command;
        try {
            command = mDataIn.readByte();
            switch(command){
                case((byte)255):{
                    readMap();
                    drawMap();
                break;
                }
                case((byte)128):{
                    clearMap();
                    readCoordinates();
                break;}
            }
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void readMap(){
        try {
            mMapWidth = mDataIn.readInt();
            mMapHeight = mDataIn.readInt();
            mMap = new byte[mMapHeight*mMapWidth];
            mDataIn.readFully(mMap, 0, mMapHeight*mMapWidth);
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void drawMap(){
        
    }
    public void clearMap(){
        if((mPreviousXCoords!=null)&&(mPreviousYCoords!=null)){
            for(int i = 0; i < mPreviousXCoords.length; i++){
                mMap[mPreviousYCoords[i]*mMapWidth+mPreviousXCoords[i]] = 0;
            }
        }
    }

    public MainThread() {
    }
    public void readCoordinates(){
       int length;
       int x,y,counter;
       counter = 0;
        try {
            length = mDataIn.readInt();
            mCurrentXCoords = new int[length-2];
            mCurrentYCoords = new int[length-2];
            for(int i = 0; i < length-2; i+=2){
                x = mDataIn.readInt();
                y = mDataIn.readInt();
                mMap[y*mMapWidth+x] = (byte)128;
                mCurrentXCoords[counter] = x;
                mCurrentYCoords[counter] = y;
                counter++;
                
             }
                x = mDataIn.readInt();
                y = mDataIn.readInt();
                mMap[y*mMapWidth+x] = (byte)130;
                mPreviousXCoords = mCurrentXCoords;
                mPreviousYCoords = mCurrentYCoords;
        } catch (IOException ex) {
            Logger.getLogger(MainThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

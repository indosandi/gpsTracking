package org.kmbtech.tri.server.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class AcceptInfo extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6481543524730842912L;
	private HashMap<String,ObjInfo> objHash;
	private final static String FROM_CLIENT="1";
	private static long TIME_CLEAN_INTERVAL=1000;
	private static long TIME_EXPIRED=1000;
	
	private static String clientParameter="client";
	private static String idParameter="id";
	private static String latitudeParameter="latitude";
	private static String longitudeParameter="longitude";
	private static String descriptionParameter="description";
//	private static String timeStampParameter="timestamp";
	
	private Timestamp timeServer;
	private Timer timer;
	final static Logger logger = Logger.getLogger(AcceptInfo.class);



	public void init() throws ServletException{
	      // Do required initialization
		objHash= new HashMap<String,ObjInfo>();
		TIME_CLEAN_INTERVAL=1000*30*1; // 3 minutes
		TIME_EXPIRED=1000*1*50;
		timeServer=dateNow(); //set date as now
		
		 //create new timer and schedule a task
		timer=new Timer();
		timer.scheduleAtFixedRate(new cleanDataPeriodic(),
				TIME_CLEAN_INTERVAL,TIME_CLEAN_INTERVAL);
		
	}
	
	// For periodic check 
	// if found old object then delete
	class cleanDataPeriodic extends TimerTask{

		@Override
		public void run() {
//			System.out.println("running periodic");
			//logger.info("periodic");
			
			// TODO Auto-generated method stub
			Iterator it=objHash.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry itHash=(Map.Entry)it.next();
				ObjInfo itObj=(ObjInfo)itHash.getValue();
				Timestamp oldObj=itObj.getTimeStamp();
				if(dateNow().getTime()-oldObj.getTime()>TIME_EXPIRED){
					logger.info(itObj.getDescription()+" is deleted"+(dateNow().getTime()-oldObj.getTime())+"");
			    	//System.out.println(itObj.getDescription()+" object need to be deleted");
			    	//System.out.println(dateNow().getTime()-oldObj.getTime());
			    	it.remove();
			    }			 
			}		
			logger.info("Object= "+objHash.size());
		}		
	}
	
	public void doPost(HttpServletRequest request,
             HttpServletResponse response)
     throws ServletException, IOException{
		
		PrintWriter out = response.getWriter();
		//determine either client or broadcaster
		if (request.getParameter(clientParameter).equals(FROM_CLIENT) 
				&&request.getParameter(clientParameter)!=null){
			out.print(dispatchClient()); //client request
		}
		else{ //broadcaster request
			String broadResponse=dispatchBroadcast(request);
			out.print(broadResponse);
			
		}

	}
	
	public void doGet(HttpServletRequest request,
            HttpServletResponse response)
           throws ServletException, IOException {
	}
	private String dispatchClient(){
		String response="";
		for (ObjInfo value : objHash.values()) {
		    response+=value.getLatitude()+";"+value.getLongitude()+";"
		    	+value.getDescription()+";";
		}

		return response;
	}
	private String dispatchBroadcast(HttpServletRequest request){
		String tempId=request.getParameter(idParameter);
		String tempLatitude=request.getParameter(latitudeParameter);
		String tempLongitude=request.getParameter(longitudeParameter);
		String tempDescription=request.getParameter(descriptionParameter);
		String outResponse="";
		if (tempId!=null && tempLatitude!=null && tempLongitude!=null){
			// send broadcast
			if(objHash.containsKey(tempId)){
				//add object if didn't exist before
				ObjInfo tempObjHash=objHash.get(tempId);
				tempObjHash.setDescription(tempDescription);
				tempObjHash.setLatitude(tempLatitude);
				tempObjHash.setLongitude(tempLongitude);
				tempObjHash.setTimeStamp(dateNow());
				outResponse=tempId+";"+tempLatitude+";"+
						tempLongitude+";"+tempObjHash.getTimeStamp().getTime()+"";
				//out.print(outResponse);
			}
			else{
				//update position of old object
				ObjInfo tempObj=new ObjInfo(tempLatitude, tempLongitude
						,dateNow(), tempDescription);
				objHash.put(tempId, tempObj);
				
				outResponse=tempId+";"+tempLatitude+";"+
						tempLongitude+";"+tempObj.getTimeStamp().getTime()+"";
				//out.print(outResponse);
			}
		}
		else{
			// do nothing on hashmap object
		}
		return outResponse;
	}
	private Timestamp dateNow(){
		java.util.Date date= new java.util.Date();
		return new Timestamp(date.getTime());
	}
}

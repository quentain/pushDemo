package ser.ds.util;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import android.os.Environment;

public class WriteServiceLog {	
	//������Ŀ¼���ļ����ļ�����
	public static void write( String content)  
	    {  
	        try  
	        {  
	            
	            if(Environment.getExternalStorageState()  
	                    .equals(Environment.MEDIA_MOUNTED))  
	            {  
	                File sdCardDir = Environment.getExternalStorageDirectory();  
	                File file = new File(sdCardDir+"/DefenseService/servicelog");
	                file.mkdirs();
	                String directory="DefenseService/servicelog";
	                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");    
	            	String date=sdf.format(new java.util.Date());  
	            	String fileName=date+".txt";
	                String directory_full_path = sdCardDir.getCanonicalPath()+"/"+directory;
	                String file_full_path = sdCardDir.getCanonicalPath()+"/"+directory+"/"+fileName;
	                SimpleDateFormat sdftime=new SimpleDateFormat("HH:mm:ss ");    
	            	String time=sdftime.format(new java.util.Date()); 
	            	String writeContent=time+content+"\r\n";
	            	
	                createDir(directory_full_path);
	                createFile(directory_full_path, fileName);
	                
	                File targetFile = new File(file_full_path);  
	                
	                System.out.println(file_full_path);
	                
	                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");  
	                raf.seek(targetFile.length());  
	                raf.write(writeContent.getBytes());  
	                raf.close();       
	            }  
	        }  
	        catch(Exception e)  
	        {  
	            e.printStackTrace();  
	        }  
	    }    
	    public static void createFile(String path,String filename) throws IOException
	    {
	        File file=new File(path+"/"+filename);
	        if(!file.exists())
	            file.createNewFile();
	    }
	    
	    public static void createDir(String path){
	        File dir=new File(path);
	        if(!dir.exists())
	            dir.mkdir();
	    }  
	    
	    
}

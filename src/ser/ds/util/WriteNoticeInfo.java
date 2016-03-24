package ser.ds.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;

import android.os.Environment;

public class WriteNoticeInfo {	
	private static String lineTxt;
	//传参数目录，文件名，文件内容
	public static void write( String content)  
	    {  
	        try  
	        {  
	            //如果手机插入了SD卡,而且应用程序具有访问SD的权限
	        	//Environment.getExternalStorageDirectory()getRootDirectory()//获取手机根目录
	            if(Environment.getExternalStorageState()  
	                    .equals(Environment.MEDIA_MOUNTED))  
	            {  
	                //获取SD卡根目录  
	                File sdCardDir = Environment.getExternalStorageDirectory();  
	                File file = new File(sdCardDir+"/DefenseService/noticeinfo");
	                file.mkdirs();
	                String directory="DefenseService/noticeinfo";
//	                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");    
//	            	String date=sdf.format(new java.util.Date());  
	            	String fileName="noticeinfo.txt";
	                String directory_full_path = sdCardDir.getCanonicalPath()+"/"+directory;
	                String file_full_path = sdCardDir.getCanonicalPath()+"/"+directory+"/"+fileName;
	                
	            	String writeContent=content+"\r\n";
	            	
	                createDir(directory_full_path);
	                createFile(directory_full_path, fileName);
	                
	                File targetFile = new File(file_full_path);  
	                //以指定文件创建RandomAccessFile对象  
	                RandomAccessFile raf = new RandomAccessFile(targetFile, "rw");  
	                //将文件记录指针移动到最后  
	                raf.seek(targetFile.length());  
	                //输出文件内容  
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
	    public static String readTxtFile(File fileName)throws Exception{  
	    	  String result=null;  
	    	  FileReader fileReader=null;  
	    	  BufferedReader bufferedReader=null;  
	    	  try{  
	    	   fileReader=new FileReader(fileName);  
	    	   bufferedReader=new BufferedReader(fileReader);  
	    	   try{  
	    	    String read=null;  
	    	    while((read=bufferedReader.readLine())!=null){  
	    	     result=result+read+"\r\n";  
	    	    }  
	    	   }catch(Exception e){  
	    	    e.printStackTrace();  
	    	   }  
	    	  }catch(Exception e){  
	    	   e.printStackTrace();  
	    	  }finally{  
	    	   if(bufferedReader!=null){  
	    	    bufferedReader.close();  
	    	   }  
	    	   if(fileReader!=null){  
	    	    fileReader.close();  
	    	   }  
	    	  }  
	    	  System.out.println("读取出来的文件内容是："+"\r\n"+result);  
	    	  return result;  
	    	 }  
}

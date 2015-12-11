package com.grep.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionGrabber {

	private BufferedReader reader;
	private String basePath;
	private File outFile;
	private PrintWriter writer;
	private String appType;
	private Map<String,String> expressionMap; 
	private Map<String,String> categoryMap;
	private Map<String,String> descriptionMap;
	private List<String> excludeFolderList;
	
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public boolean openWriter(){
		try {
			writer=new PrintWriter(outFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean closeWriter(){
		writer.close();
		return true;
	}
		
	public String getBasePath() {
		return basePath;
	}



	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}	
	
	public boolean setPathName(){
		
		reader=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the path:");
		
		try {
			basePath=reader.readLine();
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		//fileOperation();		
		return true;
	}
	
	public void excludeFolder(){
		Properties prop=new Properties();
		
		try {
			prop.load(ExpressionGrabber.class.getClassLoader().getResourceAsStream("ExcludeFolders.properties"));
			
			String buffer=prop.getProperty("ExcludeFolder");
			String excludeFolderArray[]=new String[buffer.split("\t").length];
			excludeFolderArray=buffer.split("\t");
			excludeFolderList=new ArrayList<String>();
			
			for(int i=0;i<excludeFolderArray.length;i++){
				System.out.println("exclude:"+excludeFolderArray[i]);
				excludeFolderList.add(excludeFolderArray[i]);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public boolean isExcluded(String folderName){
		System.out.println("in isExcluded"+excludeFolderList.contains(folderName));
		System.out.println(excludeFolderList);
		return excludeFolderList.contains(folderName);
	}
		
	public boolean fileOperation(File baseFile){
		String fileNameList[]=baseFile.list();
		System.out.println("Naaaaame--->"+baseFile.getAbsolutePath()+"  list:"+fileNameList);
		List<String> fileList=new ArrayList<String>();
		List<String> directoryList=new ArrayList<String>();
		
		for(int i=0;i<fileNameList.length;i++){
			if(fileNameList[i].contains(".")){
				fileList.add(fileNameList[i]);
			}else{
				File fileTest=new File(baseFile.getAbsoluteFile()+"\\"+fileNameList[i]);
				
				if(!fileTest.isFile()){
					directoryList.add(fileNameList[i]);
				}
				
			}
		}
		
		
		Iterator itr=fileList.iterator();
		
		while(itr.hasNext()){
			System.out.println("File name:"+itr.next());
		}
		
		itr=directoryList.iterator();
		
		while(itr.hasNext()){
			System.out.println("Directory name:"+itr.next());
		}
		
		//pathName=baseFile.getAbsolutePath();
		//System.out.println("Path:"+pathName);
		
		if(fileList.size()!=0)
			searchRegex(outFile, fileList,baseFile.getAbsolutePath());
		
		Iterator<String> itr1=directoryList.iterator();
		
		System.out.println("Before----"+directoryList+"itr1.hasNext"+itr1.hasNext());
		while(itr1.hasNext()){
			String directoryName=itr1.next();
			System.out.println("IS excluded:"+isExcluded(directoryName));
			
			if(!isExcluded(directoryName)){
				System.out.println("Directory name:"+directoryName+" calling");
				fileOperation(new File(baseFile.getAbsolutePath()+"\\"+directoryName));
			}
		}
		
		return true;
	}
	

	public void iterateFile(){
		
		outFile=new File("OutXSS.txt");
		
		if(outFile.exists()){
			outFile.delete();
		}else{
			try {
				outFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
	}
	
	public void storeRegex(){
		Properties prop = new Properties();
		String inputPropString;
		expressionMap=new TreeMap<String,String>();
		descriptionMap=new TreeMap<String,String>();
		categoryMap=new TreeMap<String,String>();
		String breakString[]=new String[4];
		String langType;
		
		
		
    	try {
    		prop.load(ExpressionGrabber.class.getClassLoader().getResourceAsStream("AppType.properties"));
    		appType=prop.getProperty("Type");
    		//load a properties file from class path, inside static method
    		prop.load(ExpressionGrabber.class.getClassLoader().getResourceAsStream("Expression.properties"));
    	
    		int i=101;	
    		int j=101;
    		
    		while(true){
    			inputPropString=prop.getProperty(""+i);
    			if(inputPropString==null)
    				break;
    			
    			breakString=inputPropString.split("\t");
    			
    			langType=breakString[3];
    			
    			if(appType.equals("JAVA")){
    				if(langType.equals("JSP")||langType.equals("Javascript")){
    					expressionMap.put(""+j, breakString[0]);
    					descriptionMap.put(""+j,breakString[1]);
    					categoryMap.put(""+j, breakString[2]);
    					j++;
    				}
    				
    			}else if(appType.equals("PHP")){
    				if(langType.equals("Javascript")){
    					expressionMap.put(""+j, breakString[0]);
    					descriptionMap.put(""+j,breakString[1]);
    					categoryMap.put(""+j, breakString[2]);
    					j++;
    				}
    			}else if(appType.equals("ObjectiveC")){
    				if(langType.equals("ObjectiveC")){
    					expressionMap.put(""+j, breakString[0]);
    					descriptionMap.put(""+j,breakString[1]);
    					categoryMap.put(""+j, breakString[2]);
    					j++;
    				}
    			}
    			i++;
    			
    		}
         
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }

	}

	
	
	public boolean searchRegex(File outputFile,List<String> fileNameList,String pathName){
		
		System.out.println("In searchRegex");
		Iterator<String> itr=fileNameList.iterator();
		String messageString="";
		int j=100;
		ArrayList<String> regex=new ArrayList<String>();
		boolean matchFlag;
		
		Pattern pattern[]=new Pattern[200];
		Matcher matcher[]=new Matcher[200];
		
		for(int i=0;i<expressionMap.size();i++){
			System.out.println("In map "+i);
			pattern[i]=Pattern.compile(expressionMap.get(""+(++j)));
			System.out.println();
		}
		
		while(itr.hasNext()){
			System.out.println("In op");
			String fileType=itr.next();
			
			if(isFileLanguageType(fileType)){
				File inFile=new File(pathName+"\\"+fileType);
				
				try {
					reader=new BufferedReader(new FileReader(inFile));
					String line;
					int i=0;
																	
					while((line=reader.readLine())!=null){
						
						i++;
						for(int j1=0;j1<expressionMap.size();j1++){
							matcher[j1]=pattern[j1].matcher(line);
						}
										
						for(int k=0;k<expressionMap.size();k++){
							
							matchFlag=matcher[k].find();
							
							if(matchFlag){
								
								String mapKey="";
								for(int cnt=101;cnt<500;cnt++){
									if(expressionMap.get(""+cnt).equals(matcher[k].pattern().pattern())){
										mapKey=""+cnt;
										break;
									}
								}
								
								writer.flush();
		            			writer.println();
		            			writer.println();
		            			messageString=line.trim();
		            			
		            			if(messageString.length()>100){
		            				messageString="Line is too long, please check the line number";
		            			}
		            			System.out.println("culprit "+matcher[k].pattern().pattern());
		            			writer.write(inFile.getAbsolutePath()+" Line no:"+i+" category:"+categoryMap.get(mapKey)+" description:"+descriptionMap.get(mapKey)+" code:"+messageString);
		            			
		            			writer.println();
							}
							
						}
								
					}
					
					reader.close();
									
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}

				
		return true;
	}
	
	public boolean isFileLanguageType(String fileType){
		if(appType.equals("JAVA")){
			if(fileType.endsWith(".js")||fileType.endsWith(".html")||fileType.endsWith(".htm")||fileType.endsWith(".jsp")||fileType.endsWith(".jspf")){
				return true;
			}
		}else if(appType.equals("ObjectiveC")){
			if(fileType.endsWith(".m")){
				return true;
			}
		}else if(appType.equals("PHP")){
			if(fileType.endsWith(".js")||fileType.endsWith(".html")||fileType.endsWith(".htm")||fileType.endsWith(".php")){
				return true;
			}
		}
		return false;
	}


}

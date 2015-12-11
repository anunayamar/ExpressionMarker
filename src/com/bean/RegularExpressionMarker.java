package com.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.grep.service.ExpressionGrabber;

public class RegularExpressionMarker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("\n\n\t\tWelcome to Regular Expression Marker");
		
		ExpressionGrabber expGrabber=new ExpressionGrabber();
			
		expGrabber.setPathName();
		expGrabber.excludeFolder();
		expGrabber.storeRegex();
		expGrabber.iterateFile();
		expGrabber.openWriter();
		expGrabber.fileOperation(new File(expGrabber.getBasePath()));
		expGrabber.closeWriter();
									
		
	}

}

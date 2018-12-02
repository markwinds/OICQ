package org.fitzeng.main;

import javax.lang.model.element.Parameterizable;

public class LogManager {
	public final static int OUTPUTNOTHING=0;
	public final static int OUTPUTIMPMSG=1;
	public final static int OUTPUTMSG=2;
	public final static int OUTPUTDEBUG=3;
	
	private static int status;

	
	public static int getStatus() {
		return status;
	}
	
	
	public static void setStatus(int i) {
		status=i;
	}
}

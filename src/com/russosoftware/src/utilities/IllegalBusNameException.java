package com.russosoftware.src.utilities;

public class IllegalBusNameException extends Exception 
{
	private final String exceptionMessage;
	
	public IllegalBusNameException(String string) 
	{
		this.exceptionMessage = string;
	}
	
	public String toString()
	{
		return this.exceptionMessage;
	}

	private static final long serialVersionUID = 2386306755008297942L;
}

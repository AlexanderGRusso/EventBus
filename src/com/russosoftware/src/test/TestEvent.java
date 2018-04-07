package com.russosoftware.src.test;

import com.russosoftware.src.utilities.IEvent;

class TestEvent implements IEvent
{
	@Override
	public String[] getEventListenerMethods() 
	{
		return new String[]{"Test Worked!"};
	}	
}

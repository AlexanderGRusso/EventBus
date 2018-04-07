package com.russosoftware.src.test;

import com.russosoftware.src.utilities.EventBus;
import com.russosoftware.src.utilities.IEventListener;
import com.russosoftware.src.utilities.IllegalBusNameException;

public class TestClass 
{
	public static void main(String[] args)
	{
		try 
		{
			EventBus bus = new EventBus("Test Bus");
			bus.registerEventBusListener(new IEventListener()
			{
				public void invokedMethod(TestEvent event)
				{
					System.out.println(event.getEventListenerMethods()[0]);
				}
			});
			
			bus.post(new TestEvent());
		} catch (IllegalBusNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

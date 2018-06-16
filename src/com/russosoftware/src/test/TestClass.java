package com.russosoftware.src.test;

import com.russosoftware.src.utilities.EventBus;
import com.russosoftware.src.utilities.EventHandler;
import com.russosoftware.src.utilities.EventHandler.EventPriority;

public class TestClass 
{
	public static void main(String[] args)
	{
		EventBus bus = EventBus.instance();
		
		bus.registerEventBusListener(new Object()
		{
			@EventHandler(priority = EventPriority.LOW)
			public void testMethod(TestEvent event)
			{
				System.out.println("Second listener on the event bus is being posted to!");
			}
			
			@EventHandler(priority = EventPriority.HIGH)
			public void lowerPriorityListener(TestEvent event)
			{
				System.out.println("This is also invoking. Seeing if utilizes priority.");
			}
		});
		
		bus.registerEventBusListener(new Object()
		{
			@EventHandler
			public void invokedMethod(TestEvent event)
			{
				System.out.println(event);
			}
		});
		
		bus.post(new TestEvent());
	}
}
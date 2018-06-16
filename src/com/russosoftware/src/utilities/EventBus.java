package com.russosoftware.src.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.russosoftware.src.utilities.EventHandler.EventPriority;

/**
 * Distributes events to register Event Listeners
 **/
public class EventBus
{
	/**
	 * HashMap constructed to contain the event busses registered to post the event and distribute it to the event listeners.
	 **/
	private static Map<String, EventBus> busMap = new HashMap<>();
	
	/**
	 * Default EventBus
	 **/
	private static EventBus INSTANCE;
	
	static
	{
		try 
		{
			if(busMap == null) busMap = new HashMap<>();		/** In the event that the bus map has not been constructed, construct it to avoid a NullPointerException **/

			INSTANCE = new EventBus();
		} 
		catch (IllegalBusNameException e) 
		{
			/**
			 * Dummy catch block. Default bus will never be constructed outside of the class before this bus.
			 **/
			e.printStackTrace();
		}
	}
	
	/**
	 * HashMap constructed to contain the event listeners registered to receive the event bus posting.
	 **/
	private HashMap<Class<?>, HashMap<EventPriority, List<EventListenerPair>>> eventMap = new HashMap<>();
	private final String busName;
	
	private EventBus() throws IllegalBusNameException
	{
		this("Default Bus");
	}
	
	public EventBus(String busName) throws IllegalBusNameException
	{	
		if(eventMap == null) eventMap = new HashMap<>();	/** In the event that the event map has not been constructed, construct it to avoid a NullPointerException **/
		
		if(!busMap.isEmpty() && busMap.containsKey(busName))
				throw new IllegalBusNameException("The name utilized for this bus has already been used. Please choose another.");
		
		this.busName = busName;
		
		EventBus.busMap.put(busName, this);
	}
	
	public void registerEventBusListener(Object eventListener)
	{
		for(Method m : eventListener.getClass().getDeclaredMethods())
		{
			if(m.isAnnotationPresent(EventHandler.class) && m.getParameterTypes().length == 1)
			{
				EventHandler eventData = m.getAnnotation(EventHandler.class);
				Class<?> eventRegistering = (Class<?>) m.getParameterTypes()[0];
				
				addListener(eventData.priority(), eventRegistering, eventListener, m);
			}
		}
	}
	
	private void addListener(EventPriority priority, Class<?> eventRegistering, Object eventListener, Method m)
	{
		/**
		* If the event we're registering a listener has not had any other listeners registered, construct a HashMap for that event.
		* If the event has not had another listener registered with the same priority, construct a List for that priority.
		* This keeps unnecessary Lists and Maps from being created.
		**/
		if(eventMap.get(eventRegistering) == null)
			eventMap.put(eventRegistering, new HashMap<>());
		if(eventMap.get(eventRegistering).get(priority) == null)
			eventMap.get(eventRegistering).put(priority, new ArrayList<>());
		
		eventMap.get(eventRegistering).get(priority).add(new EventListenerPair(eventListener, m));
	}
	
	public void post(Object event)
	{
		// If the event being posted has no registered listeners, don't bother posting to the bus.
		if(!eventMap.containsKey(event.getClass())) return;
		
		try 
		{
			int numPriorities = EventPriority.values().length;
			for(int i = numPriorities - 1; i >= 0; i--)
			{
				if(eventMap.get(event.getClass()).containsKey(EventPriority.values()[i]))
				{					
					// Post to all priority event buses, in order of priority.
					for(EventListenerPair listener : eventMap.get(event.getClass()).get(EventPriority.values()[i]))
					{
						listener.postEvent(event);	
					}
				}
			}
		} 
		catch (IllegalAccessException e) 
		{
			System.err.println("Java Reflection accessibility not changed. Set accessible!");
			e.printStackTrace();
		} 
		catch (IllegalArgumentException e) 
		{
			e.printStackTrace();
		} 
		catch (InvocationTargetException e) 
		{
			e.printStackTrace();
		}
	}
	
	private class EventListenerPair
	{
		public final Object listenerObject;
		public final Method methodInvoked;
		
		public EventListenerPair(Object eventListener, Method invokedMethod)
		{
			this.listenerObject = eventListener;
			this.methodInvoked = invokedMethod;
		}

		public void postEvent(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException 
		{
			this.methodInvoked.setAccessible(true);
			this.methodInvoked.invoke(this.listenerObject, event);
		}
	}
	
	public String getName()
	{
		return this.busName;
	}
	
	public static EventBus instance()
	{
		return INSTANCE;
	}
}
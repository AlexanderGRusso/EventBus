package com.russosoftware.src.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Distributes events to register Event Listeners
 **/
public class EventBus
{
	/**
	 * Default EventBus
	 **/
	private static EventBus INSTANCE;
	
	static
	{
		try 
		{
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
	 * HashMap constructed to contain the event busses registered to post the event and distribute it to the event listeners.
	 **/
	private static Map<String, EventBus> busMap = new HashMap<>();
	
	/**
	 * HashMap constructed to contain the event listeners registered to receive the event bus posting.
	 **/
	private HashMap<Class<? extends IEvent>, List<EventListenerPair>> eventMap = new HashMap<>();
	private final String busName;
	
	private EventBus() throws IllegalBusNameException
	{
		this("Default Bus");
		EventBus.busMap.put("Default Bus", this);
	}
	
	public EventBus(String busName) throws IllegalBusNameException
	{
		if(busMap == null) busMap = new HashMap<>();		/** In the event that the bus map has not been constructed, construct it to avoid a NullPointerException **/
		if(eventMap == null) eventMap = new HashMap<>();	/** In the event that the event map has not been constructed, construct it to avoid a NullPointerException **/
		
		if(!busMap.isEmpty() && busMap.containsKey(busName))
				throw new IllegalBusNameException("The name utilized for this bus has already been used. Please choose another.");
		
		this.busName = busName;
	}
	
	public void registerEventBusListener(IEventListener eventListener)
	{
		for(Method m : eventListener.getClass().getDeclaredMethods())
		{
			if(m.getParameterTypes().length == 1 && m.getParameterTypes()[0].getSuperclass().isAssignableFrom(IEvent.class))
			{
				Class<? extends IEvent> eventRegistering = (Class<? extends IEvent>) m.getParameterTypes()[0];
				
				System.out.println(eventRegistering.getName() + " : " + eventListener.toString() + " : " + m.getName());
				
				if(eventMap.containsKey(eventRegistering))
				{
					eventMap.get(eventRegistering).add(new EventListenerPair(eventListener, m));
					return;
				}
				List<EventListenerPair> newEventListenerList = new ArrayList<>();
				newEventListenerList.add(new EventListenerPair(eventListener, m));
				eventMap.put(eventRegistering, newEventListenerList);
			}
		}
	}
	
	public void post(IEvent event)
	{
		if(!eventMap.containsKey(event.getClass())) return;
		
		for(EventListenerPair listener : eventMap.get(event.getClass()))
		{
			try 
			{
				listener.methodInvoked.setAccessible(true);
				listener.methodInvoked.invoke(listener.listenerObject, event);
			} 
			catch (IllegalAccessException e) 
			{
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
	}
	
	private class EventListenerPair
	{
		public final IEventListener listenerObject;
		public final Method methodInvoked;
		
		public EventListenerPair(IEventListener listener, Method invokedMethod)
		{
			this.listenerObject = listener;
			this.methodInvoked = invokedMethod;
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
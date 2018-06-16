package com.russosoftware.src.utilities;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for ensuring that a particular method is an event handler.
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler 
{
	EventPriority priority() default EventPriority.NORMAL;
	
	enum EventPriority
	{
		LOW,
		NORMAL,
		HIGH
	}
}

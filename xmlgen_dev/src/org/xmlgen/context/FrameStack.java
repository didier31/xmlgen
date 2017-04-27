package org.xmlgen.context;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FrameStack implements Map<String, Object> 
{	
	Stack<Frame> stack = new Stack<Frame>();
	
	public FrameStack(String frameName)
	{		
		stack.push(new Frame(frameName, 0));
	}
	
	/**
	 * Clear top frame
	 */
	@Override
	public void clear() 
	{
		stack.peek().clear();
	}

	public Frame peek()
	{
		return stack.peek();
	}
	
	/**
	 * Searchs key in all Frame Stack (not only the top frame).
	 */
	@Override
	public boolean containsKey(Object key) {
		String keyStr = (String) key;
		boolean notFound;
		int i = stack.size();
		do
		{
			i--;
			notFound = stack.elementAt(i).containsKey(keyStr);
		}
		while (notFound || i > 0); 
		return !notFound;
	}

	/**
	 * Returns if stack frame contains value 
	 */
	@Override
	public boolean containsValue(Object value) 
	{
		boolean notFound;
		int i = stack.size();
		do
		{
			i--;
			notFound = stack.elementAt(i).containsValue(value);
		}
		while (notFound || i > 0); 
		return !notFound;
	}

	
	/**
	 * Returns top frame's entry set 
	 */	
	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() 
	{
	return null;
	}

	/**
	 * Apply Map<K,V>.get(Object key) on the stack beginning by the top.
	 */
	@Override
	public Object get(Object key) 
	{
		int i = stack.size();
		Object value;
		do
		{
			i--;
			value = stack.elementAt(i).get(key);
		}
		while (value == null && i > 0); 
		return value;
	}

	/**
	 * Check if frame stack is empty.
	 * Should always return false.
	 */
	@Override
	public boolean isEmpty() 
	{
		return stack.isEmpty();
	}

	/**
	 * Returns all reference names
	 */
	@Override
	public Set<String> keySet() {
		;
		Set<String> values = new HashSet<String>(100);
		for (int i = stack.size() - 1; i >= 0; i--)
		{	
			values.addAll(stack.elementAt(i).keySet());		
		}
		return values;
	}

	/**
	 * Put value on top frame
	 */
	@Override
	public Object put(String key, Object value) 
	{		
		return stack.peek().put(key, value);
	}

	/**
	 * putAll in the top frame.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) 
	{
		stack.peek().putAll(m);		
	}

	
	/**
	 * remove from the top frame.
	 */
	@Override
	public Object remove(Object key) 
	{		
		return stack.peek().remove(key);
	}

	@Override
	/*
	 * Returns size of stack as a large Map
	 */
	public int size() 
	{
		int size = 0;
		for (int i = 0; i < stack.size(); i++)
		{	
			size += stack.elementAt(i).size();		
		}
		return size;
	}

	@Override
	public Collection<Object> values() 
	{		
		int i = stack.size() - 1;
		@SuppressWarnings("unchecked")
		Collection<Object> values = (Collection<Object>) stack.elementAt(i);
		for (i--;i >= 0;i--)
		{	
			@SuppressWarnings("unchecked")
			Collection<Object> otherValues = (Collection<Object>) stack.elementAt(i);
			values.addAll(otherValues);		
		}
		return values;
	}

	/**
	 * Push frame on stack
	 * @param frame
	 */
	public void push(Frame frame)
	{
		assert frame.getLevel() >= stack.peek().getLevel();
		stack.push(frame);
	}
	
	/**
	 * Pop frame from stack
	 */
	public void pop()
	{
		stack.pop();
	}
	
	/**
	 * Pop all frames with frame Names at the same level as top frame.
	 * @param frameNames
	 */
	public void pop(Iterable<String> frameNames)
	{
		for (String frameName : frameNames)
		{
			int index = search(frameName);
			if (index > -1)
			{
				stack.setSize(index);
			}
		}
	}
	
	/**
	 * Returns index of the frame related to frameName with the same level as top frame.
	 * @param frameName
	 * @return index in stack or -1 if not found
	 */
	protected int search(String frameName)
	{
		int index = stack.size();
		int level = stack.peek().getLevel();
		Frame frame;
		do 
		{
			index--;
			frame = stack.elementAt(index);
		} 
		while (index >= 0 && frame.getLevel() == level && !frame.getName().equals(frameName));
		
		return (frame.getLevel() == level) ? index : -1;
	}
	
	@Override
	public String toString()
	{
		String string = "";
		for (String key  : keySet())
		{
			string += '(' + key + ", " + get(key) + ")\n";
		}
	return string;
	}
}


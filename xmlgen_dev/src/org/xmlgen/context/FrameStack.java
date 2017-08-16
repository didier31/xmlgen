/*
 * 
 */
package org.xmlgen.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * The Class FrameStack.
 * 
 * @author Didier Garcin
 */
public class FrameStack implements Map<String, Object>
{

	/** The stack. */
	Stack<Frame> stack = new Stack<Frame>();

	/**
	 * Instantiates a new frame stack.
	 *
	 * @param frameName
	 *           the frame name
	 */
	public FrameStack(String frameName)
	{
		stack.push(new Frame(frameName));
		pushNumbering();
	}
	
	/**
	 * Gets frames count
	 * @return the number of frames
	 */
	public int framesCount()
	{
		return stack.size();
	}
	
	
	/**
	 * Gets the frame at 
	 *
	 * @param index
	 * @return the frame at index
	 */
	public Frame elementAt(int index)
	{
		return stack.elementAt(index);
	}

	/**
	 * Clear top frame.
	 */
	@Override
	public void clear()
	{
		stack.peek().clear();
	}

	/**
	 * Peek.
	 *
	 * @return the frame
	 */
	public Frame peek()
	{
		return stack.peek();
	}

	/**
	 * Searchs key in all Frame Stack (not only the top frame).
	 *
	 * @param key
	 *           the key
	 * @return true, if successful
	 */
	@Override
	public boolean containsKey(Object key)
	{
		String keyStr = (String) key;
		boolean found = false;
		int i = stack.size()-1;
		while (!found && i >= 0)
		{
		found = stack.elementAt(i).containsKey(keyStr);
			i--;
		}
		return found;
	}

	/**
	 * Returns if stack frame contains value.
	 *
	 * @param value
	 *           the value
	 * @return true, if successful
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
	 * Apply Map<K,V>.get(Object key) on the stack beginning by the top.
	 *
	 * @param key
	 *           the key
	 * @return the object
	 */
	@Override
	public Object get(Object key)
	{
		int i = stack.size();
		Object value = null;
		boolean found = false;
		while (!found && i > 0)
		{
			i--;
			Frame frame = stack.elementAt(i);
			found =  frame.containsKey(key);
			if (found)
			{
				value = stack.elementAt(i).get(key);
			}
		}		
		return value;
	}

	/**
	 * Check if frame stack is empty. Should always return false.
	 *
	 * @return true, if is empty
	 */
	@Override
	public boolean isEmpty()
	{
		return stack.isEmpty();
	}

	/**
	 * Returns all reference names.
	 *
	 * @return the sets the
	 */
	@Override
	public Set<String> keySet()
	{
		;
		Set<String> values = new HashSet<String>(100);
		for (int i = stack.size() - 1; i >= 0; i--)
		{
			values.addAll(stack.elementAt(i).keySet());
		}
		return values;
	}

	/**
	 * Put value on top frame.
	 *
	 * @param key
	 *           the key
	 * @param value
	 *           the value
	 * @return the object
	 */
	@Override
	public Object put(String key, Object value)
	{
		return stack.peek().put(key, value);
	}

	/**
	 * putAll in the top frame.
	 *
	 * @param m
	 *           the m
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> m)
	{
		stack.peek().putAll(m);
	}

	/**
	 * remove from the top frame.
	 *
	 * @param key
	 *           the key
	 * @return the object
	 */
	@Override
	public Object remove(Object key)
	{
		return stack.peek().remove(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	@Override
	public Collection<Object> values()
	{
		int i = stack.size() - 1;
		@SuppressWarnings("unchecked")
		Collection<Object> values = (Collection<Object>) stack.elementAt(i);
		for (i--; i >= 0; i--)
		{
			@SuppressWarnings("unchecked")
			Collection<Object> otherValues = (Collection<Object>) stack.elementAt(i);
			values.addAll(otherValues);
		}
		return values;
	}

	/**
	 * Push frame on stack.
	 *
	 * @param frame
	 *           the frame
	 */
	public void push(Frame frame)
	{
		Integer level = numbering.peek() + 1;
		numbering.set(numbering.size() - 1, level);
		frame.setLevel(numberingToString());
		stack.push(frame);			
	}

	/**
	 * Pop frame from stack.
	 */
	public void pop()
	{
		stack.pop();
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		String string = "";
		for (String key : keySet())
		{
			string += '(' + key + ", " + get(key) + ")\n";
		}
		return string;
	}

	/**
	 * Returns top frame's entry set.
	 *
	 * @return the sets the
	 */
	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet()
	{
		HashMap<String, Object> identifiers = new HashMap<String, Object>(size()); 
		ArrayList<java.util.Map.Entry<String, Object>> copy = new ArrayList<java.util.Map.Entry<String, Object>>(size());		
		for (int i = stack.size() - 1; i >= 0; i--)
		{
			for (Entry<String, Object> e : stack.elementAt(i).entrySet())
			{
				String identifier = e.getKey();
				if (!identifiers.containsKey(identifier))
				{
					Object value = e.getValue();
					copy.add(new MyEntry<String, Object>(identifier, value));
					identifiers.put(identifier, null);
				}				
			}
		}
		return new HashSet<java.util.Map.Entry<String, Object>>(copy);
	}

	/**
	 * The Class MyEntry.
	 *
	 * @param <K>
	 *           the key type
	 * @param <V>
	 *           the value type
	 */
	final class MyEntry<K, V> implements Map.Entry<K, V>
	{

		/** The key. */
		private final K key;

		/** The value. */
		private V value;

		/**
		 * Instantiates a new my entry.
		 *
		 * @param key
		 *           the key
		 * @param value
		 *           the value
		 */
		public MyEntry(K key, V value)
		{
			this.key = key;
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Map.Entry#getKey()
		 */
		@Override
		public K getKey()
		{
			return key;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Map.Entry#getValue()
		 */
		@Override
		public V getValue()
		{
			return value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Map.Entry#setValue(java.lang.Object)
		 */
		@Override
		public V setValue(V value)
		{
			V old = this.value;
			this.value = value;
			return old;
		}
	}
	
	public void pushNumbering()
	{
		numbering.push(0);
	}
	
	public void popNumbering()
	{
		numbering.pop();
	}	
	
	protected String numberingToString()
	{
		String numberingStr = numbering.firstElement().toString();
		for (int i = 1; i < numbering.size(); i++)
		{
			numberingStr += "." + numbering.elementAt(i).toString();
		}
		return numberingStr;
	}
	
	private Stack<Integer> numbering = new Stack<Integer>(); 
}

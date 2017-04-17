package org.xmlgen.expansion;

import org.w3c.dom.Node;

public class Expander 
{
	
	public Node expand(Node root)
	{
		Node[] nodes = expandR(root);
		return nodes[0];
	}

	protected Node[] expandR(Node root)
	{
		// TODO : Stub
		return null;
	}
}

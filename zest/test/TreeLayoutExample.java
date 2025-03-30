package test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

public class TreeLayoutExample {
	public static void main(String[] args)
	{
	    Display d = new Display();
	    Shell shell = new Shell(d);
	    shell.setText("GraphSnippet1");
	    shell.setLayout(new FillLayout());
	    shell.setSize(1280, 500);
	
	    final Graph g = new Graph(shell, SWT.NONE);
	    g.setSize(-1, -1);
	
	    GraphNode root = new GraphNode(g, SWT.NONE, "ROOT");
	    root.setSize(-1, -1);
	
	    for (int i = 0; i < 3; i++)
	    {
	        GraphNode n = new GraphNode(g, SWT.NONE, "LONG TEXT");
	        n.setSize(-1, -1);
	
	        for (int j = 0; j < 2; j++)
	        {
	            GraphNode n2 = new GraphNode(g, SWT.NONE, "EVEN LONGER TEXT");
	            n2.setSize(-1, -1);
	            new GraphConnection(g, SWT.NONE, n, n2).setWeight(-1);
	        }
	
	        new GraphConnection(g, SWT.NONE, root, n);
	    }
	
	    TreeLayoutAlgorithm layoutAlgorithm = new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	    g.setLayoutAlgorithm(layoutAlgorithm, true);
	
	    shell.open();
	
	    while (!shell.isDisposed())
	    {
	        while (!d.readAndDispatch())
	        {
	            d.sleep();
	        }
	    }
	}
}


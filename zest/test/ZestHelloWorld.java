package test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;

public class ZestHelloWorld {

	public static void main(String[] args) {
		Display d = new Display();
		Shell shell = new Shell(d);
		Image image1 = Display.getDefault().getSystemImage(SWT.ICON_INFORMATION);
		Image image2 = Display.getDefault().getSystemImage(SWT.ICON_WARNING);
		Image image3 = Display.getDefault().getSystemImage(SWT.ICON_ERROR);
		shell.setLayout(new FillLayout());
		shell.setSize(400, 400);

		Graph g = new Graph(shell, SWT.NONE);
		g.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				System.out.println(((Graph) e.widget).getSelection());
			}

		});
		
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		GraphNode n1 = new GraphNode(g, SWT.NONE, "Information", image1);
		GraphNode n2 = new GraphNode(g, SWT.NONE, "Warning", image2);
		GraphNode n3 = new GraphNode(g, SWT.NONE, "Error", image3);

		new GraphConnection(g, SWT.NONE, n1, n2);
		new GraphConnection(g, SWT.NONE, n2, n3);

		g.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true);

		shell.open();
		while (!shell.isDisposed()) {
			while (!d.readAndDispatch()) {
				d.sleep();
			}
		}
		
		image1.dispose();
		image2.dispose();
		image3.dispose();
	}
}
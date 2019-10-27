package org.anchoranalysis.bean.gui;

/*
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.Position;



/**
 * A dialog that displays a list of items, and allows the user to select one or more
 *  
 * @author Owen Feehan
  */
class ListDialog extends JDialog {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JList<String> list;
		private String[] strArr;
		private boolean success = false;
		private boolean selectOneOnly;
		private String defaultChoice;
		
		/**
		 * Creates the dialog with a list of items
		 * 
		 * @param frame parent-frame
		 * @param modal is a modal diagram?
		 * @param items items to present to the user
		 * @param title title of dialog
		 * @param selectOneOnly the user may select only a single item
		 * @param defaultChoice the default (already selected item)
		 */
		public ListDialog(JFrame frame, boolean modal, String[] items, String title, boolean selectOneOnly, String defaultChoice) {  
	       super(frame, modal);
	       this.selectOneOnly = selectOneOnly;
	       this.defaultChoice = defaultChoice;
	       this.strArr = items;
	       setTitle(title);
	       initComponents();  
	       pack();  
	       setLocationRelativeTo(frame);
	       setAlwaysOnTop(true);
	       setVisible(true);
	       setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	   } 

		/**
		 * Selected indices
		 * 
		 * @return an array of integers, 0,1,2 etc. or an empty array if nothing is sslected
		 */
		public int[] getSelectedIndices() {
			if (success) {
				return list.getSelectedIndices();
			} else {
				return new int[]{};
			}
		}

		/**
		 * Did the user click OK?
		 * @return true if yes, false otherwise
		 */
		public boolean isSuccess() {
			return success;
		}
		
		private void initComponents() {
			
			list = new JList<>(strArr);
			setupList(list);
			
		    setLayout(new BorderLayout());
		    
		    JButton buttonOK = createButtonOK();
		    addPanels(buttonOK);
		    getRootPane().setDefaultButton(buttonOK);
		    buttonOK.requestFocus();
		}
		
		private void setupList( JList<String> list ) {
			if (selectOneOnly) {
				list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
			} else {
				list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			}
			
			if (defaultChoice!=null) {
				int selectedIndex = list.getNextMatch(defaultChoice, 0, Position.Bias.Forward);
				list.setSelectedIndex(selectedIndex);
			} else {
				if (!selectOneOnly) {
					selectAll();	
				}
			}
		}
		
		private void addPanels(  JButton buttonOK ) {
			JScrollPane pane = new JScrollPane(
				list,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
            );
		    pane.setPreferredSize(new Dimension(500, 600));
		    add(pane, BorderLayout.CENTER);
		    add(createBottomPanel(buttonOK), BorderLayout.SOUTH);
		}
		
		private JPanel createBottomPanel( JButton buttonOK ) {
			
			JPanel panel = new JPanel();
		    panel.setLayout( new BorderLayout() );
		   
		    JPanel right = new JPanel();
		    right.setLayout( new FlowLayout() );
		    
		    JPanel left = new JPanel();
		    left.setLayout( new FlowLayout() );
		    
		    right.add( buttonOK );
		    right.add( createButtonCancel() );
		    
		    if (!selectOneOnly) {
			    left.add( createButtonAll() );
			    left.add( createButtonNone() );
			    panel.add(left, BorderLayout.WEST);
		    }
		    
		    panel.add(right, BorderLayout.EAST);
		    return panel;
		}
		
		
		private void selectAll() {
			list.setSelectionInterval(0, strArr.length-1);
		}
		
		private JButton createButtonOK() {
			JButton button = new JButton("OK");
		    button.addActionListener( e -> {success=true; setVisible(false); } );
		    return button;
		}
		
		private JButton createButtonCancel() {
			JButton button = new JButton("Cancel");
			button.addActionListener( (ActionEvent e) -> System.exit(0) );
		    getRootPane().setDefaultButton(button);
		    return button;
		}
		
		private JButton createButtonAll() {
			JButton button = new JButton("All");
			button.addActionListener( (ActionEvent e) -> selectAll() );
		    return button;
		}
		
		private JButton createButtonNone() {
			JButton button = new JButton("None");
			button.addActionListener( (ActionEvent e) -> list.clearSelection() );
		    return button;
		}
	
	}
package ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Utilities {
	/**
	 * Displays an informational message dialog.
	 * 
	 * @param parent  The parent component of this option dialog
	 * @param message The message to display
	 * @param title   The title of the dialog box
	 */
	public static void displayMessageDialog(Component parent, String message, String title) {
		JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays an option dialog, returning the choice selected by the user.
	 * 
	 * @param parent  The parent component of this option dialog
	 * @param message The message to display
	 * @param title   The title of the dialog box
	 * @param options The options to be provided in the dialog - the initial option
	 *                selected is the first element of the provided object array
	 * @return The choice made by the user
	 */
	public static int displayOptionDialog(Component parent, String message, String title, Object[] options) {
		return JOptionPane.showOptionDialog(parent, message, title, JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
	}
	
	/**
	 * Binds the specified keystroke to the specified JComponent.
	 * 
	 * @param component  The component on which the keystroke should be bound
	 * @param keystroke  The keystroke to bind
	 * @param actionName The name of keystroke action
	 * @param method     The method to execute when the keystroke is activated
	 */
	public static void bindKeyStroke(JComponent component, String keystroke, String actionName, Runnable method) {
		if (keystroke.length() == 1) {
			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keystroke.charAt(0)),
					actionName);
		} else {
			component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keystroke), actionName);
		}
		component.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				method.run();
			}

		});
	}
}

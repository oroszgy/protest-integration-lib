/*******************************************************************************
 * Copyright (c) 2010 György Orosz
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package org.protest.integration.lib.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.Workbench;

public class DynamicInputDialog extends Dialog {

	/**
	 * Runs a dynamic input dialog, with the given paramters and title
	 * 
	 * @param title
	 *            Dialog title
	 * @param parameterNames
	 *            parameters to get
	 * @return
	 * @throws NullInputException
	 *             if the user press cancel button
	 */
	public static ArrayList<String> run(String title, String... parameterNames)
			throws NullInputException {
		Shell parent = Workbench.getInstance().getActiveWorkbenchWindow()
				.getShell();
		Shell shell = parent;
		ArrayList<String> par = new ArrayList<String>();
		for (String s : parameterNames) {
			par.add(s);
		}
		DynamicInputDialog dialog = new DynamicInputDialog(shell, title, par);
		if (Dialog.OK == dialog.open())
			if (dialog.isFinished()) {
				return dialog.getInput();
			}

		throw new NullInputException();
	}

	protected DynamicInputDialog(Shell parentShell, String title,
			List<String> parametersName) {
		super(parentShell);
		this.title = title;
		this.parameterNames = parametersName;
	}

	protected List<String> parameterNames;
	protected ArrayList<String> parameterValues;
	// protected List<Label> parameterLabels;
	protected ArrayList<Text> parameterTexts;
	protected final String title;
	protected boolean isFinished = false;
	protected Button okButton;

	public boolean isFinished() {
		return isFinished;
	}

	public ArrayList<String> getInput() {
		return parameterValues;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		// parameterLabels = new ArrayList<Label>();
		parameterTexts = new ArrayList<Text>();
		parameterValues = new ArrayList<String>(parameterNames.size());
		for (String p : parameterNames) {

			Label label = new Label(composite, SWT.WRAP);
			label.setText(p + ":");
			GridData minToksData = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			minToksData.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			label.setLayoutData(minToksData);
			label.setFont(parent.getFont());

			parameterValues.add("");

			Text text = new Text(composite, getInputTextStyle());
			text.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {

					for (int i = 0; i < parameterTexts.size(); ++i) {
						parameterValues.set(i, parameterTexts.get(i).getText());
					}

				}
			});
			text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
					| GridData.HORIZONTAL_ALIGN_FILL));
			parameterTexts.add(text);
		}
		return composite;

	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);

		okButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				isFinished = true;
			}

		});
	}
}

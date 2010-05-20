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
package org.protest.integration.lib.textutils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.erlide.core.erlang.ErlModelException;
import org.erlide.core.erlang.ErlangCore;
import org.erlide.core.erlang.IErlElement;
import org.erlide.core.erlang.IErlFunctionClause;
import org.erlide.core.erlang.IErlMember;
import org.erlide.core.erlang.IErlModule;
import org.erlide.core.erlang.ISourceRange;
import org.protest.integration.lib.ui.DynamicInputDialog;
import org.protest.integration.lib.ui.NullInputException;

/**
 * Utility class, which contain functions, for basic text manipulation in the
 * editor plane.
 * 
 * @author György Orosz
 * 
 */
public class EditorUtils {

	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String COMMA = ", ";
	public static final String TAB = "\t";

	static public ISourceRange getActiveFunctionClauseRange()
			throws ErlModelException {
		if (getActiveElement() instanceof IErlFunctionClause) {
			IErlMember m = (IErlMember) getActiveElement();
			return m.getSourceRange();
		}
		return null;
	}

	// TODO: error handling: if no member is selected

	static public String getActiveFunctionClause() throws BadLocationException,
			ErlModelException {
		ITextEditor editor = getEditor();
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		if (getActiveFunctionClauseRange() == null)
			return null;
		return doc.get(getActiveFunctionClauseRange().getOffset(),
				getActiveFunctionClauseRange().getLength());
	}

	static protected IErlElement getActiveElement() {

		IErlModule module = ErlangCore.getModel().findModule(getFileName());

		try {
			IErlElement element = module
					.getElementAt(getActiveSelectionOffset());
			if (element == null)
				return module;
			else
				return element;

		} catch (ErlModelException e) {
		}
		return module;
	}

	/**
	 * Returns the actual position of the cursor in a text editor
	 * 
	 * @return
	 */
	static public int getActiveSelectionOffset() {
		int offset = ((TextSelection) getEditor().getSelectionProvider()
				.getSelection()).getOffset();
		return offset;
	}

	/**
	 * Returns the actual selection's length
	 * 
	 * @return
	 */
	static public int getActiveSelectionLength() {
		int length = ((TextSelection) getEditor().getSelectionProvider()
				.getSelection()).getLength();
		return length;
	}

	/**
	 * Inserts text asound the current selection
	 * 
	 * @param before
	 *            text before the selection
	 * @param after
	 *            text after the selection
	 */
	static public void insertText(String before, String after) {
		insertText(before, after, getActiveSelectionOffset(),
				getActiveSelectionLength());

	}

	/**
	 * Inserts text before the active editor's selected position
	 * 
	 * @param text
	 */
	static public void insertText(String text) {
		insertText(text, getActiveSelectionOffset());
	}

	/**
	 * Inserts text to the active editor
	 * 
	 * @param offest
	 *            where to insert
	 * @param text
	 *            what to insert
	 */
	static public void insertText(String text, int offset) {
		insertText(text, "", offset, 0);
	}

	static public void insertText(InsertionStringPair s) {
		int offset;
		try {
			switch (s.position) {
			case BEFORE:
				offset = getActiveFunctionClauseRange().getOffset();
				break;
			case AFTER:
				offset = getActiveFunctionClauseRange().getOffset()
						+ getActiveFunctionClauseRange().getLength();
				break;
			default:
				offset = getActiveSelectionOffset();
			}
		} catch (Exception e) {
			offset = getActiveSelectionOffset();
		}
		if (!s.second.equals(""))
			EditorUtils.insertText(s.first, s.second + EditorUtils.NEWLINE);
		else
			EditorUtils.insertText(s.first + EditorUtils.NEWLINE, offset);
	}

	/**
	 * Insert texts around a selection
	 * 
	 * @param before
	 *            string inserted before the selection
	 * @param after
	 *            string inserted after the selection
	 * @param offset
	 *            selection offest
	 * @param length
	 *            selection length
	 */
	static protected void insertText(String before, String after, int offset,
			int length) {
		ITextEditor editor = getEditor();
		if (editor == null)
			return;
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		try {
			String original = doc.get(offset, length);
			// ident original
			original = original.replace(EditorUtils.NEWLINE,
					EditorUtils.NEWLINE + EditorUtils.TAB);
			doc.replace(offset, length, before + original + after);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the active text editor object
	 * 
	 * @return
	 */
	static protected ITextEditor getEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();
		if (!(part instanceof AbstractTextEditor))
			return null;
		ITextEditor editor = (ITextEditor) part;
		return editor;
	}

	/**
	 * Returns the currently opened filename
	 * 
	 * @return
	 */
	static public String getFileName() {

		String moduleName = getEditor().getEditorInput().getName();

		return moduleName;
	}

	/**
	 * Returns the currently opened module name
	 * 
	 * @return
	 */
	static public String getModuleName() {
		return getFileName().substring(0, getFileName().lastIndexOf("."));
	}

	/**
	 * Returns the system user name
	 * 
	 * @return
	 */
	static public String getAuthorName() {
		return getUserName();
	}

	/**
	 * Returns the current date
	 * 
	 * @return
	 */
	static public String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.UK);
		Date date = new Date();
		return dateFormat.format(date);
	}

	/**
	 * Returns the the system's user name
	 * 
	 * @return
	 */
	static public String getUserName() {
		return System.getProperty("user.name");
	}

	/**
	 * Returns the machine name
	 * 
	 * @return
	 */
	static public String getMachineName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Compass the current selection with the given macro and parameters
	 * 
	 * @param name
	 *            macro name
	 * @param title
	 *            input dialog box title
	 * @param originalOrdinalNumber
	 *            the original selection place between the parameters
	 * @param parameters
	 *            macro parameters
	 * @return
	 * @throws NullInputException
	 *             if the user press cancel in the input dialog
	 */
	public static InsertionStringPair compassWith(String name, String title,
			OrdinalNumber originalOrdinalNumber, String... parameters)
			throws NullInputException {
		ArrayList<String> input = new ArrayList<String>();
		String after = "";
		String before = "";
		if (parameters != null && parameters.length > 0)
			input = DynamicInputDialog.run(title, parameters);
		before += name + "(";
		if (originalOrdinalNumber.equals(OrdinalNumber.Last)) {
			for (String s : input)
				before += s + EditorUtils.COMMA;
			before += EditorUtils.NEWLINE + EditorUtils.TAB;
		} else {
			int n = originalOrdinalNumber.getValue();
			for (int i = 0; i < n; ++i) {
				before += input.get(i) + EditorUtils.COMMA;
			}
			after += EditorUtils.COMMA;
			for (int i = n; i < input.size(); ++i) {
				after += input.get(i);
				if (i < input.size() - 1)
					after += EditorUtils.COMMA;
			}
		}
		after += ")";
		return new InsertionStringPair(before, after);

	}

	/**
	 * Compass the current selection with the given macro or function
	 * 
	 * @param name
	 *            macro / function name
	 * @param title
	 * @return
	 * @throws NullInputException
	 */
	public static InsertionStringPair compassWith(String name, String title)
			throws NullInputException {
		return compassWith(name, title, new String[0]);
	}

	/**
	 * Compass the current selection with the given macro or function name and
	 * parameters. The original selection will be the last parameter in the
	 * macro.
	 * 
	 * @param name
	 *            macro name
	 * @param title
	 *            input dialog title
	 * @param parameters
	 *            macro parameters
	 * @return
	 * @throws NullInputException
	 *             if the user press cancel in the input dialog
	 */
	public static InsertionStringPair compassWith(String name, String title,
			String... parameters) throws NullInputException {
		return compassWith(name, title, OrdinalNumber.Last, parameters);
	}

	/**
	 * Creates a macro or function call with the given name and parameters
	 * 
	 * @param name
	 *            macro/function name
	 * @param title
	 *            input dialog box title
	 * @param parameters
	 *            macro/function parameters name
	 * @return
	 * @throws NullInputException
	 *             if the user press the cancel button in the input dialog
	 */
	public static String createCall(String name, String title,
			String... parameters) throws NullInputException {
		ArrayList<String> input = new ArrayList<String>();
		if (parameters != null && parameters.length > 0)
			input = DynamicInputDialog.run(title, parameters);
		String ret = name + "(";
		for (int i = 0; i < input.size(); ++i) {
			String s = input.get(i);
			ret += s;
			if (i < input.size() - 1)
				ret += EditorUtils.COMMA;
		}

		// ret += EditorUtils.NEWLINE + EditorUtils.TAB;
		ret += ")";
		return ret;
	}

	/**
	 * Creates a single macro or function call with the given name, without any
	 * parameters
	 * 
	 * @param name
	 *            macro name
	 * @param title
	 * @return
	 * @throws NullInputException
	 */
	public static String createCall(String name, String title)
			throws NullInputException {
		return createCall(name, title, new String[0]);

	}

}

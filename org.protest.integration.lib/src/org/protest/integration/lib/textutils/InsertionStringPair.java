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

/**
 * Simple class for handling String pairs
 * 
 * @author György Orosz
 * 
 */
public class InsertionStringPair {
	public enum Position {
		BEFORE, AFTER, DEFAULT
	}

	public Position position;
	public String first;
	public String second;

	public InsertionStringPair(String first, String second, Position position) {
		this.position = position;
		this.first = first;
		this.second = second;
	}

	public InsertionStringPair(String first, String second) {
		this(first, second, Position.DEFAULT);
	}

	@Override
	public String toString() {
		return first + second;
	}
}

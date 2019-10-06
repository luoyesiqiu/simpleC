/*
 * Copyright (c) 2013 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */

package com.myopicmobile.textwarrior.common;


public class ColorSchemeDark extends ColorScheme {

	public ColorSchemeDark(){
		setColor(Colorable.FOREGROUND, OFF_WHITE);
		setColor(Colorable.BACKGROUND, OFF_BLACK);
		setColor(Colorable.NON_PRINTING_GLYPH, DARK_GREY);
		setColor(Colorable.KEYWORD, 0xff569cd6);
		setColor(Colorable.STRING, 0xffce9178);
		setColor(Colorable.NUMBER, 0xffb5cea8);
		setColor(Colorable.SECONDARY, 0xffce9178);//宏
		setColor(Colorable.COMMENT, 0xff6a9742);
	}

	private static final int DARK_GREY = 0xFF606060;
	private static final int OFF_BLACK = 0xFF1e1e1e;
	private static final int OFF_WHITE = 0xFFD0D2D3;

	@Override
	public boolean isDark() {
		return true;
	}
}

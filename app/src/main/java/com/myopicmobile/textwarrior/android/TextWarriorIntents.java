/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.myopicmobile.textwarrior.android;

public class TextWarriorIntents {
	public final static String ACTION_PICK_FILE = "textwarrior.intent.pick_file";
	public final static String ACTION_PICK_FILENAME_FOR_SAVE = "textwarrior.intent.enter_filename";
	public final static int REQUEST_PICK_FILE = 4;
	public final static int REQUEST_PICK_FILENAME_FOR_SAVE = 8;
	public final static String EXTRA_TITLE = "textwarrior.extra.title";
	public final static String EXTRA_BUTTON_TEXT = "textwarrior.extra.button_text";
}

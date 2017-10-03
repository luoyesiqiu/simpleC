package org.openintents.filemanager;

import java.util.List;
/* 
 * @author Tah Wei Hoon
 * @date 30 Dec 2010
 *
 * - Made class public
 * - Added getters and setters
 * - Removed unused imports
 * - Removed noMedia boolean member
 */

public class DirectoryContents {
    List<IconifiedText> listDir;
    List<IconifiedText> listFile;
    List<IconifiedText> listSdCard;

    public final List<IconifiedText> getListDir() {
		return listDir;
	}

	public final void setListDir(List<IconifiedText> listDir) {
		this.listDir = listDir;
	}

	public final List<IconifiedText> getListFile() {
		return listFile;
	}

	public final void setListFile(List<IconifiedText> listFile) {
		this.listFile = listFile;
	}

	public final List<IconifiedText> getListSdCard() {
		return listSdCard;
	}

	public final void setListSdCard(List<IconifiedText> listSdCard) {
		this.listSdCard = listSdCard;
	}
}

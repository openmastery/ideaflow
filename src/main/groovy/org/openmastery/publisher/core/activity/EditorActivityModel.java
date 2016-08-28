package org.openmastery.publisher.core.activity;

import java.io.File;

public class EditorActivityModel extends ActivityModel<EditorActivityEntity> {

	public EditorActivityModel(EditorActivityEntity delegate) {
		super(delegate);
	}

	public String getFileName() {
		String filePath = getFilePath();
		return filePath == null ? null : new File(filePath).getName();
	}

	public String getFilePath() {
		return delegate.getFilePath();
	}

	public boolean isModified() {
		return delegate.isModified();
	}

}

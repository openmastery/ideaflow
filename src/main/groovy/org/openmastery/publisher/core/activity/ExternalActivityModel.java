package org.openmastery.publisher.core.activity;

public class ExternalActivityModel extends ActivityModel<ExternalActivityEntity> {

	public ExternalActivityModel(ExternalActivityEntity delegate) {
		super(delegate);
	}

	public String getComment() {
		return delegate.getComment();
	}

}

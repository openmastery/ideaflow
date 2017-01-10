package org.openmastery.storyweb.api.metrics;


import java.util.ArrayList;
import java.util.List;

public interface Explodable<T> {

	String getRelativePath();

	List<T> explode() throws CloneNotSupportedException;

}

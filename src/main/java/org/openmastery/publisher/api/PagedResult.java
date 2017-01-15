package org.openmastery.publisher.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class PagedResult<T> {

	List<T> contents;

	int totalPages;
	int totalElements;

	int pageNumber;
	int elementsPerPage;

	boolean hasNext;
	boolean hasPrevious;

	List<SortOrder> propertySortOrders;

	public PagedResult() {
		propertySortOrders = new ArrayList<SortOrder>();
		contents = new ArrayList<T>();
	}

	public void addSortOrder(String property, PagedResult.SortOrder.Direction direction) {
		propertySortOrders.add( new SortOrder(property, direction));
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	public static class SortOrder {
		String property;
		Direction direction;

		public enum Direction {
			ASC, DESC
		}
	}

}

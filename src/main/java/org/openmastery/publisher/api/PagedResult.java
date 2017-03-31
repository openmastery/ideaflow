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

	public static PagedResult create(int recordCount, int pageNumber, int elementsPerPage) {
		PagedResult pagedResult = new PagedResult();
		pagedResult.hasNext = (pageNumber + 1) * elementsPerPage < recordCount;
		pagedResult.hasPrevious = pageNumber > 0;
		pagedResult.pageNumber = pageNumber;
		pagedResult.totalPages = (recordCount / elementsPerPage) + ((recordCount % elementsPerPage) > 0 ? 1 : 0);
		pagedResult.totalElements = recordCount;
		pagedResult.elementsPerPage = elementsPerPage;
		return pagedResult;
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

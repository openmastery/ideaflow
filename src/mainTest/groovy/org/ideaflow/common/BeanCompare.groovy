package org.ideaflow.common

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError

/**
 * Logic is a bit wonky or repeated sometimes - this is primarily so we can rely on Spock power asserts to provide
 * good diagnostics when something goes wrong.
 */
class BeanCompare {

    private List excludeFieldList = []
    private List includeFieldList = []

    public BeanCompare excludeFields(String ... excludeFields) {
        excludeFieldList.addAll(excludeFields)
        assertIncludeExcludeFieldsMutuallyExclusive()
        this
    }

    public BeanCompare includeFields(String ... includeFields) {
        includeFieldList.addAll(includeFields)
        assertIncludeExcludeFieldsMutuallyExclusive()
        this
    }

    private assertIncludeExcludeFieldsMutuallyExclusive() {
        if ((includeFieldList.isEmpty() == false) && (excludeFieldList.isEmpty() == false)) {
            throw new IllegalStateException("Include and exclude fields are mutually exclusive")
        }
    }


    public void assertEquals(List lhsList, List rhsList) {
        if (lhsList.isEmpty()) {
            assert rhsList.isEmpty()
        }

        List lhsListCopy = new ArrayList(lhsList)
        rhsList.each { Object rhs ->
            Object lhs = find(lhsListCopy, rhs)
            if (lhs) {
                lhsListCopy.removeAll { it.is(lhs) }
            } else {
                assert find(lhsListCopy, rhs)
            }
        }

        assert lhsListCopy.isEmpty()
    }

    public boolean isEqual(List lhsList, List rhsList) {
        try {
            assertEquals(lhsList, rhsList)
            return true
        } catch (PowerAssertionError err) {
            return false
        }
    }

    public Object find(List list, Object object) {
        list.find { Object lhs ->
            isEqual(lhs, object)
        }
    }

    public void assertEquals(Object lhs, Object rhs) {
        assert (lhs == null && rhs == null) || (lhs != null && rhs != null)

        if (lhs.equals(rhs) == false) {
            Map rhsProperties = getPropertiesToCompare(rhs)
            Map lhsProperties = getPropertiesToCompare(lhs)
            assert lhsProperties == rhsProperties
        }
    }

    public boolean isEqual(Object lhs, Object rhs) {
        try {
            assertEquals(lhs, rhs)
            return true
        } catch (PowerAssertionError err) {
            return false
        }
    }

    private Map getPropertiesToCompare(Object object) {
        Map properties = object.properties
        properties.remove("class")
        removeExcludedFields(properties)
        removeAllButIncludedFields(properties)
        properties
    }

    private void removeExcludedFields(Map properties) {
        excludeFieldList.each { String field ->
            properties.remove(field)
        }
    }

    private void removeAllButIncludedFields(Map properties) {
        if (includeFieldList.isEmpty() == false) {
            (properties.keySet() as List).each { String key ->
                if (includeFieldList.contains(key) == false) {
                    properties.remove(key)
                }
            }
        }
    }

}

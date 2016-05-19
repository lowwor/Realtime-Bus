package com.lowwor.realtimebus.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by lowworker on 2016/5/19 0019.
 */
public class CollectionUtilsTest {

    @Test
    public void testRemoveDuplicate() throws Exception {

        List<String> items = new ArrayList<>();
        items.add("3A");
        items.add("3A");

        CollectionUtils.removeDuplicateWithOrder(items);

        assertThat(items).hasSize(1);
    }
}
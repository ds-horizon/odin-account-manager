package com.dream11.odin.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class CollectionUtilTest {

  @Test
  void testFilterList() {
    // arrange
    List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5);

    // act
    List<Integer> filteredList = CollectionUtil.filterList(val -> val > 2, intList);

    // assert
    assertThat(filteredList).hasSize(3).containsAll(Arrays.asList(3, 4, 5));
  }
}

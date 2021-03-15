package com.banksalad.collectmydata.irp.func;

import com.banksalad.collectmydata.irp.common.dto.IrpAccountDetail;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.clazz.ValueObjectDefinition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ListCompareCases {

  @Test
  @DisplayName("Compare the equality of the elements in two lists objects")
  void diffElementsInTwoLists_Same() {

    //given
    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    List<IrpAccountDetail> oldIrpAccountDetails = new ArrayList<>();
    oldIrpAccountDetails.add(IrpAccountDetail.builder()
        .irpName("가나다a")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.123"))
        .build());
    oldIrpAccountDetails.add(IrpAccountDetail.builder()
        .irpName("가나다ab")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.121"))
        .build());

    List<IrpAccountDetail> newIrpAccountDetails = new ArrayList<>();
    newIrpAccountDetails.add(
        IrpAccountDetail.builder()
            .irpName("가나다ab")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build());
    newIrpAccountDetails.add(
        IrpAccountDetail.builder()
            .irpName("가나다a")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.123"))
            .build());

    oldIrpAccountDetails.sort(Comparator.comparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal));

    newIrpAccountDetails.sort(Comparator.comparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal));

    //when
    Diff diff = javers.compareCollections(oldIrpAccountDetails, newIrpAccountDetails, IrpAccountDetail.class);

    //then
    assertThat(diff.getChanges()).hasSize(0);

    System.out.println(diff);
    System.out.println(javers.getJsonConverter().toJson(diff));
    diff.getChanges().forEach(change -> System.out.println("- " + change));
  }

  @Test
  @DisplayName("Compare the equality of the elements in two lists objects including ignored field")
  void diffElementsInTwoLists_IncludingIgnoredField_Same() {

    /*
      collection에서 sort할 때 사용한 field를 ignore field로 지정하면 sort가 무의미할 가능성 존재
     */
    //given
    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)
        .registerValueObject(new ValueObjectDefinition(IrpAccountDetail.class, List.of("openDate", "expDate")))
        .build();

    List<IrpAccountDetail> oldIrpAccountDetails = new ArrayList<>();
    oldIrpAccountDetails.add(IrpAccountDetail.builder()
        .irpName("가나다a")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(null)
//        .intRate(new BigDecimal("3333.123"))
        .build());
    oldIrpAccountDetails.add(IrpAccountDetail.builder()
        .irpName("가나다ab")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.111"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.121"))
        .build());

    List<IrpAccountDetail> newIrpAccountDetails = new ArrayList<>();
    newIrpAccountDetails.add(
        IrpAccountDetail.builder()
            .irpName("가나다ab")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.111"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build());
    newIrpAccountDetails.add(
        IrpAccountDetail.builder()
            .irpName("가나다a")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("11112222")
            .expDate("33334444")
            .intRate(null)
//            .intRate(new BigDecimal("3333.123"))
            .build());

    oldIrpAccountDetails.sort(Comparator.comparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal));

    newIrpAccountDetails.sort(Comparator.comparing(IrpAccountDetail::getIrpName)
        .thenComparing(IrpAccountDetail::getEvalAmt)
        .thenComparing(IrpAccountDetail::getInvPrincipal));

    //when
    Diff diff = javers.compareCollections(oldIrpAccountDetails, newIrpAccountDetails, IrpAccountDetail.class);

    //then
    assertThat(diff.getChanges()).hasSize(0);

    System.out.println(diff);
    System.out.println(javers.getJsonConverter().toJson(diff));
    diff.getChanges().forEach(change -> System.out.println("- " + change));
  }

  @Test
  @DisplayName("Compare the element in two list objectsz")
  void diffElementsInTwoLists() {

    // given
    // Javers javers = JaversBuilder.javers().build();
    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    List<IrpAccountDetail> oldIrpAccountDetails = List.of(IrpAccountDetail.builder()
        .irpName("가나다a")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.123"))
        .build());

    List<IrpAccountDetail> newIrpAccountDetails = List.of(IrpAccountDetail.builder()
        .irpName("가나다a")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.121"))
        .build());

    //when
    Diff diff = javers.compareCollections(oldIrpAccountDetails, newIrpAccountDetails, IrpAccountDetail.class);
    List<ValueChange> valueChanges = diff.getChangesByType(ValueChange.class);
    valueChanges.forEach(change -> System.out.println("- " + change));
    List<ListChange> listChanges = diff.getChangesByType(ListChange.class);
    listChanges.forEach(change -> System.out.println("- " + change));

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getPropertyName()).isEqualTo("intRate");
    assertThat(change.isPropertyAdded()).isEqualTo(false);
    assertThat(change.isPropertyRemoved()).isEqualTo(false);
    assertThat(change.isPropertyValueChanged()).isEqualTo(true);
    assertThat(change.getLeft()).isEqualTo(new BigDecimal("3333.123"));
    assertThat(change.getRight()).isEqualTo(new BigDecimal("3333.121"));

    System.out.println(diff);
//    System.out.println(javers.getJsonConverter().toJson(diff));
  }

  @Test
  @DisplayName("Compare the two lists in case of target")
  void diffElementsInTwoLists_InCaseOfAdding() {

    //given
    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    List<IrpAccountDetail> oldIrpAccountDetails = List.of(IrpAccountDetail.builder()
        .irpName("가나다a")
        .irpType("01")
        .evalAmt(new BigDecimal("100000.123"))
        .invPrincipal(new BigDecimal("99999.999"))
        .fundNum(5)
        .openDate("20200222")
        .expDate("20210330")
        .intRate(new BigDecimal("3333.123"))
        .build());

    List<IrpAccountDetail> newIrpAccountDetails = List.of(IrpAccountDetail.builder()
            .irpName("가나다a")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.123"))
            .build(),
        IrpAccountDetail.builder()
            .irpName("가나다ab")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build());

    //when
    Diff diff = javers.compareCollections(oldIrpAccountDetails, newIrpAccountDetails, IrpAccountDetail.class);

    //then
    assertThat(diff.getObjectsByChangeType(NewObject.class)).hasSize(1);
    assertThat(diff.getObjectsByChangeType(PropertyChange.class)).hasSize(1);

    System.out.println(diff);
    System.out.println(javers.getJsonConverter().toJson(diff));
    diff.getChanges().forEach(change -> System.out.println("- " + change));
  }

  @Test
  @DisplayName("two list objects diff remove")
  void diffElementsInTwoLists_InCaseOfRemoving() {

    //given
    Javers javers = JaversBuilder.javers()
        .withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();

    List<IrpAccountDetail> oldIrpAccountDetails = List.of(
        IrpAccountDetail.builder()
            .irpName("가나다a")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.123"))
            .build(),
        IrpAccountDetail.builder()
            .irpName("가나다ab")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build(),
        IrpAccountDetail.builder()
            .irpName("가나다abc")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build());

    List<IrpAccountDetail> newIrpAccountDetails = List.of(
        IrpAccountDetail.builder()
            .irpName("가나다ab")
            .irpType("01")
            .evalAmt(new BigDecimal("100000.123"))
            .invPrincipal(new BigDecimal("99999.999"))
            .fundNum(5)
            .openDate("20200222")
            .expDate("20210330")
            .intRate(new BigDecimal("3333.121"))
            .build());

    //when
    Diff diff = javers.compareCollections(oldIrpAccountDetails, newIrpAccountDetails, IrpAccountDetail.class);

    //then
    assertThat(diff.getObjectsByChangeType(ObjectRemoved.class)).hasSize(2);

    System.out.println(diff);
    System.out.println(javers.getJsonConverter().toJson(diff));
    diff.getChanges().forEach(change -> System.out.println("- " + change));
  }
}

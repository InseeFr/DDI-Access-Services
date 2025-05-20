package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FragmentDataTest {

    @Test
    void shouldIndicateIfTwoFragmentDateAreIdentical() {
        FragmentData fragmentDataExampleOne = new FragmentData("identifier","agencyId", "version");
        FragmentData fragmentDataExampleTwo = new FragmentData("identifier","agencyId", "version");
        FragmentData fragmentDataExampleThree = new FragmentData("example","agencyId", "version");
        assertTrue(fragmentDataExampleOne.equals(fragmentDataExampleTwo) && ! fragmentDataExampleOne.equals(fragmentDataExampleThree));
    }

    @Test
    void shouldReturnDifferentHashCodeForEachDifferentFragmentData() {

        FragmentData fragmentDataExampleOne = new FragmentData("identifierExampleOne","identifierExampleOne", "identifierExampleOne");
        FragmentData fragmentDataExampleTwo = new FragmentData("identifierExampleTwo","agencyIdExampleTwo", "versionExampleTwo");
        FragmentData fragmentDataExampleThree = new FragmentData("identifierExampleThree","agencyIdExampleThree", "versionExampleThree");

        boolean compareHshCodeOneAndTwo = fragmentDataExampleOne.hashCode() ==fragmentDataExampleTwo.hashCode() ;
        boolean compareHshCodeTwoAndThree = fragmentDataExampleTwo.hashCode() ==fragmentDataExampleThree.hashCode() ;
        boolean compareHshCodeThreeAndOne = fragmentDataExampleThree.hashCode() ==fragmentDataExampleOne.hashCode() ;

        assertEquals(List.of(false,false,false),List.of(compareHshCodeOneAndTwo,compareHshCodeTwoAndThree,compareHshCodeThreeAndOne));
    }

    @Test
    void shouldReturnSameHashCodeSameFragmentData() {

        FragmentData fragmentDataExampleOne = new FragmentData("identifierExampleOne","identifierExampleOne", "identifierExampleOne");
        FragmentData fragmentDataExampleTwo = new FragmentData("identifierExampleOne","identifierExampleOne", "identifierExampleOne");

        boolean compareHshCodeOneAndTwo = fragmentDataExampleOne.hashCode() ==fragmentDataExampleTwo.hashCode() ;

        assertTrue(compareHshCodeOneAndTwo);

    }


    void shouldReturnBooleanWhenTestEquals() {
        FragmentData fragmentData = new FragmentData("identifier", "agencyId", "version");
        FragmentData mockFragmentDataOne = new FragmentData("identifier", "agencyId", "version");
        FragmentData mockFragmentDataTwo = new FragmentData("id", "ag", "ve");
        String mockString = "example";
        boolean isEqualFragmentData= fragmentData.equals(fragmentData);
        boolean isEqualMockFragmentDataOne = fragmentData.equals(mockFragmentDataOne);
        boolean isEqualMockFragmentDataTwo = fragmentData.equals(mockFragmentDataTwo);
        boolean isEqualMockString = fragmentData.equals(mockString);
        assertTrue( isEqualFragmentData && isEqualMockFragmentDataOne && !isEqualMockFragmentDataTwo && !isEqualMockString);
    }
}
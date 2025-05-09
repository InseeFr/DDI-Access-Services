package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FragmentDataTest {

    @Test
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
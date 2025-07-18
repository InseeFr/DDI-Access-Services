package fr.insee.rmes.exceptions.errors;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CodesListErrorCodesTest {

    @Test
    void shouldCompareAnotherCodeWithTheOthers(){

        int reference = CodesListErrorCodes.STRUCTURE_DELETE_ONLY_UNPUBLISHED;

        List<Boolean> comparisons = List.of(reference==CodesListErrorCodes.CODE_LIST_UNICITY,
                reference==CodesListErrorCodes.CODE_LIST_AT_LEAST_ONE_CODE,
                reference== CodesListErrorCodes.CODE_LIST_DELETE_CODELIST_WITHOUT_PARTIAL,
                reference== CodesListErrorCodes.CODE_LIST_UNKNOWN_ID,
                reference==CodesListErrorCodes.CODE_LIST_DELETE_ONLY_UNPUBLISHED);

        List<Boolean> expected = List.of(false,false,false,false,true);

        assertEquals(expected,comparisons);

    }

}
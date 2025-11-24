package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class DicoVarTest {

    @Test
    void shouldCheckDicoVarAreUniqueness(){
        List<DicoVar> actual = List.of(DicoVar.CONCIS,DicoVar.CONCIS_AVEC_EXPRESSION,DicoVar.SCINDABLE,DicoVar.NON_SCINDABLE);
        SortedSet<DicoVar> set = new TreeSet<>();
        set.addAll(actual);
        assertEquals( set.size(),actual.size());
    }

}
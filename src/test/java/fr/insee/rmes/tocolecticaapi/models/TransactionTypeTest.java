package fr.insee.rmes.tocolecticaapi.models;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class TransactionTypeTest {

    @Test
    void shouldCheckTransactionTypeAreUniqueness(){
        List<TransactionType> actual = List.of(TransactionType.COMMITASLATESTWITHLATESTCHILDRENANDPROPAGATEVERSIONS,TransactionType.COPYCOMMIT);
        SortedSet<TransactionType> set = new TreeSet<>();
        set.addAll(actual);
        assertEquals(set.size(),actual.size());
    }
}
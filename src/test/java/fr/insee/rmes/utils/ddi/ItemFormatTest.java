package fr.insee.rmes.utils.ddi;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ItemFormatTest {

    @Test
    void shouldCreateItemFormat(){
        List<String> itemFormatNameStandard = List.of(
                ItemFormat.DDI_31.toString(),
                ItemFormat.DDI_32.toString()
        );

        List<String> nameStandard = List.of(
                "34F5DC49-BE0C-4919-9FC2-F84BE994FA34",
                "C0CA1BD4-1839-4233-A5B5-906DA0302B89");

        assertEquals(nameStandard,itemFormatNameStandard);

    }

}
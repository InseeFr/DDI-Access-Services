package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CodeListTest {

    @Test
    void shouldTestToString() {

        Code code2025 = new Code();
        code2025.setLevelNumber(2025);
        code2025.setValue("valueExample");
        code2025.setDiscrete(true);

        Code code2026 = new Code();
        code2026.setLevelNumber(2026);
        code2026.setValue("valueExample");
        code2026.setDiscrete(true);

        List<Code> codeList = List.of(code2025,code2026);

        CodeList codeListExample = new CodeList();
        codeListExample.setUniversallyUnique(true);
        codeListExample.setCodeList(codeList);

        assertTrue(codeListExample.toString().contains(String.valueOf(codeListExample.getCodeList())) &&
                codeListExample.toString().contains(String.valueOf(codeListExample.isUniversallyUnique()))
        );

    }
}
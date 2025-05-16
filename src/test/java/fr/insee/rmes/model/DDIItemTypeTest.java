package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DDIItemTypeTest {

    @Test
    void shouldSearchByUUID() {
        List<String> uuids = List.of("31E8515B-C0CC-4E88-9E00-AE4BB6D4AC25","1C11DE94-A36D-4D80-95DC-950C6F37F624","a51e85bb-6259-4488-8df2-f08cb43485f8");
        List<String> actual = new ArrayList<>();
        uuids.forEach(uuid ->actual.add(DDIItemType.searchByUUID(uuid).getName()));
        assertEquals("[NCubeScheme, CategoryScheme, PhysicalInstance]",actual.toString());
    }

    @Test
    void shouldSearchByName() {
        List<String> names = List.of("Category","StudyUnit","UniverseScheme");
        List<String> actual = new ArrayList<>();
        names.forEach(name ->actual.add(DDIItemType.searchByName(name).getUUID()));
        assertEquals("[7E47C269-BCAB-40F7-A778-AF7BBC4E3D00, 30EA0200-7121-4F01-8D21-A931A182B86D, 101F901A-2C28-4931-88D6-8F80B36D5650]",actual.toString());
    }

}
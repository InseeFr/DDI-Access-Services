package fr.insee.rmes.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PhysicalInstanceTest {

    @Test
    void shouldTestToString() {

        PhysicalInstance physicalInstance= new PhysicalInstance(null,null,null,null,null,null,null,null,null);

        physicalInstance.setId("id");
        physicalInstance.setUrn("urn");
        physicalInstance.setDataFileVersion("dataFileVersion");
        physicalInstance.setDataFileLocations(List.of("A","B"));
        physicalInstance.setGrossFileStructure("grossFileStructure");
        physicalInstance.setByteOrder("byteOrder");
        physicalInstance.setStatisticalSummary("statisticalSummary");
        physicalInstance.setSoftwareUsed("softwareUsed");
        physicalInstance.setQualityStatement("qualityStatement");

        assertTrue(
                physicalInstance.toString().contains(physicalInstance.getId()) &&
                        physicalInstance.toString().contains(physicalInstance.getUrn()) &&
                        physicalInstance.toString().contains(physicalInstance.getDataFileVersion()) &&
                        physicalInstance.toString().contains(String.valueOf(physicalInstance.getDataFileLocations())) &&
                        physicalInstance.toString().contains(physicalInstance.getGrossFileStructure()) &&
                        physicalInstance.toString().contains(physicalInstance.getByteOrder()) &&
                        physicalInstance.toString().contains(physicalInstance.getStatisticalSummary()) &&
                        physicalInstance.toString().contains(physicalInstance.getSoftwareUsed()) &&
                        physicalInstance.toString().contains(physicalInstance.getQualityStatement()));

    }
}
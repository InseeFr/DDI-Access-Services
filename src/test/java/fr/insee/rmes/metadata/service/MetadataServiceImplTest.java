package fr.insee.rmes.metadata.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetadataServiceImplTest {

    @Test
    void shouldReturnListOfUnits() throws Exception {

        MetadataServiceImpl metadataServiceImpl = new MetadataServiceImpl();

        String actual =metadataServiceImpl.getUnits().toString();
        String expected = "[Unit [uri=http://id.insee.fr/unit/euro, label=€], Unit [uri=http://id.insee.fr/unit/keuro, label=k€], Unit [uri=http://id.insee.fr/unit/percent, label=%], Unit [uri=http://id.insee.fr/unit/heure, label=heures], Unit [uri=http://id.insee.fr/unit/jour, label=jours], Unit [uri=http://id.insee.fr/unit/semaine, label=semaines], Unit [uri=http://id.insee.fr/unit/mois, label=mois], Unit [uri=http://id.insee.fr/unit/annee, label=années], Unit [uri=http://id.insee.fr/unit/an, label=ans], Unit [uri=http://id.insee.fr/unit/watt, label=W], Unit [uri=http://id.insee.fr/unit/kilowatt, label=kW], Unit [uri=http://id.insee.fr/unit/megawatt, label=MW], Unit [uri=http://id.insee.fr/unit/megawattheurepcs, label=MWh PCS], Unit [uri=http://id.insee.fr/unit/megawattheure, label=MWh], Unit [uri=http://id.insee.fr/unit/megawattpcs, label=MW PCS], Unit [uri=http://id.insee.fr/unit/kilowattthermique, label=kWth], Unit [uri=http://id.insee.fr/unit/kg, label=kg], Unit [uri=http://id.insee.fr/unit/tonne, label=tonnes], Unit [uri=http://id.insee.fr/unit/tonnematiereseche, label=tonnes matières sèches], Unit [uri=http://id.insee.fr/unit/degrecelsius, label=°C], Unit [uri=http://id.insee.fr/unit/bar, label=bars], Unit [uri=http://id.insee.fr/unit/litre, label=litres], Unit [uri=http://id.insee.fr/unit/metre, label=mètres], Unit [uri=http://id.insee.fr/unit/centimetre, label=centimètres], Unit [uri=http://id.insee.fr/unit/metrecarre, label=mètres carrés]]";

        assertEquals(expected,actual);
    }

}
package fr.insee.rmes.exceptions;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import static org.junit.jupiter.api.Assertions.*;

class ErrorCodesTest {

    @Test
    void shouldCheckIntegerErrorCodesAreNotUniqueness(){

        List<Integer> actual = List.of(
                ErrorCodes.CONCEPT_CREATION_RIGHTS_DENIED,
                ErrorCodes.CONCEPT_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.CONCEPT_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.CONCEPT_MAILING_RIGHTS_DENIED,
                ErrorCodes.CONCEPT_DELETION_SEVERAL_GRAPHS,
                ErrorCodes.CONCEPT_DELETION_LINKED,
                ErrorCodes.INDICATOR_CREATION_RIGHTS_DENIED,
                ErrorCodes.INDICATOR_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.INDICATOR_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.DOCUMENT_CREATION_RIGHTS_DENIED,
                ErrorCodes.DOCUMENT_CREATION_EXISTING_FILE,
                ErrorCodes.DOCUMENT_DELETION_LINKED,
                ErrorCodes.LINK_CREATION_RIGHTS_DENIED,
                ErrorCodes.LINK_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.FAMILY_CREATION_RIGHTS_DENIED,
                ErrorCodes.FAMILY_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.FAMILY_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.SERIES_CREATION_RIGHTS_DENIED,
                ErrorCodes.SERIES_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.SERIES_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.SERIES_VALIDATION_UNPUBLISHED_FAMILY,
                ErrorCodes.OPERATION_CREATION_RIGHTS_DENIED,
                ErrorCodes.OPERATION_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.OPERATION_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.OPERATION_VALIDATION_UNPUBLISHED_SERIES,
                ErrorCodes.SIMS_CREATION_RIGHTS_DENIED,
                ErrorCodes.SIMS_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.SIMS_VALIDATION_RIGHTS_DENIED ,
                ErrorCodes.SIMS_VALIDATION_UNPUBLISHED_TARGET ,
                ErrorCodes.SIMS_DELETION_RIGHTS_DENIED,
                ErrorCodes.COLLECTION_CREATION_RIGHTS_DENIED,
                ErrorCodes.COLLECTION_MODIFICATION_RIGHTS_DENIED,
                ErrorCodes.COLLECTION_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.COLLECTION_MAILING_RIGHTS_DENIED,
                ErrorCodes.COMPONENT_FORBIDDEN_DELETE,
                ErrorCodes.COMPONENT_UNICITY,
                ErrorCodes.STRUCTURE_UNICITY,
                ErrorCodes.COMPONENT_PUBLICATION_EMPTY_CREATOR,
                ErrorCodes.COMPONENT_PUBLICATION_EMPTY_STATUS,
                ErrorCodes.COMPONENT_PUBLICATION_VALIDATED_CONCEPT ,
                ErrorCodes.COMPONENT_PUBLICATION_VALIDATED_CODESLIST ,
                ErrorCodes.STRUCTURE_PUBLICATION_VALIDATED_COMPONENT,
                ErrorCodes.CLASSIFICATION_VALIDATION_RIGHTS_DENIED,
                ErrorCodes.CONCEPT_UNKNOWN_ID,
                ErrorCodes.INDICATOR_UNKNOWN_ID,
                ErrorCodes.DOCUMENT_UNKNOWN_ID,
                ErrorCodes.LINK_UNKNOWN_ID,
                ErrorCodes.FAMILY_UNKNOWN_ID,
                ErrorCodes.FAMILY_INCORRECT_BODY,
                ErrorCodes.SERIES_UNKNOWN_ID,
                ErrorCodes.SERIES_UNKNOWN_FAMILY,
                ErrorCodes.OPERATION_UNKNOWN_ID,
                ErrorCodes.OPERATION_UNKNOWN_SERIES,
                ErrorCodes.SIMS_UNKNOWN_ID,
                ErrorCodes.SIMS_UNKNOWN_TARGET,
                ErrorCodes.GEOFEATURE_UNKNOWN ,
                ErrorCodes.GEOFEATURE_INCORRECT_BODY,
                ErrorCodes.GEOFEATURE_EXISTING_LABEL,
                ErrorCodes.CLASSIFICATION_UNKNOWN_ID,
                ErrorCodes.CLASSIFICATION_INCORRECT_BODY ,
                ErrorCodes.DISTRIUBTION_PATCH_INCORRECT_BODY,
                ErrorCodes.DATASET_PATCH_INCORRECT_BODY,
                ErrorCodes.DISTRIBUTION_DELETE_ONLY_UNPUBLISHED,
                ErrorCodes.DATASET_DELETE_ONLY_UNPUBLISHED ,
                ErrorCodes.DATASET_DELETE_ONLY_WITHOUT_DISTRIBUTION,
                ErrorCodes.DATASET_DELETE_ONLY_WITHOUT_DERIVED_DATASET,
                ErrorCodes.DOCUMENT_EMPTY_NAME,
                ErrorCodes.DOCUMENT_FORBIDDEN_CHARACTER_NAME,
                ErrorCodes.LINK_EMPTY_URL,
                ErrorCodes.LINK_EXISTING_URL,
                ErrorCodes.LINK_BAD_URL,
                ErrorCodes.SERIES_OPERATION_OR_SIMS,
                ErrorCodes.SIMS_INCORRECT,
                ErrorCodes.SIMS_DELETION_FOR_NON_SERIES,
                ErrorCodes.SIMS_EXPORT_WITHOUT_LANGUAGE
        );

        SortedSet<Integer> set = new TreeSet<>();
        set.addAll(actual);
        boolean existDuplicates = set.size()!= actual.size();
        boolean existSameCode = ErrorCodes.DISTRIBUTION_DELETE_ONLY_UNPUBLISHED==ErrorCodes.DATASET_DELETE_ONLY_UNPUBLISHED;

        assertTrue( existDuplicates && existSameCode);

    }

    @Test
    void shouldCheckStringErrorCodesAreUniqueness(){

        List<String> actual = List.of(
                ErrorCodes.OPERATION_FAMILY_EXISTING_PREF_LABEL_LG1,
                ErrorCodes.OPERATION_FAMILY_EXISTING_PREF_LABEL_LG2,
                ErrorCodes.OPERATION_DOCUMENT_LINK_EXISTING_LABEL_LG1,
                ErrorCodes.OPERATION_DOCUMENT_LINK_EXISTING_LABEL_LG2,
                ErrorCodes.OPERATION_SERIES_EXISTING_PREF_LABEL_LG1,
                ErrorCodes.OPERATION_SERIES_EXISTING_PREF_LABEL_LG2,
                ErrorCodes.OPERATION_OPERATION_EXISTING_PREF_LABEL_LG1,
                ErrorCodes.OPERATION_OPERATION_EXISTING_PREF_LABEL_LG2,
                ErrorCodes.OPERATION_INDICATOR_EXISTING_PREF_LABEL_LG1,
                ErrorCodes.OPERATION_INDICATOR_EXISTING_PREF_LABEL_LG2
        );

        SortedSet<String> set = new TreeSet<>();
        set.addAll(actual);
        boolean existDuplicates = set.size()== actual.size();
        assertTrue( existDuplicates );

    }

}

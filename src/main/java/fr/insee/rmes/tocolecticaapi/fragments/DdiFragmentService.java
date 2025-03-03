package fr.insee.rmes.tocolecticaapi.fragments;

import fr.insee.rmes.exceptions.RmesException;

public interface DdiFragmentService {
    /**
     * Retourne une projection json de l'objet datarelationship du fragment DDI dont l'id est passé en paramètre.
     * Le DDI fragment est fourni en interrogeant colectica
     * @param uuid : l'id du DDI fragment dont on doit extraire le datarelationship
     * @return une chaine de caractères représentant le  datarelationship
     */
    String extractDataRelationship(String uuid) throws RmesException;
}

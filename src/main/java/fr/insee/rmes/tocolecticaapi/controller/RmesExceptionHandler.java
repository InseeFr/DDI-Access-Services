package fr.insee.rmes.tocolecticaapi.controller;

import fr.insee.rmes.exceptions.RmesException;
import fr.insee.rmes.exceptions.RmesExceptionIO;
import fr.insee.rmes.transfoxsl.controller.TransformationController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice(assignableTypes = {DeleteItem.class, GetItem.class, PostItem.class, TransformationController.class})
public class RmesExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RmesException.class})
    public final ResponseEntity<String> handleRmesException(RmesException exception){
        logger.error(exception.getMessageAndDetails(), exception);
        return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }

    @ExceptionHandler({RmesExceptionIO.class})
    public final ResponseEntity<String> handleRmesExceptionIO(RmesExceptionIO exception){
        logger.error(exception.toRestMessage().getDetails(), exception);
        return ResponseEntity.status(exception.toRestMessage().getStatus()).body(exception.getMessage());
    }

}

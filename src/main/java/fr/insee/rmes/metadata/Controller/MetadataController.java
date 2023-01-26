package fr.insee.rmes.metadata.Controller;


import fr.insee.rmes.metadata.model.Unit;
import fr.insee.rmes.metadata.service.MetadataService;
import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import jakarta.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meta-data")
public class MetadataController {

    @Autowired
    private MetadataService metadataService;

    @GetMapping("/item/{id}/rp/{resourcePackageId}/deref-ddi")
    public  String getDerefDDIDocumentWithExternalRP(@PathVariable String id, @PathVariable String resourcePackageId) throws Exception{
        return metadataService.getDerefDDIDocumentWithExternalRP(id,resourcePackageId);
    }


    @GetMapping("/item/{id}/deref-ddi")
    public String getDerefDDIDocument(@PathVariable String id) throws Exception{
        return metadataService.getDerefDDIDocument(id);
    }

    @GetMapping()
    public String getDDIDocument(@PathVariable String id) throws Exception{
        return metadataService.getDDIDocument(id);
    }

    @GetMapping("/sequence/{id}/ddi")
    public String getSequence(@PathVariable String id) throws Exception{
        return metadataService.getSequence(id);
    }


    @GetMapping("/question/{id}/ddi")
    public String getQuestion(@PathVariable String id) throws Exception{
        return metadataService.getQuestion(id);
    }

    @GetMapping("/units")
    public List<Unit> getUnits() throws Exception{
        return metadataService.getUnits();
    }

    @GetMapping("/ddi-instance/{id}/ddi")
    public String getDDIInstance(@PathVariable String id) throws Exception{
        return metadataService.getDDIInstance(id);
    }



}

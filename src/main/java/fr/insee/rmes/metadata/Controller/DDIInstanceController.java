package fr.insee.rmes.metadata.Controller;


import fr.insee.rmes.metadata.service.ddiinstance.DDIInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meta-data")
public class DDIInstanceController {

    @Autowired
    private DDIInstanceService ddiInstanceService;

    @GetMapping()
    public  String getDDIInstance(@PathVariable String id) throws Exception{
        return ddiInstanceService.getDDIInstance(id);
    }


}

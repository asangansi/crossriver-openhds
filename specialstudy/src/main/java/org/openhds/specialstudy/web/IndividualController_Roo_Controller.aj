package org.openhds.specialstudy.web;

import java.lang.Long;
import java.lang.String;
import org.openhds.specialstudy.domain.Individual;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect IndividualController_Roo_Controller {
    
    @RequestMapping(value = "/individual/form", method = RequestMethod.GET)
    public String IndividualController.createForm(ModelMap modelMap) {
        modelMap.addAttribute("individual", new Individual());
        return "individual/create";
    }
    
    @RequestMapping(value = "/individual/{id}", method = RequestMethod.GET)
    public String IndividualController.show(@PathVariable("id") Long id, ModelMap modelMap) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        modelMap.addAttribute("individual", Individual.findIndividual(id));
        return "individual/show";
    }
    
    @RequestMapping(value = "/individual", method = RequestMethod.GET)
    public String IndividualController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            modelMap.addAttribute("individuals", Individual.findIndividualEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Individual.countIndividuals() / sizeNo;
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            modelMap.addAttribute("individuals", Individual.findAllIndividuals());
        }
        return "individual/list";
    }
    
    @RequestMapping(value = "/individual/{id}", method = RequestMethod.DELETE)
    public String IndividualController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        Individual.findIndividual(id).remove();
        return "redirect:/individual?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }
    
}

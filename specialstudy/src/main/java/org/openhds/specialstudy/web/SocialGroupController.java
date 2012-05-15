package org.openhds.specialstudy.web;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.apache.cxf.jaxrs.client.WebClient;
import org.openhds.specialstudy.domain.SocialGroup;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "socialgroup", automaticallyMaintainView = false, update = true, formBackingObject = SocialGroup.class)
@RequestMapping("/socialgroup/**")
@SessionAttributes({"extId", "fromFilteredList"})
@Controller
public class SocialGroupController {
	
	@Autowired
	WebClient webClient;
				
	@RequestMapping(value = "/socialgroup", method = RequestMethod.POST)
    public String create(@Valid SocialGroup socialGroup, BindingResult result, ModelMap modelMap) {
		
		Response response;
		webClient.reset();
		
		try {
			response = webClient.path("/socialgroup/{id}", socialGroup.getHouseholdId().trim()).get();
		} catch (Exception e) {
			setFieldsOnForm(modelMap);
			ObjectError oe = new ObjectError("socialgroup", "Unable to connect to the OpenHDS web service.");
        	result.addError(oe);
            modelMap.addAttribute("socialgroup", socialGroup);
            return "socialgroup/create";
		}
			
        if (response.getStatus() != 200) {
            setFieldsOnForm(modelMap);
        	ObjectError oe = new ObjectError("socialgroup", "The request could not be fullfilled. Ensure that the household id is valid.");
        	result.addError(oe);
            modelMap.addAttribute("socialGroup", socialGroup);
            return "socialgroup/create";
        }
        socialGroup.persist(); 
        return "redirect:/socialgroup/" + socialGroup.getId();
    }
    
    @RequestMapping(value = "/socialgroup/form", method = RequestMethod.GET)
    public String createForm(ModelMap modelMap, SessionStatus sessionStatus) {
        setFieldsOnForm(modelMap);
        sessionStatus.setComplete();
        modelMap.addAttribute("socialGroup", new SocialGroup());
        return "socialgroup/create";
    }
       
    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid SocialGroup socialGroup, BindingResult result, ModelMap modelMap) {
        SocialGroup sg = SocialGroup.findSocialGroup(socialGroup.getId());
        socialGroup.setHouseholdId(sg.getHouseholdId());
        socialGroup.merge();
        return "redirect:/socialgroup/" + socialGroup.getId();
    }
    
    @RequestMapping(value = "/socialgroup/{id}/form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, ModelMap modelMap) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        setFieldsOnForm(modelMap);
        modelMap.addAttribute("socialGroup", SocialGroup.findSocialGroup(id));
        return "socialgroup/update";
    }
    
    @RequestMapping(value = "/socialgroup/list", method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap, SessionStatus sessionStatus) {
    	sessionStatus.setComplete();
    	if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            modelMap.addAttribute("socialgroups", SocialGroup.findSocialGroupEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) SocialGroup.countSocialGroups() / sizeNo;
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            modelMap.addAttribute("socialgroups", SocialGroup.findAllSocialGroups());
        }
        return "socialgroup/list";
    }
    
    @RequestMapping(value = "/socialgroup/{id}/list", method = RequestMethod.GET)
    public String list(@PathVariable("id") String id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {
    	modelMap.addAttribute("fromFilteredList", true);
    	modelMap.addAttribute("extId", id);
    	if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            modelMap.addAttribute("socialgroups", SocialGroup.findSocialGroupEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) SocialGroup.countSocialGroups() / sizeNo;
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            modelMap.addAttribute("socialgroups", SocialGroup.findAllSocialGroupsByHouseholdId(id));
        }
        return "socialgroup/list";
    }
    
    @RequestMapping(value = "/socialgroup/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, ModelMap modelMap) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        modelMap.addAttribute("socialGroup", SocialGroup.findSocialGroup(id));
        return "socialgroup/show";
    }
    
    @RequestMapping(value = "/socialgroup/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        if (id == null) throw new IllegalArgumentException("An Identifier is required");
        SocialGroup.findSocialGroup(id).remove();
        return "redirect:/socialgroup/list";
    }
    
    private void setFieldsOnForm(ModelMap modelMap) {
    	  List<String> drinkingWater = new ArrayList<String>();
          drinkingWater.add("Taps");
          drinkingWater.add("Tanks");
          drinkingWater.add("Hawkers");
          drinkingWater.add("Bottled/Satchet Water");
          drinkingWater.add("Residence/Compound");
          drinkingWater.add("Public Tap");
          drinkingWater.add("Well on Residence/Comp");
          drinkingWater.add("Public Well");
          drinkingWater.add("Bore Hole");
          drinkingWater.add("River/Stream");
          drinkingWater.add("Dam/Pond/Lake");
          drinkingWater.add("Rainwater");
          modelMap.addAttribute("drinkingWater", drinkingWater);
          
          List<String> toiletFacility = new ArrayList<String>();
          toiletFacility.add("Own Flush Toilet");
          toiletFacility.add("Shared Flush Toilet");
          toiletFacility.add("Own Traditional Pit Toilet");
          toiletFacility.add("Shared Traditional Pit Toilet");
          toiletFacility.add("Own (VIP) Latrine");
          toiletFacility.add("Shared (VIP) Latrine");
          toiletFacility.add("Bucket/Pan");
          toiletFacility.add("No Facility/Bush/Field");
          modelMap.addAttribute("toiletFacility", toiletFacility);
          
          List<String> floorMaterial = new ArrayList<String>();
          floorMaterial.add("Mud/Sand/Gravel");
          floorMaterial.add("Wood Planks");
          floorMaterial.add("Polished Wood/Tiles/Terrazzo");
          floorMaterial.add("Cement");
          floorMaterial.add("Carpet");
          modelMap.addAttribute("floorMaterial", floorMaterial);
     
          List<String> roofMaterial = new ArrayList<String>();
          roofMaterial.add("Mud Roof");
          roofMaterial.add("Grass/Thatch");
          roofMaterial.add("Ironsheet/Zinc");
          roofMaterial.add("Tiles");
          modelMap.addAttribute("roofMaterial", roofMaterial);
       
          List<String> wallMaterial = new ArrayList<String>();
          wallMaterial.add("Bricks (Mud)");
          wallMaterial.add("Cement Blocks");
          wallMaterial.add("Ironsheets (Zinc)");
          wallMaterial.add("Wood/Boards");
          wallMaterial.add("Grass/Stocks");
          modelMap.addAttribute("wallMaterial", wallMaterial);

          List<String> cookingFuel = new ArrayList<String>();
          cookingFuel.add("Kerosene/Paraffin");
          cookingFuel.add("Gas");
          cookingFuel.add("Electricity");
          cookingFuel.add("Charcoal");
          cookingFuel.add("Firewood");
          cookingFuel.add("Animal Waste");
          cookingFuel.add("Crop Residue/Saw Dust");
          modelMap.addAttribute("cookingFuel", cookingFuel);
          
          List<String> lightingSource = new ArrayList<String>();
          lightingSource.add("Kerosene/Paraffin");
          lightingSource.add("Gas");
          lightingSource.add("Electricity");
          lightingSource.add("Solar");
          lightingSource.add("Candles");
          lightingSource.add("Firewood");
          modelMap.addAttribute("lightingSource", lightingSource);
          
          List<String> landOwnership = new ArrayList<String>();
          landOwnership.add("Landlord");
          landOwnership.add("Public/Government Land");
          landOwnership.add("Self/Family Owned");
          landOwnership.add("Don't Know");
          modelMap.addAttribute("landOwnership", landOwnership);
          
          List<String> tenure = new ArrayList<String>();
          tenure.add("Purchased");
          tenure.add("Constructed");
          tenure.add("Inherited");
          tenure.add("Individual");
          tenure.add("Government");
          tenure.add("Local Authority");
          tenure.add("Private Company");
          tenure.add("Free of Charge");
          tenure.add("Don't Know");
          modelMap.addAttribute("tenure", tenure);
          
          List<String> waste = new ArrayList<String>();
          waste.add("Garbage Dump");
          waste.add("In the River");
          waste.add("On the Road");
          waste.add("In Drainage/Trench");
          waste.add("In Private Pits");
          waste.add("In Public Pits");
          waste.add("Garbage Disposal Services");
          waste.add("Vacant/Abandoned House");
          waste.add("Burning");
          waste.add("No Designated Place/All Over");
          modelMap.addAttribute("waste", waste);
          
          List<String> owland = new ArrayList<String>();
          owland.add("Yes");
          owland.add("No");
          owland.add("Don't Know");
          modelMap.addAttribute("owland", owland);
          
          List<String> enland = new ArrayList<String>();
          enland.add("Yes");
          enland.add("No");
          enland.add("N/A");
          modelMap.addAttribute("enland", enland);
          
          List<String> enfood = new ArrayList<String>();
          enfood.add("Yes");
          enfood.add("No");
          enfood.add("Don't Know");
          modelMap.addAttribute("enfood", enfood);
          
          List<String> faid = new ArrayList<String>();
          faid.add("Bought from the Market");
          faid.add("Food from Relatives");
          faid.add("Managed with Stock");
          faid.add("Assistance from Friends");
          faid.add("Government/NGO Aid");
          faid.add("N/A");
          modelMap.addAttribute("faid", faid);
          
          List<String> mstaple = new ArrayList<String>();
          mstaple.add("Garri");
          mstaple.add("Yam");
          mstaple.add("Rice");
          mstaple.add("Maize");
          mstaple.add("Potatoes");
          mstaple.add("Beans");
          modelMap.addAttribute("mstaple", mstaple);
          
          List<String> meals = new ArrayList<String>();
          meals.add("One");
          meals.add("Two");
          meals.add("Three");
          meals.add("Four or more");
          modelMap.addAttribute("meals", meals);

          List<String> adfood = new ArrayList<String>();
          adfood.add("Yes");
          adfood.add("No");
          adfood.add("Don't Know");
          modelMap.addAttribute("adfood", adfood);      
    }
}

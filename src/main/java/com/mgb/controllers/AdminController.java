package com.mgb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgb.to.CustomSubscriber;
import com.utiljar.bo.AreaMaster;
import com.utiljar.bo.Subscriber;
import com.utiljar.model.AreaMasterDto;
import com.utiljar.model.GlobalResponse;
import com.utiljar.util.ApplicationUtils;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class AdminController
{
  @Autowired
  private ApplicationContext appContext;
  @Autowired
  RestTemplate restTemplate;
  
  @RequestMapping({"/admin/userInfo"})
  public String getAllUsersInfo(Model model, @RequestParam(value="generateReport", required=false) String generateReport)
  {
    try
    {
      model.addAttribute("userList", getAllSubscribers());
      if ((generateReport != null) && (generateReport.equalsIgnoreCase("true"))) {
        return "generateReportUserInfo";
      }
      return "userInfo";
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public List<Subscriber> getAllSubscribers()
    throws Exception
  {
    String respJson = (String)this.restTemplate.getForObject("http://localhost:8082/cable/subscribers/list?tenantId=1", String.class, new Object[0]);
    return ApplicationUtils.getResponseBoList(respJson, new Subscriber());
  }
  
  @RequestMapping({"/admin"})
  public String welcomeAdmin(Model model)
  {
    return "adminPage";
  }
  
  @RequestMapping({"admin/addSubscriber"})
  public String openRegisterPage(Model model)
  {
    try
    {
      CustomSubscriber subscriber = new CustomSubscriber();
      model.addAttribute("subscriber", subscriber);
      model.addAttribute("areaMap", getAreaList());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "addSubscriber";
  }
  
  public List<AreaMaster> getAreaList()
    throws Exception
  {
    String respJson = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/getAll?tenantId=1", String.class, new Object[0]);
    List<AreaMaster> areaList = ApplicationUtils.getResponseBoList(respJson, new AreaMaster());
    return areaList;
  }
  
  @RequestMapping({"admin/saveSubscriber"})
  public String registration(@ModelAttribute("subscriber") CustomSubscriber subscriber, BindingResult result, Model model)
    throws Exception
  {
    try
    {
      if ((subscriber.getAreaMaster() != null) && ((subscriber.getAreaMaster().getId() == null) || (subscriber.getAreaMaster().getId().intValue() == 0))) {
        subscriber.setAreaMaster(null);
      }
      model.addAttribute("subscriber", subscriber);
      ObjectMapper mapper = new ObjectMapper();
      String jsonInString = mapper.writeValueAsString(subscriber);
      
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<String> entity = new HttpEntity(jsonInString, headers);
      
      String uri = "http://localhost:8082/cable/subscriber/add?tenantId=1";
      ResponseEntity<GlobalResponse> response = this.restTemplate.exchange(uri, HttpMethod.POST, entity, GlobalResponse.class, new Object[0]);
      GlobalResponse globalResponse = (GlobalResponse)response.getBody();
      model.addAttribute("userList", getAllSubscribers());
      if (globalResponse.getStatusCode() == 2000)
      {
        model.addAttribute("message", globalResponse.getStatusMessage());
        return "userInfo";
      }
      model.addAttribute("error", globalResponse.getStatusMessage());
      return "addSubscriber";
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  @RequestMapping({"admin/openEditSubscriber"})
  public String openEditSubscriber(Model model, @RequestParam(value="userId", required=false) String userId)
  {
    try
    {
      String respJson = (String)this.restTemplate.getForObject("http://localhost:8082/cable/subscribers/" + userId + "?tenantId=" + 1, String.class, new Object[0]);
      CustomSubscriber subscriber = (CustomSubscriber)ApplicationUtils.getResponseBo(respJson, new CustomSubscriber());
      model.addAttribute("subscriber", subscriber);
      model.addAttribute("areaMap", getAreaList());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "addSubscriber";
  }
  
  @RequestMapping({"admin/deleteSubscriber"})
  public String deleteSubscriber(Model model, @RequestParam(value="userId", required=false) String userId)
  {
    try
    {
      model.addAttribute("error", "This option is not available yet,");
      model.addAttribute("userList", getAllSubscribers());
    }
    catch (Exception localException) {}
    return "userInfo";
  }
  
  @RequestMapping({"admin/openAreaMaster"})
  public String listAllAreas(Model model)
  {
    try
    {
      String respJson = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/getAll?tenantId=1", String.class, new Object[0]);
      List<AreaMaster> areaList = ApplicationUtils.getResponseBoList(respJson, new AreaMaster());
      
      model.addAttribute("areaList", areaList);
      AreaMasterDto dto = new AreaMasterDto();
      model.addAttribute("areaMasterDto", dto);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "areaMaster";
  }
  
  @RequestMapping({"admin/areaMaster/add"})
  public String addPaymentDef(Model model, @Valid @ModelAttribute("areaMasterDto") AreaMasterDto areaMasterDto, BindingResult result)
  {
    try
    {
      if (!result.hasErrors())
      {
        ObjectMapper mapper = new ObjectMapper();
        areaMasterDto.setTenantId(1);
        String jsonInString = mapper.writeValueAsString(areaMasterDto);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity(jsonInString, headers);
        
        String uri = "http://localhost:8082/admin/areaMaster/add";
        ResponseEntity<GlobalResponse> response = this.restTemplate.exchange(uri, HttpMethod.POST, entity, GlobalResponse.class, new Object[0]);
        GlobalResponse globalResponse = (GlobalResponse)response.getBody();
        if (globalResponse.getStatusCode() == 2000) {
          model.addAttribute("message", globalResponse.getStatusMessage());
        } else {
          model.addAttribute("error", globalResponse.getStatusMessage());
        }
      }
      String respJson1 = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/getAll?tenantId=1", String.class, new Object[0]);
      List<AreaMaster> areaList = ApplicationUtils.getResponseBoList(respJson1, new AreaMaster());
      model.addAttribute("areaList", areaList);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "areaMaster";
  }
  
  @RequestMapping({"admin/areaMaster/edit"})
  public String editArea(Model model, @RequestParam(value="boId", required=false) String boId)
  {
    try
    {
      String respJson = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/get?id=" + boId + "&tenantId=" + 1, String.class, new Object[0]);
      AreaMaster areaMaster = (AreaMaster)ApplicationUtils.getResponseBo(respJson, new AreaMaster());
      model.addAttribute("areaMasterDto", areaMaster);
      
      String respJson1 = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/getAll?tenantId=1", String.class, new Object[0]);
      List<AreaMaster> areaList = ApplicationUtils.getResponseBoList(respJson1, new AreaMaster());
      model.addAttribute("areaList", areaList);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "areaMaster";
  }
  
  @RequestMapping({"admin/areaMaster/delete"})
  public String deletePaymentDef(Model model, @ModelAttribute("areaMasterDto") AreaMasterDto areaMasterDto, @RequestParam(value="boId", required=false) String boId)
  {
    try
    {
      GlobalResponse globalResponse = (GlobalResponse)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/delete?id=" + boId + "&tenantId=1", GlobalResponse.class, new Object[0]);
      if (globalResponse.getStatusCode() == 2000) {
        model.addAttribute("message", globalResponse.getStatusMessage());
      } else {
        model.addAttribute("error", globalResponse.getStatusMessage());
      }
      String respJson1 = (String)this.restTemplate.getForObject("http://localhost:8082/admin/areaMaster/getAll?tenantId=1", String.class, new Object[0]);
      List<AreaMaster> areaList = ApplicationUtils.getResponseBoList(respJson1, new AreaMaster());
      model.addAttribute("areaList", areaList);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "areaMaster";
  }
  
  @RequestMapping({"admin/uploadSubscriberPage"})
  public String uploadSubscriberPage(Model model)
  {
    try
    {
      model.addAttribute("test", null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "uploadSubscriberPage";
  }
  
  @RequestMapping({"admin/uploadSubscriberFile"})
  public String uploadSubscriberFile(Model model, @RequestParam("file") MultipartFile file)
  {
    try
    {
      model.addAttribute("test", null);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return "uploadSubscriberPage";
  }
}

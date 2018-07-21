package com.mgb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utiljar.bo.PaymentDefinition;
import com.utiljar.model.GlobalResponse;
import com.utiljar.model.PaymentDefDTO;
import com.utiljar.model.PaymentDetailsDTO;
import com.utiljar.util.ApplicationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Controller
@SessionAttributes(value={"paymentDto"})
public class PaymentController {
    @Autowired
    RestTemplate restTemplate;

    @RequestMapping(value={"admin/openPaymentPage"})
    public String showMonthlyPaymentPage(Model model, @RequestParam(value="userId", required=false) String userId) {
        try {
            model.addAttribute("paymentDto", (Object)this.getPaymentDetailsDTO(userId, 1));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "openPayment";
    }

    public PaymentDetailsDTO getPaymentDetailsDTO(String subId, int tenantId) throws Exception {
        String respJson = (String)this.restTemplate.getForObject("http://localhost:8083/payment/monthlyPaymentDetails?subscriberId=" + subId + "&tenantId=" + tenantId, String.class, new Object[0]);
        PaymentDetailsDTO paymentDetailsDTO = (PaymentDetailsDTO)ApplicationUtils.getResponseBo((String)respJson, (Object)new PaymentDetailsDTO());
        return paymentDetailsDTO;
    }

    @RequestMapping(value={"admin/addMonthlyPayment"})
    public String addMonthlyPayment(Model model, @ModelAttribute(value="paymentDto") PaymentDetailsDTO paymentDto) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString((Object)paymentDto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity entity = new HttpEntity((Object)jsonInString, (MultiValueMap)headers);
            String uri = "http://localhost:8083/payment/addMonthlyPayment?tenantId=1";
            ResponseEntity response = this.restTemplate.exchange(uri, HttpMethod.POST, entity, GlobalResponse.class, new Object[0]);
            GlobalResponse globalResponse = (GlobalResponse)response.getBody();
            if (globalResponse.getStatusCode() == 2000) {
                model.addAttribute("message", (Object)globalResponse.getStatusMessage());
            } else {
                model.addAttribute("error", (Object)globalResponse.getStatusMessage());
            }
            model.addAttribute("paymentDto", (Object)this.getPaymentDetailsDTO(paymentDto.getSubscriberId(), 1));
        }
        catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", (Object)"Fialed to add payment");
        }
        return "openPayment";
    }

    @RequestMapping(value={"admin/openPaymentDefPage"})
    public String listAllPaymentDef(Model model) {
        try {
            model.addAttribute("defList", this.getPaymentDefList());
        }
        catch (Exception e) {
            if (e instanceof ResourceAccessException) {
                model.addAttribute("error", (Object)"Sorry,one of our component is down.");
            }
            e.printStackTrace();
        }
        PaymentDefDTO paymentDefDTO = new PaymentDefDTO();
        model.addAttribute("paymentDefDTO", (Object)paymentDefDTO);
        return "openPaymentDef";
    }

    public List<PaymentDefDTO> getPaymentDefList() throws Exception {
        String respJson = (String)this.restTemplate.getForObject("http://localhost:8083/payment/paymentDef/list?tenantId=1", String.class, new Object[0]);
        List<PaymentDefinition> defList = ApplicationUtils.getResponseBoList((String)respJson,new PaymentDefinition());
        ArrayList<PaymentDefDTO> dtoList = new ArrayList<PaymentDefDTO>();
        defList.forEach(d-> {
            PaymentDefDTO dto = new PaymentDefDTO();
            dto.setId(d.getId());
            dto.setDefMonth(ApplicationUtils.monthsArr[d.getMonth()]);
            dto.setDefYear(String.valueOf(d.getYear()));
            dto.setAmount(String.valueOf(d.getAmount()));
            dtoList.add(dto);
            });
        return dtoList;
    }

    @RequestMapping(value={"admin/paymentDef/add"})
    public String addPaymentDef(Model model, @Valid @ModelAttribute(value="paymentDefDTO") PaymentDefDTO paymentDefDTO, BindingResult result) {
        try {
            if (!result.hasErrors()) {
                ObjectMapper mapper = new ObjectMapper();
                String jsonInString = mapper.writeValueAsString((Object)paymentDefDTO);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity entity = new HttpEntity((Object)jsonInString, (MultiValueMap)headers);
                String uri = "http://localhost:8083/payment/paymentDef/add?tenantId=1";
                ResponseEntity response = this.restTemplate.exchange(uri, HttpMethod.POST, entity, GlobalResponse.class, new Object[0]);
                GlobalResponse globalResponse = (GlobalResponse)response.getBody();
                if (globalResponse.getStatusCode() == 2000) {
                    model.addAttribute("message", (Object)globalResponse.getStatusMessage());
                } else {
                    model.addAttribute("error", (Object)globalResponse.getStatusMessage());
                }
            }
            model.addAttribute("defList", this.getPaymentDefList());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "openPaymentDef";
    }

    @RequestMapping(value={"admin/paymentDef/edit"})
    public String editPaymentDef(Model model, @RequestParam(value="boId", required=false) String boId) {
        try {
            String respJson = (String)this.restTemplate.getForObject("http://localhost:8083/payment/paymentDef/get?id=" + boId + "&tenantId=" + 1, String.class, new Object[0]);
            PaymentDefDTO paymentDefDTO = (PaymentDefDTO)ApplicationUtils.getResponseBo((String)respJson, (Object)new PaymentDefDTO());
            model.addAttribute("paymentDefDTO", (Object)paymentDefDTO);
            model.addAttribute("defList", this.getPaymentDefList());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "openPaymentDef";
    }

    @RequestMapping(value={"admin/paymentDef/delete"})
    public String deletePaymentDef(Model model, @ModelAttribute(value="paymentDefDTO") PaymentDefDTO paymentDefDTO, @RequestParam(value="boId", required=false) String boId) {
        try {
            GlobalResponse globalResponse = (GlobalResponse)this.restTemplate.getForObject("http://localhost:8083/payment/paymentDef/delete?id=" + boId + "&tenantId=1", GlobalResponse.class, new Object[0]);
            if (globalResponse.getStatusCode() == 2000) {
                model.addAttribute("message", (Object)globalResponse.getStatusMessage());
            } else {
                model.addAttribute("error", (Object)globalResponse.getStatusMessage());
            }
            model.addAttribute("defList", this.getPaymentDefList());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "openPaymentDef";
    }
}
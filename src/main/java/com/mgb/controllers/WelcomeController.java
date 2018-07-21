package com.mgb.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/SpringMaven/")
public class WelcomeController
{
  @Autowired
  private ApplicationContext appContext;
  
  @RequestMapping({"/home"})
  public String openWelcomePage()
  {
    return "welcome";
  }
  
  @RequestMapping({"/about"})
  public String openAboutDetails()
  {
    return "about";
  }
  
  @RequestMapping(value={"/login"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String login(@RequestParam(value="error", required=false) String error, @RequestParam(value="username", required=false) String username, Model model, HttpServletRequest request)
  {
    HttpSession session = request.getSession(false);
    if (error != null) {
      model.addAttribute("error", "Invalid username and password!");
    }
    return "login";
  }
  
  @RequestMapping({"/loginCheck"})
  public String loginCheck(Model model, HttpServletRequest request)
  {
    return "loginSuccess";
  }
  
  @RequestMapping({"/loginSuccess"})
  public String loginSuccess(Model model, HttpServletRequest request)
  {
    return "loginSuccess";
  }
  
  @RequestMapping(value={"/logout"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
  public String logoutPage(HttpServletRequest request, HttpServletResponse response, Model model)
  {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null)
    {
      new SecurityContextLogoutHandler().logout(request, response, auth);
      model.addAttribute("msg", "You have successfully loged out.");
    }
    return "login";
  }
}

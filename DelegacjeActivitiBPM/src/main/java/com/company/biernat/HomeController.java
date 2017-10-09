package com.company.biernat;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class HomeController {
	

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Inject
	private ProcessEngine processEngine;

    //Strona główna
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(HttpServletRequest request,Locale locale, Model model) {
        if(request.getSession().getAttribute("login")!=null){
        	return login(request, locale, model);	
        }     
		RepositoryService repositoryService = processEngine.getRepositoryService();	
		repositoryService.createDeployment().addInputStream("ProcessActiviti.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/ProcessActiviti.bpmn")).deploy();
		Map<String, Object> variables = new HashMap<String, Object>();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess", variables);
		return "login";
	}
	
    //Logowanie
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(HttpServletRequest request,Locale locale, Model model) {
		logger.info("Logowanie", locale);
		if(request.getSession().getAttribute("login")!=null){
        	return loginWeryfikacja(request, locale, model);
        }
		return "login";
	}
	
    // Nowa Delegacja formularz
	@RequestMapping(value="/nowa")	
	public String nowa(HttpServletRequest request,Locale locale, Model model) {
		if(request.getSession().getAttribute("login")==null){
			return login(request, locale, model);
		}
		else{
			return "create";
		}  
	}
	
	//Panel uzytkownika
	@RequestMapping(value="/panel")	
	public String loginWeryfikacja(HttpServletRequest request,Locale locale, Model model) {
	    if(request.getSession().getAttribute("login")==null){    	
	    	String login=request.getParameter("login");
		    String haslo=request.getParameter("haslo");
		    IdentityService identityService=processEngine.getIdentityService();
		    boolean checkPassword=identityService.checkPassword(login, haslo);
		    if(checkPassword){
		    	request.getSession().setAttribute("login", login);
		    	return moje(request, locale, model);
		    }
	    	return "login";
	    }
		return "panel";
	}
	
	//Nowa Delegacje wpis do bazy
	@RequestMapping(value="/nowaWeryfikacja")	
	public String nowaWeryfikacja(HttpServletRequest request,Locale locale, Model model) {	
	    String login=(String) request.getSession().getAttribute("login"); 
	    String miejsce=request.getParameter("miejsce");
	    String dataRozpoczecia=request.getParameter("dataRozpoczecia");
	    String dataZakonczenia=request.getParameter("dataZakonczenia");
	    String transport=request.getParameter("transport");
	    String hotel=request.getParameter("hotel");
	    String cel=request.getParameter("cel");
	    String projekt=request.getParameter("projekt");
	    Date date = new Date();
		Map<String, Object> variables = new HashMap<String, Object>();
		   
		variables.put("miejsce", miejsce);
		variables.put("dataRozpoczecia", dataRozpoczecia);
		variables.put("dataZakonczenia", dataZakonczenia);
		variables.put("transport", transport);
		variables.put("hotel", hotel);
		variables.put("cel", cel);
		variables.put("projekt", projekt);
		   
		variables.put("customerName", "Irshad");
		variables.put("customerEamil", "irshad.mansuri@attuneinfocom.   com");
		variables.put("trainingTopic", "Activiti");
		variables.put("trainingDate", date);
		variables.put("temp", login);
		RuntimeService runtimeService = processEngine.getRuntimeService();
		IdentityService  identityService=processEngine.getIdentityService();
		String id;
		try {
			identityService.setAuthenticatedUserId(login);
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess", variables);
			id=processInstance.getId();
		
		}
		finally {
			identityService.setAuthenticatedUserId(null);
		}

			return "createFinish";
	}
	
	//Delegacje do zatwierdzenia 
	@RequestMapping(value="/moje")	
	public String moje(HttpServletRequest request,Locale locale, Model model) {
		String login=request.getSession().getAttribute("login").toString();
		if(login==null){
			System.out.println("login == null!!!!");
		}
		else{
			System.out.println("Login nie null");
		}
		TaskService taskService = processEngine.getTaskService();	
		List<Task> tasks =taskService.createTaskQuery().taskAssignee(login).list();
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);	
		String formattedDate = dateFormat.format(date);	
		model.addAttribute("serverTime", formattedDate );	
		model.addAttribute("zadania", tasks);
		return "delegacje";
	}
	//Moje procesy
	@RequestMapping(value="/procesy")	
	public String procesy(HttpServletRequest request,Locale locale, Model model) {
		String login=request.getSession().getAttribute("login").toString();
		if(login==null){
			System.out.println("login == null!!!!");
		}
		else{
			System.out.println("Login nie null");
		}
		TaskService taskService = processEngine.getTaskService();	
		List<Task> tasks =taskService.createTaskQuery().taskAssignee(login).list();
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);	
		String formattedDate = dateFormat.format(date);	
				
		List<HistoricProcessInstance> lista2=new ArrayList();
		
	//	processEngine.getHistoryService().
		
		HistoryService history=processEngine.getHistoryService();
		//List<HistoricActivityInstance>	processinstance=history.	createHistoricActivityInstanceQuery().taskAssignee("mbiernat").	list();				
		List<HistoricProcessInstance> lista=processEngine.getHistoryService().createHistoricProcessInstanceQuery().list();
		lista=processEngine.getHistoryService().createHistoricProcessInstanceQuery().involvedUser("mbiernat").list();		
		//List<Task> task=taskService.createTaskQuery().executionId("742501").list();
	 	//List<HistoricTaskInstance> tasks=processEngine.getHistoryService().createHistoricTaskInstanceQuery().list();

		
		for (HistoricProcessInstance historicProcessInstance : lista) {

			 if(!(historicProcessInstance.getStartUserId().equalsIgnoreCase("mbiernat"))){
				 continue;
			 }
			 lista2.add(historicProcessInstance);			 
		}
	
		model.addAttribute("serverTime", formattedDate );	
		model.addAttribute("zadania", lista2);
		return "procesy";
	}
	
	// Delegacje na grupie użytkownika
	@RequestMapping(value="/claims")	
	public String claims(HttpServletRequest request,Locale locale, Model model) {
		String login=request.getSession().getAttribute("login").toString();
		if(login==null){
			
		}
		else{

		}
		TaskService taskService = processEngine.getTaskService();
		IdentityService identityService=processEngine.getIdentityService();
		List<Group> partofuser =identityService.createGroupQuery().
		groupMember(login).list();
		List<Task> tasks2=	taskService.createTaskQuery().taskAssignee("kgorska").list();
		for(Group partofuse:partofuser){
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(partofuse.getName()).list();
			if(tasks==null){
	
			}
			else{
				tasks2.addAll(tasks);
			}
		}
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		String formattedDate = dateFormat.format(date);
		model.addAttribute("serverTime", formattedDate );
		model.addAttribute("zadania", tasks2);
		return "claims";
	}
	
	//Szczegóły delegacji
	@RequestMapping(value="/delegacja")	
	public String delegacja(HttpServletRequest request,Locale locale, Model model,@RequestParam String del) {
		String login=request.getSession().getAttribute("login").toString();
		TaskService taskService = processEngine.getTaskService(); 
		List<Task> tasks =taskService.createTaskQuery().taskAssignee(login).list();
		for (Task task : tasks) {
			String id=task.getId();
			if(id.equalsIgnoreCase(del)){
				
				model.addAttribute("projekt", taskService.getVariable(id, "projekt"));
				model.addAttribute("miejsce", taskService.getVariable(id, "miejsce"));
				model.addAttribute("dataRozpoczecia", taskService.getVariable(id, "dataRozpoczecia"));
				model.addAttribute("dataZakonczenia", taskService.getVariable(id, "dataZakonczenia"));
				model.addAttribute("transport", taskService.getVariable(id, "transport"));
				model.addAttribute("hotel", taskService.getVariable(id, "hotel"));
				model.addAttribute("cel", taskService.getVariable(id, "cel"));
				model.addAttribute("customerEamil", taskService.getVariable(id, "customerEamil"));		
				model.addAttribute("dateStep1", taskService.getVariable(id, "dateStep1"));
				model.addAttribute("trainingDate", taskService.getVariable(id, "trainingDate"));
				model.addAttribute("events", taskService.getVariable(id, "events"));	
				List<HistoricTaskInstance> tasks2=processEngine.getHistoryService().createHistoricTaskInstanceQuery().executionId(task.getProcessInstanceId()).list();
				HistoricVariableInstanceQuery vv=processEngine.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(task.getProcessInstanceId());
				List<HistoricVariableInstance> variables=vv.list();
			 
			}
		}
		return "delegacja";
	}
	//Szczegóły delegacji
	@RequestMapping(value="/proces")	
	public String proces(HttpServletRequest request,Locale locale, Model model,@RequestParam String del) {
		String login=request.getSession().getAttribute("login").toString();
		List<HistoricTaskInstance> tasks=processEngine.getHistoryService().createHistoricTaskInstanceQuery().executionId(del).list();
	 
		List<HistoricProcessInstance> lista=processEngine.getHistoryService().createHistoricProcessInstanceQuery().involvedUser(login).list();
		for (HistoricProcessInstance historicProcessInstance : lista) {
			if(historicProcessInstance.getId().equalsIgnoreCase(del)){
			HistoricVariableInstanceQuery vv=processEngine.getHistoryService().createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId());
			List<HistoricVariableInstance> variables=vv.list();				 
			model.addAttribute("procesDane", variables);	
		    List<HistoricTaskInstance> tasks2=processEngine.getHistoryService().createHistoricTaskInstanceQuery().executionId(historicProcessInstance.getId()).list();
		    }	 
		}
			
		return "proces";
	}
	//Szcegóły delegacji na grupie
	@RequestMapping(value="/claim")	
	public String claim(HttpServletRequest request,Locale locale, Model model,@RequestParam String del) {
		String login=request.getSession().getAttribute("login").toString();
		TaskService taskService = processEngine.getTaskService();
		IdentityService identityService=processEngine.getIdentityService();
		List<Group> partofuser =identityService.createGroupQuery().groupMember(login).list();
		for(Group partofuse:partofuser){
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(partofuse.getName()).list();
			for (Task task : tasks) {
				String id=task.getId();
				if(id.equalsIgnoreCase(del)){
					model.addAttribute("projekt", taskService.getVariable(id, "projekt"));
					model.addAttribute("miejsce", taskService.getVariable(id, "miejsce"));
					model.addAttribute("dataRozpoczecia", taskService.getVariable(id, "dataRozpoczecia"));
					model.addAttribute("dataZakonczenia", taskService.getVariable(id, "dataZakonczenia"));
					model.addAttribute("transport", taskService.getVariable(id, "transport"));
					model.addAttribute("hotel", taskService.getVariable(id, "hotel"));
					model.addAttribute("cel", taskService.getVariable(id, "cel"));
					model.addAttribute("customerEamil", taskService.getVariable(id, "customerEamil"));
					model.addAttribute("trainingDate", taskService.getVariable(id, "trainingDate"));						
				}
			}
		}
		return "claim";
	}
	
	//Zakceptowanie delegacji
	@RequestMapping(value="/akceptacja")	
	public String akceptacja(HttpServletRequest request,Locale locale, Model model,@RequestParam String del) {
		String login=request.getSession().getAttribute("login").toString();
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks =taskService.createTaskQuery().taskAssignee(login).list();
		for (Task task : tasks) {
			String id=task.getId();
			if(id.equalsIgnoreCase(del)){
				taskService.complete(task.getId());
			}		
		}
		return moje(request, locale, model);
	}
	
	//Przypisanie delegacje nie przypisanej która jests na grupie
	@RequestMapping(value="/przypisz")	
	public String przypisz(HttpServletRequest request,Locale locale, Model model,@RequestParam String del) {
		String login=request.getSession().getAttribute("login").toString();
		TaskService taskService = processEngine.getTaskService();
		IdentityService identityService=processEngine.getIdentityService();
		List<Group> partofuser =identityService.createGroupQuery().groupMember(login).list();
		for(Group partofuse:partofuser){
			List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(partofuse.getName()).list();
			for (Task task : tasks) {
				String id=task.getId();
				if(id.equalsIgnoreCase(del)){
					if(id.equalsIgnoreCase(del)){
						taskService.claim(task.getId(), login);
					}
				}

			}
		}
		return claims(request, locale, model);
	}
	
	//Wylogowanie sie
	@RequestMapping(value="/wyloguj")	
	public String wyloguj(HttpServletRequest request,Locale locale, Model model) {
	
		String login=request.getSession().getAttribute("login").toString();
		request.getSession().setAttribute("login", null);
		return "login";
	}

}

package com.company.biernat.controllers;

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

  private static final Logger logger = LoggerFactory
      .getLogger(HomeController.class);

  @Inject
  private ProcessEngine processEngine;

  // Strona główna
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String home(HttpServletRequest request, Locale locale, Model model) {
    if (request.getSession().getAttribute("login") != null) {
      return login(request, locale, model);
    }
    RepositoryService repositoryService = processEngine.getRepositoryService();
    repositoryService.createDeployment()
        .addInputStream("ProcessActiviti.bpmn20.xml",
            ReflectUtil.getResourceAsStream("diagrams/ProcessActiviti.bpmn"))
        .deploy();
    return "login";
  }

  // Logowanie
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(HttpServletRequest request, Locale locale, Model model) {
    logger.info("Logowanie", locale);
    if (request.getSession().getAttribute("login") != null) {
      return loginWeryfikacja(request, locale, model);
    }
    return "login";
  }

  // Nowa Delegacja formularz
  @RequestMapping(value = "/nowa")
  public String nowa(HttpServletRequest request, Locale locale, Model model) {
    if (request.getSession().getAttribute("login") == null) {
      return login(request, locale, model);
    } else {
      return "create";
    }
  }

  // Panel uzytkownika
  @RequestMapping(value = "/panel")
  public String loginWeryfikacja(HttpServletRequest request, Locale locale,
      Model model) {
    if (request.getSession().getAttribute("login") == null) {
      String login = request.getParameter("login");
      String haslo = request.getParameter("haslo");
      IdentityService identityService = processEngine.getIdentityService();
      boolean checkPassword = identityService.checkPassword(login, haslo);
      if (checkPassword) {
        request.getSession().setAttribute("login", login);
        return moje(request, locale, model);
      }
      return "login";
    }
    return "panel";
  }

  // Nowa Delegacje wpis do bazy
  @RequestMapping(value = "/nowaWeryfikacja")
  public String nowaWeryfikacja(HttpServletRequest request, Locale locale,
      Model model) {
    String login = (String) request.getSession().getAttribute("login");
    String miejsce = request.getParameter("miejsce");
    String dataRozpoczecia = request.getParameter("dataRozpoczecia");
    String dataZakonczenia = request.getParameter("dataZakonczenia");
    String transport = request.getParameter("transport");
    String hotel = request.getParameter("hotel");
    String cel = request.getParameter("cel");
    String projekt = request.getParameter("projekt");
    Map<String, Object> variables = new HashMap<String, Object>();

    variables.put("miejsce", miejsce);
    variables.put("dataRozpoczecia", dataRozpoczecia);
    variables.put("dataZakonczenia", dataZakonczenia);
    variables.put("transport", transport);
    variables.put("hotel", hotel);
    variables.put("cel", cel);
    variables.put("projekt", projekt);
    variables.put("temp", login);
    RuntimeService runtimeService = processEngine.getRuntimeService();
    IdentityService identityService = processEngine.getIdentityService();

    try {
      identityService.setAuthenticatedUserId(login);
      runtimeService.startProcessInstanceByKey("myProcess", variables);

    } finally {
      identityService.setAuthenticatedUserId(null);
    }

    return "createFinish";
  }

  // Delegacje do zatwierdzenia
  @RequestMapping(value = "/moje")
  public String moje(HttpServletRequest request, Locale locale, Model model) {
    String login = request.getSession().getAttribute("login").toString();
    if (login == null) {
      return login;
    }
    TaskService taskService = processEngine.getTaskService();
    List<Task> tasks = taskService.createTaskQuery().taskAssignee(login).list();
    model.addAttribute("zadania", tasks);
    return "delegacje";
  }

  // Moje procesy
  @RequestMapping(value = "/procesy")
  public String procesy(HttpServletRequest request, Locale locale,
      Model model) {
    String login = request.getSession().getAttribute("login").toString();
    if (login == null) {
      return login;
    }

    List<HistoricProcessInstance> processList = new ArrayList<HistoricProcessInstance>();

    List<HistoricProcessInstance> historicProcess;
    historicProcess = processEngine.getHistoryService()
        .createHistoricProcessInstanceQuery().involvedUser(login).list();

    for (HistoricProcessInstance historicProcessInstance : historicProcess) {

      if (!(historicProcessInstance.getStartUserId().equalsIgnoreCase(login))) {
        continue;
      }
      processList.add(historicProcessInstance);
    }

    model.addAttribute("zadania", processList);
    return "procesy";
  }

  // Delegacje na grupie użytkownika
  @RequestMapping(value = "/claims")
  public String claims(HttpServletRequest request, Locale locale, Model model) {
    String login = request.getSession().getAttribute("login").toString();
    if (login == null) {
      return login;
    }
    TaskService taskService = processEngine.getTaskService();
    IdentityService identityService = processEngine.getIdentityService();
    List<Group> partofuser = identityService.createGroupQuery()
        .groupMember(login).list();
    List<Task> userTasks = taskService.createTaskQuery().taskAssignee(login)
        .list();
    for (Group partofuse : partofuser) {
      List<Task> groupTasks = taskService.createTaskQuery()
          .taskCandidateGroup(partofuse.getName()).list();
      if (groupTasks != null) {
        userTasks.addAll(groupTasks);
      }
    }
    model.addAttribute("zadania", userTasks);
    return "claims";
  }

  // Szczegóły delegacji
  @RequestMapping(value = "/delegacja")
  public String delegacja(HttpServletRequest request, Locale locale,
      Model model, @RequestParam String del) {
    String login = request.getSession().getAttribute("login").toString();
    TaskService taskService = processEngine.getTaskService();
    List<Task> tasks = taskService.createTaskQuery().taskAssignee(login).list();
    for (Task task : tasks) {
      String id = task.getId();
      if (id.equalsIgnoreCase(del)) {

        model.addAttribute("projekt", taskService.getVariable(id, "projekt"));
        model.addAttribute("miejsce", taskService.getVariable(id, "miejsce"));
        model.addAttribute("dataRozpoczecia",
            taskService.getVariable(id, "dataRozpoczecia"));
        model.addAttribute("dataZakonczenia",
            taskService.getVariable(id, "dataZakonczenia"));
        model.addAttribute("transport",
            taskService.getVariable(id, "transport"));
        model.addAttribute("hotel", taskService.getVariable(id, "hotel"));
        model.addAttribute("cel", taskService.getVariable(id, "cel"));
        model.addAttribute("customerEamil",
            taskService.getVariable(id, "customerEamil"));
        model.addAttribute("dateStep1",
            taskService.getVariable(id, "dateStep1"));
        model.addAttribute("trainingDate",
            taskService.getVariable(id, "trainingDate"));
        List<HistoricTaskInstance> tasksHistoric = processEngine
            .getHistoryService().createHistoricTaskInstanceQuery()
            .executionId(task.getExecutionId()).list();

        model.addAttribute("taskHistory", tasksHistoric);
      }
    }
    return "delegacja";
  }

  // Szczegóły delegacji
  @RequestMapping(value = "/proces")
  public String proces(HttpServletRequest request, Locale locale, Model model,
      @RequestParam String del) {
    String login = request.getSession().getAttribute("login").toString();

    List<HistoricProcessInstance> processList = processEngine
        .getHistoryService().createHistoricProcessInstanceQuery()
        .involvedUser(login).list();
    for (HistoricProcessInstance historicProcessInstance : processList) {
      if (historicProcessInstance.getId().equalsIgnoreCase(del)) {

        HistoricVariableInstanceQuery historicVarables = processEngine
            .getHistoryService().createHistoricVariableInstanceQuery()
            .processInstanceId(historicProcessInstance.getId());
        List<HistoricVariableInstance> variables = historicVarables.list();
        for (HistoricVariableInstance historicVariableInstance : variables) {
          if (historicVariableInstance.getVariableName()
              .equalsIgnoreCase("projekt")) {
            model.addAttribute("projekt",
                (String) historicVariableInstance.getValue());
          } else if (historicVariableInstance.getVariableName()
              .equalsIgnoreCase("miejsce")) {
            model.addAttribute("miejsce",
                (String) historicVariableInstance.getValue());
          } else if (historicVariableInstance.getVariableName()
              .equalsIgnoreCase("transport")) {
            model.addAttribute("transport",
                (String) historicVariableInstance.getValue());
          } else if (historicVariableInstance.getVariableName()
              .equalsIgnoreCase("cel")) {
            model.addAttribute("cel",
                (String) historicVariableInstance.getValue());
          }

        }

        List<HistoricTaskInstance> historyTasks = processEngine
            .getHistoryService().createHistoricTaskInstanceQuery()
            .executionId(historicProcessInstance.getId()).list();
        model.addAttribute("historyTasks", historyTasks);
      }
    }

    return "proces";
  }

  // Szcegóły delegacji na grupie
  @RequestMapping(value = "/claim")
  public String claim(HttpServletRequest request, Locale locale, Model model,
      @RequestParam String del) {
    String login = request.getSession().getAttribute("login").toString();
    TaskService taskService = processEngine.getTaskService();
    IdentityService identityService = processEngine.getIdentityService();
    List<Group> partofuser = identityService.createGroupQuery()
        .groupMember(login).list();
    for (Group partofuse : partofuser) {
      List<Task> tasks = taskService.createTaskQuery()
          .taskCandidateGroup(partofuse.getName()).list();
      for (Task task : tasks) {
        String id = task.getId();
        if (id.equalsIgnoreCase(del)) {
          model.addAttribute("projekt", taskService.getVariable(id, "projekt"));
          model.addAttribute("miejsce", taskService.getVariable(id, "miejsce"));
          model.addAttribute("dataRozpoczecia",
              taskService.getVariable(id, "dataRozpoczecia"));
          model.addAttribute("dataZakonczenia",
              taskService.getVariable(id, "dataZakonczenia"));
          model.addAttribute("transport",
              taskService.getVariable(id, "transport"));
          model.addAttribute("hotel", taskService.getVariable(id, "hotel"));
          model.addAttribute("cel", taskService.getVariable(id, "cel"));
          model.addAttribute("customerEamil",
              taskService.getVariable(id, "customerEamil"));
          model.addAttribute("trainingDate",
              taskService.getVariable(id, "trainingDate"));
        }
      }
    }
    return "claim";
  }

  // Zakceptowanie delegacji
  @RequestMapping(value = "/akceptacja")
  public String akceptacja(HttpServletRequest request, Locale locale,
      Model model, @RequestParam String del) {
    String login = request.getSession().getAttribute("login").toString();
    TaskService taskService = processEngine.getTaskService();
    List<Task> tasks = taskService.createTaskQuery().taskAssignee(login).list();
    for (Task task : tasks) {
      String id = task.getId();
      if (id.equalsIgnoreCase(del)) {
        if (task.getName().equalsIgnoreCase("Weryfikacja Kierownik")) {
          Map<String, Object> taskVariables = new HashMap<String, Object>();
          taskVariables.put("accept1", "true");
          taskService.complete(task.getId(), taskVariables);
        } else if (task.getName().equalsIgnoreCase("Weryfikacja Ksiegowosc")) {
          Map<String, Object> taskVariables = new HashMap<String, Object>();
          taskVariables.put("accept2", "1");
          taskService.complete(task.getId(), taskVariables);
        } else if (task.getName().equalsIgnoreCase("Weryfikacja Uzytkownik")) {
          Map<String, Object> taskVariables = new HashMap<String, Object>();
          taskVariables.put("accept3", "true");
          taskService.complete(task.getId(), taskVariables);
        } else if (task.getName().equalsIgnoreCase("Weryfikacja Dyrektor")) {
          Map<String, Object> taskVariables = new HashMap<String, Object>();
          taskVariables.put("accept4", "4");
          taskService.complete(task.getId(), taskVariables);
        } else {
          taskService.complete(task.getId());
        }
      }
    }
    return moje(request, locale, model);
  }

  // Przypisanie delegacje nie przypisanej która jests na grupie
  @RequestMapping(value = "/przypisz")
  public String przypisz(HttpServletRequest request, Locale locale, Model model,
      @RequestParam String del) {
    String login = request.getSession().getAttribute("login").toString();
    TaskService taskService = processEngine.getTaskService();
    IdentityService identityService = processEngine.getIdentityService();
    List<Group> partofuser = identityService.createGroupQuery()
        .groupMember(login).list();
    for (Group partofuse : partofuser) {
      List<Task> tasks = taskService.createTaskQuery()
          .taskCandidateGroup(partofuse.getName()).list();
      for (Task task : tasks) {
        String id = task.getId();
        if (id.equalsIgnoreCase(del)) {
          if (id.equalsIgnoreCase(del)) {
            taskService.claim(task.getId(), login);
          }
        }

      }
    }
    return claims(request, locale, model);
  }

  // Wylogowanie sie
  @RequestMapping(value = "/wyloguj")
  public String wyloguj(HttpServletRequest request, Locale locale,
      Model model) {

    String login = request.getSession().getAttribute("login").toString();
    if (login != null) {
      request.getSession().setAttribute("login", null);
    }
    return "login";
  }

}

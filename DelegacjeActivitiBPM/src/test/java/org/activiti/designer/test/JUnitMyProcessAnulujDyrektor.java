package org.activiti.designer.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.util.ReflectUtil;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class JUnitMyProcessAnulujDyrektor {

	
	ProcessEngine processEngine = ProcessEngineConfiguration
	.createStandaloneProcessEngineConfiguration()
	.setJdbcDriver("com.mysql.jdbc.Driver")
	.setJdbcUrl("jdbc:mysql://localhost:3306/activiti?autoReconnect=true&useSSL=false")
	.setJdbcPassword("210283").setJdbcUsername("root")
	.buildProcessEngine();

	@Test
	public void startProcess() throws Exception {
		//Krok1 Rejestracja Wniosku
		
		
		IdentityService  identityService=processEngine.getIdentityService();
		ProcessInstance processInstance ;
		
		RepositoryService repositoryService = processEngine
				.getRepositoryService();
		
		repositoryService.createDeployment().addInputStream("MyProcess.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/ProcessActiviti.bpmn")).deploy();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("data", "03-10-2017");
		variableMap.put("projekt", "projektEuro");
		variableMap.put("kraj", "Francja");
		variableMap.put("hotel", "tak");
		variableMap.put("www", "dasas");
		variableMap.put("temp", "dasas");
		try {
			identityService.setAuthenticatedUserId("dasas");
		    processInstance = runtimeService.startProcessInstanceByKey("myProcess", variableMap);
			 
		} 
		finally {
			identityService.setAuthenticatedUserId(null);
		}
		assertNotNull(processInstance.getId());

		//Krok2 Weryfikacja Kierownik
		TaskService taskService = processEngine.getTaskService();		
		Task task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();


        assertEquals("tviscardi",task.getAssignee());
		
		Map<String, Object> taskVariables = new HashMap<String, Object>();				
		taskVariables.put("accept1", "true");
		taskService.complete(task.getId(), taskVariables);
		
		//Krok3 Weryfikacja Ksiegowosc
		task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
		assertNull(task.getAssignee());
		taskService.claim(task.getId(), "kgorska");		
		task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
		assertEquals("kgorska",task.getAssignee());
		taskVariables = new HashMap<String, Object>();	
		taskVariables.put("accept2", "1");
	    taskService.complete(task.getId(), taskVariables);
	  //Krok4 Portal Zakupowy wygenerowana propozycja
	  //Będzie webservice jako portal zakupowy, do ktorego listener podłączony do kolejki bedzie wysylal zapytania
	    task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
		assertNull(task.getAssignee());
		taskService.claim(task.getId(), "mbiernat");
		task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
		assertEquals("mbiernat",task.getAssignee());
		taskVariables = new HashMap<String, Object>();	
	    taskService.complete(task.getId(), taskVariables);
		
	  //Krok5 Weryfikacja uzytkownika
	  //Uzytkownik akceptuje propozycje portalu zakupowego(hotel itp),jezeli nie wysyla ponownie zapytanie w celu dostania innej propozycji
	  	
		task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
		assertEquals("dasas",task.getAssignee());
		taskVariables = new HashMap<String, Object>();
		taskVariables.put("accept3", "true");
	    taskService.complete(task.getId(), taskVariables);	    


		//Krok6 Weryfikacja Dyrektor
		
	    task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	    assertNull(task.getAssignee());
	    taskService.claim(task.getId(), "mkorbin");
	    task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	    assertEquals("mkorbin",task.getAssignee());
	    taskVariables = new HashMap<String, Object>();
	    taskVariables.put("accept4", "1");
	    taskService.complete(task.getId(), taskVariables);
		
	    
	    
	  //Anulowane bedzie mail do uzytkownika
		
	    task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	 	assertNull(task.getAssignee());
	 	taskService.claim(task.getId(), "mbiernat");
	 	task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	 	assertEquals("mbiernat",task.getAssignee());
	 	taskVariables = new HashMap<String, Object>();	
	 	taskService.complete(task.getId(), taskVariables);
	 	task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	    assertNull(task);
	}	
}

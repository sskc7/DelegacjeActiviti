package com.company.biernat;

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

public class JUnitMyProcessAnulujKsiegowosc {

	
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
		taskVariables.put("accept2", "3");
	    taskService.complete(task.getId(), taskVariables);
	    
	  //Krok  Weryfikacja użytkownika
	  	task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	  	assertNull(task.getAssignee());
	  	taskService.claim(task.getId(), "mbiernat");		
	  		
	  	assertEquals("mbiernat",task.getAssignee());
	  	task=taskService.createTaskQuery().executionId(processInstance.getId()).singleResult();
	  	taskVariables = new HashMap<String, Object>();

	  	taskService.complete(task.getId(), taskVariables);
	  		
	  	assertNull(task);	   
	  				
	  	}	
}
		
		


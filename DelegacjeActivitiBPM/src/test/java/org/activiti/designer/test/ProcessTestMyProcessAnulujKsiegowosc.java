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

public class ProcessTestMyProcessAnulujKsiegowosc {

	
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
		
		repositoryService.createDeployment().addInputStream(	"MyProcess.bpmn20.xml", ReflectUtil.getResourceAsStream("diagrams/ProcessActiviti.bpmn")).deploy();
		RuntimeService runtimeService = processEngine.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("data", "03-10-2017");
		variableMap.put("projekt", "projektEuro");
		variableMap.put("kraj", "Francja");
		variableMap.put("hotel", "tak");
		variableMap.put("www", "dasas");
		try {
			identityService.setAuthenticatedUserId("dasas");
		    processInstance = runtimeService.startProcessInstanceByKey("myProcess", variableMap);
			 
		 } 
		finally {
			identityService.setAuthenticatedUserId(null);
		}
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " "
				+ processInstance.getProcessDefinitionId());
		System.out.println("---------------------------------");
		//Krok2 Weryfikacja Kierownik
		TaskService taskService = processEngine.getTaskService();
		List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("Admin").list();
		for (Task task : tasks) {
			
			if(task.getProcessInstanceId().equalsIgnoreCase(processInstance.getId())){
				System.out.println("getassigne: " + task.getAssignee());
				System.out.println("getexecutionid: " + task.getExecutionId());
				System.out.println("getid: " + task.getId());
				System.out.println("getname: " + task.getName());
				System.out.println("getprocessinstanceid: " + task.getProcessInstanceId());
				taskService.claim(task.getId(), "mbiernat");

			}
		}
		tasks =	taskService.createTaskQuery().taskAssignee("mbiernat").list();
		for (Task task : tasks) {

			if(task.getProcessInstanceId().equals(processInstance.getId())){
				System.out.println("getassigne: " + task.getAssignee());
				System.out.println("getexecutionid: " + task.getExecutionId());
				System.out.println("getid: " + task.getId());
				System.out.println("getname: " + task.getName());
				System.out.println("getprocessinstanceid: " + task.getProcessInstanceId());
				Map<String, Object> taskVariables = new HashMap<String, Object>();				
				taskVariables.put("accept1", "true");
			    taskService.complete(task.getId(), taskVariables);
			
			}
		}
		System.out.println("---------------------------------");
		//Krok3 Weryfikacja Ksiegowosc
		tasks = taskService.createTaskQuery().taskCandidateGroup("Ksiegowosc").list();
		for (Task task : tasks) {
			

			if(task.getProcessInstanceId().equalsIgnoreCase(processInstance.getId())){
				taskService.claim(task.getId(), "kgorska");
			}
		}
		tasks =	taskService.createTaskQuery().taskAssignee("kgorska").list();
		for (Task task : tasks) {

			if(task.getProcessInstanceId().equalsIgnoreCase(processInstance.getId())){
				Map<String, Object> taskVariables = new HashMap<String, Object>();	
				//Delegacja zagraniczna ustawiamy 1
				// 1= portal zakupowy(delegacje zagraniczne lub Polskie z hotelem lub przy transporcie samolotem
				// 2- krajowe bez samolotu lub hotelu
				// 3 -anulowanie delegacji				
				taskVariables.put("accept2", "3");
			    taskService.complete(task.getId(), taskVariables);
			    System.out.println("getassigne: " + task.getAssignee());
				System.out.println("getexecutionid: " + task.getExecutionId());
				System.out.println("getid: " + task.getId());
				System.out.println("getname: " + task.getName());
				System.out.println("getprocessinstanceid: " + task.getProcessInstanceId());
			
			}
		}	
		System.out.println("---------------------------------");
		//Krok4 Anulowane bedzie mail do uzytkownika
		tasks = taskService.createTaskQuery().taskCandidateGroup("Admin").list();
		for (Task task : tasks) {
			
			if(task.getProcessInstanceId().equalsIgnoreCase(processInstance.getId())){
				taskService.claim(task.getId(), "mbiernat");
			}
		}
		tasks =	taskService.createTaskQuery().taskAssignee("mbiernat").list();
		for (Task task : tasks) {
			
			if(task.getProcessInstanceId().equalsIgnoreCase(processInstance.getId())){
				System.out.println("getassigne: " + task.getAssignee());
				System.out.println("getexecutionid: " + task.getExecutionId());
				System.out.println("getid: " + task.getId());
				System.out.println("getname: " + task.getName());
				System.out.println("getprocessinstanceid: " + task.getProcessInstanceId());
				Map<String, Object> taskVariables = new HashMap<String, Object>();								
			    taskService.complete(task.getId(), taskVariables);
			
			}
		}
		System.out.println("---------------------------------");
		
		
		
	}
}
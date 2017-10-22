package com.company.biernat.Listeners;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.activiti.engine.IdentityService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.identity.Group;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
public class PrzypisanieKierownikaTask implements TaskListener {


  private static final long serialVersionUID = 1L;

  @Override
  public void notify(DelegateTask delegateTask) {
    // TODO Auto-generated method stub
    DelegateExecution execution = delegateTask.getExecution();
    String login = (String) execution.getVariable("temp");
    // Znajdz dzial
    IdentityService identityService = execution.getEngineServices()
        .getIdentityService();
    Group group = identityService.createGroupQuery().groupMember(login)
        .singleResult();
    String groupName = group.getName();

    String kierownik = getKierownik(groupName);
    System.out.println("Kierownik " + kierownik);

    if (kierownik != null) {
      delegateTask.setAssignee(kierownik);
    }

  }

  public String getKierownik(String dzial) {
    try {

      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(
          classLoader.getResource("files/kierownicy.xml").getFile());
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
      Document doc = dbuilder.parse(file);
      doc.getDocumentElement().normalize();
      NodeList nlist = doc.getElementsByTagName("dzial");
      for (int temp = 0; temp < nlist.getLength(); temp++) {
        Node nnode = nlist.item(temp);
        if (nnode.getNodeType() == Node.ELEMENT_NODE) {
          Element eelement = (Element) nnode;
          String dzialTemp = eelement.getElementsByTagName("nazwa").item(0)
              .getTextContent();
          String kierownikTemp = eelement.getElementsByTagName("kierownik")
              .item(0).getTextContent();
          if (dzialTemp.equalsIgnoreCase(dzial)) {
            return kierownikTemp;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "admin";

  }

}

package com.ddlab.webservice.server;

public class OrganisationInfoService 
{
	public Employee getEmp( Employee emp )
	{
		System.out.println("----------------------Data at Server Side---------------------");
		System.out.println("Emp Name : "+emp.getName());
		System.out.println("Emp Id : "+emp.getId());
		System.out.println("Emp Salary :"+emp.getSalary());
		Address adrs = emp.getAdrs();
		System.out.println("Permanent Adress ::: "+adrs.getPermanetAdrs());
		System.out.println("Temporary Adress ::: "+adrs.getTemporaryAdrs());
		System.out.println("----------------------Data at Server Side---------------------");
		return emp;
	}
	public Organisation getInfo( Organisation org )
	{
		System.out.println("Organisation Name ::: "+org.getName());
		System.out.println("Organisation Location ::: "+org.getLocation());
		Employee emp = org.getEmp();
		System.out.println("Emp Name : "+emp.getName());
		System.out.println("Emp Id : "+emp.getId());
		System.out.println("Emp Salary :"+emp.getSalary());
		Address adrs = emp.getAdrs();
		System.out.println("Permanent Adress ::: "+adrs.getPermanetAdrs());
		System.out.println("Temporary Adress ::: "+adrs.getTemporaryAdrs());
		return org;
	}
	
	public Employee getEmployeeInfo( Employee emp )
	{
		System.out.println("----------------------Data at Server Side---------------------");
		System.out.println("Data received at server side");
		
		System.out.println("Emp Name : "+emp.getName());
		System.out.println("Emp Id : "+emp.getId());
		System.out.println("Emp Salary :"+emp.getSalary());
		
		System.out.println("Data received at server side");
		
		Employee em = new Employee();
		
		em.setId(1234);
		em.setName("Deba");
		em.setSalary(13.78f);
		System.out.println("----------------------Data at Server Side---------------------");
		
		return em;
	}
}

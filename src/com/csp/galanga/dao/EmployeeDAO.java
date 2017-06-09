package com.csp.galanga.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.csp.galanga.dto.Employee;
import com.csp.galanga.util.Warehouse;

public class EmployeeDAO {
	
	public ArrayList<Employee> selectAll() throws SQLException{
		ArrayList<Employee> res = new ArrayList<>();
		String sql = "select  " + 
				"EMPLOYEE_NAME, SUP_NAME,   " + 
				"IFSCSP.Company_Position_API.Get_Position_Title(COMPANY_ID, POS_CODE) " + 
				"from IFSCSP.COMPANY_PERSON_ALL " + 
				"where POS_CODE not LIKE '%TRAVEL%' " + 
				"and EMPLOYEE_STATUS like 'ATIVO'";
		
		Warehouse warehouse = new Warehouse();
		try(Connection c = warehouse.connectIFS();
				Statement stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				){
			warehouse = null;
			while(rs.next()){
				res.add(new Employee(rs.getString(1), rs.getString(2), rs.getString(3), null, "TRUE"));
			}
		}
		return res;
	}

}

/*
select A.EMPLOYEE_NAME, A.SUP_NAME, IFSCSP.Company_Position_API.Get_Position_Title(A.COMPANY_ID, A.POS_CODE)
--, C.identity
from IFSCSP.COMPANY_PERSON_ALL A
--full outer JOIN IFSCSP.PERSON_INFO B ON A.person_id = B.person_id
--full outer JOIN IFSCSP.FND_USER C ON B.user_id = C.identity
WHERE A.POS_CODE not LIKE '%TRAVEL%' 
AND A.EMPLOYEE_STATUS like 'ATIVO'
--AND B.user_id IS NOT NULL
--AND C.active = 'TRUE'
;
SELECT  FROM PERSON_INFO B
WHERE B.user_id IS NOT NULL
;
select U.IDENTITY, U.DESCRIPTION, U.ACTIVE from IFSCSP.FND_USER U 
WHERE U.active LIKE 'TRUE'
order by U.ACTIVE, U.DESCRIPTION

;
select OBJID, OBJVERSION, COMPANY_ID, EMP_NO_DUMMY, EMP_NO, EMPLOYEE_NAME, 
POS_CODE, PERSON_ID, INSURANCE_ID, ORG_CODE, SUP_EMP_NO, SUP_NAME, 
INTERNAL_DISPLAY_NAME, EMPLOYEE_STATUS, PERSON_TYPE, PERSON_TYPE, CPF, DOCUMENT_TYPE, 
DOCUMENT_NUMBER, EXPEDITION_PLACE, EXPEDITION_DATE, IFSCSP.Company_Person_API.Get_Company_Office(COMPANY_ID,EMP_NO)||' - '||IFSCSP.Work_Location_API.Get_Description(IFSCSP.Company_Person_API.Get_Company_Office(COMPANY_ID,EMP_NO)), POS_CODE||' - '||IFSCSP.Company_Position_API.Get_Position_Title(COMPANY_ID, POS_CODE), 
IFSCSP.Company_Pers_Assign_API.Get_Org_Code(COMPANY_ID,EMP_NO,TRUNC(SYSDATE))||' - '||IFSCSP.Company_Org_API.Get_Org_Name(COMPANY_ID,IFSCSP.Company_Pers_Assign_API.Get_Org_Code(COMPANY_ID,EMP_NO,TRUNC(SYSDATE))), IFSCSP.Emp_Job_Assign_API.Get_Job_Title(COMPANY_ID, EMP_NO), 
IFSCSP.Company_Pers_Assign_API.Get_Sup_Emp_No(company_id,emp_no,least(trunc(sysdate)))||' - '||IFSCSP.Company_Pers_Assign_API.Get_Sup_Names(company_id,emp_no,least(trunc(sysdate))), IFSCSP.Pers_comms_Work_API.Get_Work_Phone(COMPANY_ID, EMP_NO), IFSCSP.Pers_comms_Work_API.Get_Work_Fax(COMPANY_ID, EMP_NO), 
IFSCSP.Pers_comms_Work_API.Get_Work_Mobile(COMPANY_ID, EMP_NO), IFSCSP.Pers_comms_Work_API.Get_Work_Email(COMPANY_ID, EMP_NO), IFSCSP.Pers_comms_Work_API.Get_Work_Address(COMPANY_ID, EMP_NO), SIGN, IFSCSP.Company_Person_API.Get_Emp_Cat_Name(COMPANY_ID, EMP_NO), IFSCSP.Emp_Employed_Time_API.Get_Current_Employment_Type(COMPANY_ID,EMP_NO,TRUNC(SYSDATE)), 
IFSCSP.Emp_Employed_Time_API.Get_Date_Of_Employment(COMPANY_ID,EMP_NO,TRUNC(SYSDATE)), IFSCSP.Emp_Employed_Time_API.Get_Date_Of_Employment(COMPANY_ID,EMP_NO,TRUNC(SYSDATE)), PICTURE_ID 
from IFSCSP.COMPANY_PERSON_ALL  
where POS_CODE not LIKE '%TRAVEL%' 
				and EMPLOYEE_STATUS like 'ATIVO'
*/
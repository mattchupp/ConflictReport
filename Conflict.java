import java.sql.*;
import oracle.jdbc.pool.*;
import oracle.sql.*;
import oracle.jdbc.*;
import java.util.*;

public class Conflict  {
	
	
	public static void main(String args[]) {
   
      Scanner console = new Scanner(System.in);  
      
		Connection odb;
		PreparedStatement query;
		ResultSet results;
      String semester; 

		String connString="jdbc:oracle:thin:@linux1:1521:database";

	    try {
			OracleDataSource ods = new OracleDataSource();
			ods.setURL(connString);
			ods.setUser("rjpleva");
			ods.setPassword("RJPLEVA");

			odb = ods.getConnection();
		   
         System.out.println("Enter Semester"); 
         semester = console.next(); 
        
               
         
         query = odb.prepareStatement("SELECT STUDENT_COURSE.SC_DEPT_CODE, "+
            "STUDENT_COURSE.SC_CRS_NUMBER, STUDENT_COURSE_1.SC_DEPT_CODE, "+
            "STUDENT_COURSE_1.SC_CRS_NUMBER, STUDENT_COURSE.SC_SEMESTER, "+
            "conflictNum(STUDENT_COURSE.SC_DEPT_CODE, "+
            "STUDENT_COURSE.SC_CRS_NUMBER, STUDENT_COURSE_1.SC_DEPT_CODE, "+
            "STUDENT_COURSE_1.SC_CRS_NUMBER,STUDENT_COURSE.SC_SEMESTER) " +
            "FROM rjpleva.STUDENT_CONFIDENTIAL STUDENT_CONFIDENTIAL, "+
            "rjpleva.STUDENT_COURSE STUDENT_COURSE, "+
            "rjpleva.STUDENT_COURSE STUDENT_COURSE_1 "+
            "WHERE STUDENT_CONFIDENTIAL.SC_ID = STUDENT_COURSE.SC_STD_ID "+
            "AND STUDENT_CONFIDENTIAL.SC_ID = STUDENT_COURSE_1.SC_STD_ID "+
            "AND STUDENT_COURSE.SC_SEMESTER = STUDENT_COURSE_1.SC_SEMESTER "+
            "AND ((STUDENT_COURSE.SC_DEPT_CODE<>STUDENT_COURSE_1.SC_DEPT_CODE"+
            " AND STUDENT_COURSE.SC_DEPT_CODE='CSIS') AND "+
            "(STUDENT_COURSE.SC_CRS_NUMBER<>STUDENT_COURSE_1.SC_CRS_NUMBER)"+
            "AND STUDENT_COURSE.SC_SEMESTER = '"+semester+"') GROUP BY "+
            "STUDENT_COURSE.SC_DEPT_CODE, STUDENT_COURSE.SC_CRS_NUMBER, "+
            "STUDENT_COURSE_1.SC_DEPT_CODE, STUDENT_COURSE_1.SC_CRS_NUMBER,"+
            " STUDENT_COURSE.SC_SEMESTER ORDER BY STUDENT_COURSE.SC_DEPT_CODE,"+
            " STUDENT_COURSE.SC_CRS_NUMBER, STUDENT_COURSE_1.SC_DEPT_CODE, "+
            "STUDENT_COURSE_1.SC_CRS_NUMBER"); 
         
         
      
			results = query.executeQuery();
			while (results.next()) {
            // (1) = STUDENT_COURSE.SC_DEPT_CODE
            // (2) = STUDENT_COURSE.SC_CRS_NUMBER
            // (3) = STUDENT_COURSE_1.SC_DEPT_CODE
            // (4) = STUDENT_COURSE_1.SC_CRS_NUMBER
            // (5) = STUDENT_COURSE.SC_SEMESTER
            
            // String[][] course = new String[][5]; 
         
		      System.out.println(results.getString(1));
			}
		} catch (Exception problem){

			System.out.println(problem.getMessage());
			problem.printStackTrace();
		}
  	
	}
}
	
		

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
           
         Set<Integer> csis_courses = new TreeSet<>();
         Map<String, Map<Integer, Map<Integer, Double>>> dept_map = new TreeMap<>();
         
			while (results.next()) {
            
            Integer csis_course = results.getInt(2);
            String dept = results.getString(3);
            Integer course = results.getInt(4);
            Double conflict = results.getDouble(6);
            
            csis_courses.add(csis_course);
            Map<Integer, Map<Integer, Double>> course_map = dept_map.get(dept);
            if (course_map == null)
               dept_map.put(dept, course_map = new TreeMap<>());
            Map<Integer, Double> conflict_map = course_map.get(course);
            if (conflict_map == null)
               course_map.put(course, conflict_map = new TreeMap<>());
            conflict_map.put(csis_course, conflict);    
            
			}
         
         
         System.out.print("CSIS");
         for (Integer csis_course : csis_courses)
            System.out.print("\t" + csis_course);
         System.out.println();
         for (Map.Entry<String, Map<Integer, Map<Integer, Double>>> dept_entry : dept_map.entrySet()) {
            String dept = dept_entry.getKey();
            System.out.println(dept);
            Map<Integer, Map<Integer, Double>> course_map = dept_entry.getValue();
            for (Map.Entry<Integer, Map<Integer, Double>> course_entry : course_map.entrySet()) {
               Integer course = course_entry.getKey();
               System.out.print(course);
               Map<Integer, Double> conflict_map = course_entry.getValue();
               for (Integer csis_course : csis_courses) {
                  Double conflict = conflict_map.get(csis_course);
                  if (conflict == null)
                     System.out.print("\t-");
                  else
                     System.out.print("\t" + conflict);
               }
               System.out.println();
            }
         }
         
		} catch (Exception problem){

			System.out.println(problem.getMessage());
			problem.printStackTrace();
		}
  	
	}  
}
	
		

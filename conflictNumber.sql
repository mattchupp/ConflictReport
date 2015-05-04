-- Matt Chupp
-- mlchupp
-- Database
-- conflictNumber.sql

create or replace function conflictNumber(dept_code1 in VARCHAR2,
									   	  crs_num1 in INTEGER,
									      dept_code2 in VARCHAR2,
									      crs_num2 in INTEGER,
									      semester in VARCHAR2)
	RETURN DECIMAL as
		frequency1 INTEGER;
		sem_freq1 DECIMAL;
		frequency2 INTEGER;
		sem_freq2 DECIMAL;
		num_enrolled INTEGER;
		numFresh INTEGER;
		numSoph INTEGER;
		numJun INTEGER;
		numSen INTEGER;
		magicNum DECIMAL; -- conflict number

BEGIN  
	select count(*)
	into frequency1
	from section
	where sec_dept_code = dept_code1 and
		  sec_crs_number = crs_num1 and 
		  to_number (substr(sec_semester, 3,4)) between 2011 and 2014;
	
	select count(*)
	into frequency2
	from section
	where sec_dept_code = dept_code2 and
		  sec_crs_number = crs_num2 and 
		  to_number (substr(sec_semester, 3,4)) between 2011 and 2014;
	
	sem_freq1 := frequency1/8;
	sem_freq2 := frequency2/8;

	select count(distinct sc_std_id)
	into num_enrolled
		from student_course
		where sc_std_id in (select sc_std_id
							from student_course
							where sc_dept_code= dept_code1 and
							sc_crs_number= crs_num1 and
							sc_semester= semester and
							sc_std_id in (select sc_std_id
										  from student_course
										  where sc_dept_code = dept_code2 and	
										  sc_semester = semester and
										  sc_crs_number = crs_num2));
										  
	select count(distinct sc_std_id)
	into numFresh
		from student_course, student_confidential
		where sc_std_id= sc_id and
			  sc_std_id in (select sc_std_id
							from student_course
							where sc_dept_code= dept_code1 and
							sc_crs_number= crs_num1 and
							 sc_semester = semester and
							sc_std_id in (select sc_std_id
										  from student_course
										  where sc_dept_code = dept_code2 and
										  sc_semester = semester and										  
										  sc_crs_number = crs_num2))	
	
				and to_char(sc_grad_year, 'YYYY') = (case when substr(semester,0,2)= 'FA' then
										to_number (substr(semester,3,4))+4
									else to_number (substr(semester,3,4))+3
									end);
				
	select count(distinct sc_std_id)
	into numSoph
		from student_course, student_confidential
		where sc_std_id= sc_id and
			  sc_std_id  in (select sc_std_id
							from student_course
							where sc_dept_code= dept_code1 and
							sc_crs_number= crs_num1 and
							 sc_semester = semester and
							sc_std_id in (select sc_std_id
										  from student_course
										  where sc_dept_code = dept_code2 and 
										  sc_semester = semester and
										  sc_crs_number = crs_num2))	
										  
				and to_char(sc_grad_year, 'YYYY') = (case when substr(semester,0,2)= 'FA' then
										to_number (substr(semester,3,4))+3
									else to_number (substr(semester,3,4))+2
									end);
			
	select count(distinct sc_std_id)
	into numJun
		from student_course, student_confidential
		where sc_std_id= sc_id and
			  sc_std_id  in (select sc_std_id
							from student_course
							where sc_dept_code= dept_code1 and
							sc_crs_number= crs_num1 and
							sc_semester = semester and
							sc_std_id in (select sc_std_id
										  from student_course
										  where sc_dept_code = dept_code2 and
										  sc_semester = semester and
										  sc_crs_number = crs_num2))	
										  
				and to_char(sc_grad_year, 'YYYY') = (case when substr(semester,0,2)= 'FA' then
										to_number (substr(semester,3,4))+2
									else to_number (substr(semester,3,4))+1
									end);
									
	select count(distinct sc_std_id)
	into numSen
		from student_course, student_confidential
		where sc_std_id= sc_id and
			  sc_std_id  in (select sc_std_id
							from student_course
							where sc_dept_code= dept_code1 and
							sc_crs_number= crs_num1 and
							sc_semester = semester and
							sc_std_id in (select sc_std_id
										  from student_course
										  where sc_dept_code = dept_code2 and
										  sc_semester = semester and
										  sc_crs_number = crs_num2))	
										  
				and to_char(sc_grad_year, 'YYYY') = (case when substr(semester,0,2)= 'FA' then
										to_number (substr(semester,3,4))+1
									else to_number (substr(semester,3,4))
									end);
	magicNum :=case when (sem_freq1+sem_freq2)= 0 then -1 else 					
	 2*((numFresh + numSoph*2 + numJun*3 + numSen*4) / (sem_freq1+sem_freq2))end;
	
	return magicNum;

end conflictNumber;

/

show errors;


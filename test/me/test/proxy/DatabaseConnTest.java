package me.test.proxy;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import me.proxy.resourse.ResourceDatabaseStorage;

public class DatabaseConnTest {

	ResourceDatabaseStorage res;
	
	@Before
	public void init(){
		
		res = new ResourceDatabaseStorage();
	}
	
	@Ignore
	@Test
	public void insertRow_Test() {
		
		try {
			res.insertRow("vasya");
		} catch (Exception e) {			
			e.printStackTrace();
			fail("Exception throwed");
		}		
		
	}
	
	@Test
	public void readRow_Test() {
		
		try {
			res.readRow(null);
		} catch (Exception e) {			
			e.printStackTrace();
			fail("Exception throwed");
		}		
		
	}
}

package iu.ogce.science.gateway.controller;

import static org.junit.Assert.*;
import iu.ogce.science.gateway.util.ViewNames;

import org.junit.Before;
import org.junit.Test;

public class PageControllerTest {

	private PageController pageController;
	
	@Before
    public void setup() {
		pageController = new PageController();
	}
	
	@Test
	public void testGetContactUs() {
		String result = pageController.getContactUs();
		assertNotNull(result);
        assertEquals(ViewNames.CONTACT_US_PAGE, result);
	}

}

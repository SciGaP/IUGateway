package org.scigap.iucig.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.scigap.iucig.controller.PageController;
import org.scigap.iucig.gateway.util.ViewNames;

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

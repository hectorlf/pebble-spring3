/*******************************************************************************
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.spring;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ViewResolver;

import com.mitchellbosecke.pebble.spring.config.MvcConfig;

/**
 * Unit test for the PebbleViewResolver
 *
 * @author Eric Bussieres
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class })
public class TestPebbleViewResolver {
    private static final Locale DEFAULT_LOCALE = Locale.CANADA;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Autowired
    private ViewResolver viewResolver;

    @Before
    public void setUp() throws Exception {
        this.request = new MockHttpServletRequest();
        this.request.setContextPath("testContextPath");

        this.request.getSession().setMaxInactiveInterval(600);

        this.response = new MockHttpServletResponse();
    }

    @Test
    public void viewResolverTestOK() throws Exception {
        Map<String, ?> model = new HashMap<>();
        String result = this.render("template", model);
        this.assertOutput(result, "/com/mitchellbosecke/pebble/spring/template/expectedTemplate.html");
    }

    private void assertOutput(String output, String expectedOutput) throws IOException {
        assertEquals(this.readExpectedOutputResource(expectedOutput), output.replaceAll("\\s", ""));
    }

    private String readExpectedOutputResource(String expectedOutput) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                this.getClass().getResourceAsStream(expectedOutput)))) {
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
        }
        // Remove all whitespaces
        return builder.toString().replaceAll("\\s", "");
    }

    private String render(String location, Map<String, ?> model) throws Exception {
        this.viewResolver.resolveViewName(location, DEFAULT_LOCALE).render(model, this.request, this.response);
        return this.response.getContentAsString();
    }
}

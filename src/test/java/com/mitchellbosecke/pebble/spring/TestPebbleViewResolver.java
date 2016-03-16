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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ViewResolver;

import com.mitchellbosecke.pebble.spring.config.MVCConfig;

/**
 * Unit test for the PebbleViewResolver
 *
 * @author Eric Bussieres
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { MVCConfig.class })
public class TestPebbleViewResolver {
    private static final Locale DEFAULT_LOCALE = Locale.CANADA;
    private static final String FORM_NAME = "formName";
    private static final String EXPECTED_RESPONSE_PATH = "/com/mitchellbosecke/pebble/spring/expectedResponse";
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private BindingResult bindingResult;

    @Autowired
    private ViewResolver viewResolver;

    @Test
    public void beansTestOK() throws Exception {
        String result = this.render("beansTest", new HashMap<String, Object>());
        this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/beansTest.html");
    }

    @Test
    public void bindingResultTestOK() throws Exception {
        Map<String, Object> model = new HashMap<>();

        model.put(BindingResult.class.getName() + "." + FORM_NAME, this.bindingResult);

        String result = this.render("bindingResultTest", model);
        this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/bindingResultTest.html");
    }

    @Test
    public void messageTestOK() throws Exception {
        String result = this.render("messageTest", new HashMap<String, Object>());
        this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/messageTest.html");
    }

    @Test
    public void requestTestOK() throws Exception {
        String result = this.render("requestTest", new HashMap<String, Object>());
        this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/requestTest.html");
    }

    @Test
    public void sessionTestOK() throws Exception {
        String result = this.render("sessionTest", new HashMap<String, Object>());
        this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/sessionTest.html");
    }

    @Before
    public void setUp() throws Exception {
        this.request = new MockHttpServletRequest();
        this.request.setContextPath("testContextPath");

        this.request.getSession().setMaxInactiveInterval(600);

        this.response = new MockHttpServletResponse();

        //Binding result setup
        this.bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(this.bindingResult.hasErrors()).thenReturn(true);
        Mockito.when(this.bindingResult.hasGlobalErrors()).thenReturn(true);
        Mockito.when(this.bindingResult.hasFieldErrors("testField")).thenReturn(true);

        List<ObjectError> allErrors = new ArrayList<>();
        allErrors.add(new ObjectError(FORM_NAME, new String[] {"error.test"}, new String[] {}, "???error.test???"));

        List<ObjectError> globalErrors = new ArrayList<>();
        globalErrors.add(new ObjectError(FORM_NAME, new String[] {"error.global.test.params"},
                new String[] {"param1", "param2"}, "???error.global.test.params???"));

        List<FieldError> fieldErrors = new ArrayList<>();
        fieldErrors.add(new FieldError(FORM_NAME, "testField", null, false, new String[] {"error.field.test.params"},
                new String[] {"param1", "param2"}, "???error.field.test.params???"));

        Mockito.when(this.bindingResult.getAllErrors()).thenReturn(allErrors);
        Mockito.when(this.bindingResult.getGlobalErrors()).thenReturn(globalErrors);
        Mockito.when(this.bindingResult.getFieldErrors("testField")).thenReturn(fieldErrors);
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

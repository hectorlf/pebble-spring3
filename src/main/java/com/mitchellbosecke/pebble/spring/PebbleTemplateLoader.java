package com.mitchellbosecke.pebble.spring;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.loader.PebbleDefaultLoader;

public class PebbleTemplateLoader extends PebbleDefaultLoader implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Override
	public Reader getReader(String resourceName) throws LoaderException {
		resourceName = getFullyQualifiedResourceName(resourceName);
		Resource resource = resourceLoader.getResource(resourceName);
		if (resource.exists()) {
			try {
				return new InputStreamReader(resource.getInputStream(), "UTF-8");
			} catch (IOException e) {
				throw new LoaderException("Failed to load template: " + resourceName);
			}
		}
		throw new LoaderException("No template exists named: " + resourceName);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private String getFullyQualifiedResourceName(String resourceName) {
		if (resourceName.startsWith(getPrefix())) {
			return resourceName;
		}
		return getPrefix() + resourceName + (getSuffix() == null ? "" : getSuffix());
	}

}

package ca.footeware.logstash.templateengines;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

/**
 * 
 * Use ${obj.prop} in your template to replace a certain the token Use
 * ${obj.prop!} to replace with empty string if obj.prop is null or undefined
 * 
 * 
 */
public class FreemarkerTemplateEngine {

	protected Configuration instance = null;
	protected String templatesFolder = "templates";
	protected Template templateCompiler = null;
	protected Writer writer = null;

	public void init(String templatesResouceFolder) {

		if (instance == null) {
			instance = new Configuration(new Version("2.3.22"));
			instance.setClassForTemplateLoading(this.getClass(), "/");
			this.templatesFolder = templatesResouceFolder;
		}
	}

	public void setTemplate(String template) {
		try {
			templateCompiler = instance
					.getTemplate(templatesFolder + File.separatorChar + template + ".ftl");
		} catch (IOException ex) {
			Logger.getLogger(FreemarkerTemplateEngine.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void process(Writer writer, Object data) {
		try {
			templateCompiler.process(data, writer);
			this.writer = writer;
		} catch (TemplateException | IOException ex) {
			Logger.getLogger(FreemarkerTemplateEngine.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void flush() {
		try {
			this.writer.flush();
		} catch (IOException ex) {
			Logger.getLogger(FreemarkerTemplateEngine.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
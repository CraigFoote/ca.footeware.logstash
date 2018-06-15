package ca.footeware.logstash.templateengines;

import java.io.IOException;
import java.io.Writer;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
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

		if (this.instance == null) {
			this.instance = new Configuration(new Version("2.3.22"));
			this.instance.setClassForTemplateLoading(this.getClass(), "/");
			this.templatesFolder = templatesResouceFolder;
		}
	}

	public void setTemplate(String template)
			throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		this.templateCompiler = this.instance.getTemplate(this.templatesFolder + "/" + template + ".ftl");
	}

	public void process(Writer writer, Object data) throws TemplateException, IOException {
		this.templateCompiler.process(data, writer);
		this.writer = writer;
	}

	public void flush() throws IOException {
		this.writer.flush();
	}

}
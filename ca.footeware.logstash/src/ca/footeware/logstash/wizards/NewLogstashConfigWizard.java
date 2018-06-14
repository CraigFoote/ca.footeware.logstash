/**
 * 
 */
package ca.footeware.logstash.wizards;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import ca.footeware.logstash.models.UserInput;
import ca.footeware.logstash.templateengines.FreemarkerTemplateEngine;
import freemarker.template.TemplateException;

/**
 * @author Footeware.ca
 *
 */
public class NewLogstashConfigWizard extends Wizard implements INewWizard {

	protected LogstashWizardPage pageOne;
	private IStructuredSelection selection;

	/**
	 * Constructor.
	 */
	public NewLogstashConfigWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle("New Logstash Configuration");

		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		// use the org.eclipse.core.runtime.Path as import
		URL url = FileLocator.find(bundle, new Path("icons/logstash-wizard.png"), null);
		// get an imageDescriptor and create Image object
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(url);
		setDefaultPageImageDescriptor(imageDescriptor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		pageOne = new LogstashWizardPage(selection);
		addPage(pageOne);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 * org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		IProject parent = pageOne.getParent();
		String name = pageOne.getName();
		String port = pageOne.getPort();
		String host = pageOne.getHost();
		String jira = pageOne.getJira();

		UserInput input = new UserInput();
		input.setName(name);
		input.setPort(port);
		input.setHost(host);
		input.setJira(jira);

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(parent.getName());
		IFolder folder = project.getFolder(name);

		try {
			folder.create(true, true, null);
		} catch (CoreException e) {
			pageOne.setErrorMessage(e.getMessage());
			return false;
		}

		FreemarkerTemplateEngine engine = new FreemarkerTemplateEngine();
		engine.init("templates"); // template location
		
		IFile log4j = folder.getFile("log4j2.properties");
		try (Writer fileWriter = new FileWriter(log4j.getRawLocation().makeAbsolute().toFile())) {
			engine.setTemplate("log4j2.properties"); // .ftl
			engine.process(fileWriter, input);
			engine.flush();
		} catch (IOException | TemplateException e) {
			pageOne.setErrorMessage(e.getMessage());
			return false;
		}

		try {
			parent.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			pageOne.setErrorMessage(e.getMessage());
			return false;
		}

		return true;
	}

}

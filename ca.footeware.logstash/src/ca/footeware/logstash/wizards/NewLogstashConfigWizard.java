/**
 * 
 */
package ca.footeware.logstash.wizards;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;
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

		try {
			// create files in new thread with progress monitor for user
			getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Creating folder and files.", 5);
					
					UserInput input = new UserInput();
					input.setName(name);
					input.setPort(port);
					input.setHost(host);
					input.setJira(jira);
					monitor.worked(1);
					
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot root = workspace.getRoot();
					IProject project = root.getProject(parent.getName());
					IFolder folder = project.getFolder(name);
					monitor.worked(1);
					
					try {
						folder.create(true, true, null);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
					monitor.worked(1);

					FreemarkerTemplateEngine engine = new FreemarkerTemplateEngine();
					engine.init("templates"); // template location
					monitor.worked(1);
					
					IFile log4j = folder.getFile("log4j2.properties");
					try (Writer fileWriter = new FileWriter(log4j.getRawLocation().makeAbsolute().toFile())) {
						engine.setTemplate("log4j2.properties"); // .ftl
						engine.process(fileWriter, input);
						engine.flush();
						monitor.worked(1);
					} catch (IOException | TemplateException e) {
						throw new InvocationTargetException(e);
					}

					// update the UI
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								parent.refreshLocal(IResource.DEPTH_INFINITE, null);
							} catch (CoreException e) {
								pageOne.setErrorMessage(e.getMessage());
							}
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IViewPart view = page.findView(IPageLayout.ID_PROJECT_EXPLORER);
							// select logstash.conf file, the one that needs editing
							((ISetSelectionTarget) view).selectReveal(new StructuredSelection(log4j));
						}
					});
					monitor.done();
				}
			});
		} catch (InterruptedException e) {
			pageOne.setErrorMessage(e.getMessage());
			return false;
		} catch (InvocationTargetException e) {
			pageOne.setErrorMessage(e.getTargetException().getMessage());
			return false;
		}

		return true;
	}

}

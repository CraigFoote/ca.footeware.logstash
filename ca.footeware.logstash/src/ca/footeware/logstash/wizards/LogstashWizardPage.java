package ca.footeware.logstash.wizards;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import ca.footeware.logstash.contentproviders.FileTreeContentProvider;

public class LogstashWizardPage extends WizardPage {
	private IStructuredSelection selection;
	private Text parentText;
	private IProject parentProject;
	private Text nameText;
	private Text portText;
	private Text hostText;
	private Text jiraText;

	/**
	 * Constructor.
	 * 
	 * @param selection
	 *            {@link IStructuredSelection}
	 */
	public LogstashWizardPage(IStructuredSelection selection) {
		super("Logstash Wizard - Page One");
		this.selection = selection;
		setTitle("Create a new Logstash Configuration.");
		setDescription("Fill in the form to create a new Logstash configuration folder with support files.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;

		// parent project
		Label parentLabel = new Label(container, SWT.NONE);
		parentLabel.setText("Parent Project");
		parentText = new Text(container, SWT.BORDER | SWT.SINGLE);
		parentText.setEditable(false);
		parentText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		GridDataFactory.defaultsFor(parentText).grab(true, false).applyTo(parentText);
		// display initial selection in text widget
		if (selection instanceof IStructuredSelection) {
			IProject project = (IProject) ((IStructuredSelection) selection).getFirstElement();
			if (project != null) {
				parentProject = project;
				parentText.setText(project.getName());
			}
		}
		// browse button
		Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						Display.getDefault().getActiveShell(), new WorkbenchLabelProvider(),
						new FileTreeContentProvider());

				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
				dialog.setAllowMultiple(false);
				dialog.addFilter(new ViewerFilter() {
					@Override
					public boolean select(Viewer viewer, Object parentElement, Object element) {
						return element instanceof IProject;
					}
				});
				dialog.setInitialSelection(selection.getFirstElement());

				int open = dialog.open();
				if (open == Window.OK) {
					Object result = dialog.getFirstResult();
					if (result instanceof IProject) {
						IProject project = (IProject) result;
						parentProject = project;
						parentText.setText(project.getName());
					}
				}
			}
		});

		// config name
		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Configuration Name");
		nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		nameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		GridDataFactory.defaultsFor(nameText).span(2, 1).applyTo(nameText);

		// admin port
		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setText("Admin. Port Number");
		portText = new Text(container, SWT.BORDER | SWT.SINGLE);
		portText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		portText.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				char newChar = e.character;
				if (Character.isDigit(newChar) || e.keyCode == SWT.BS || e.keyCode == SWT.DEL
						|| e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT) {
					e.doit = true;
				} else {
					e.doit = false;
				}
			}
		});
		GridDataFactory.defaultsFor(portText).span(2, 1).applyTo(portText);

		// host
		Label hostLabel = new Label(container, SWT.NONE);
		hostLabel.setText("Host Name");
		hostText = new Text(container, SWT.BORDER | SWT.SINGLE);
		hostText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		GridDataFactory.defaultsFor(hostText).span(2, 1).applyTo(hostText);

		// host
		Label jiraLabel = new Label(container, SWT.NONE);
		jiraLabel.setText("JIRA Task");
		jiraText = new Text(container, SWT.BORDER | SWT.SINGLE);
		jiraText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validate();
			}
		});
		GridDataFactory.defaultsFor(jiraText).span(2, 1).applyTo(jiraText);

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);

	}

	protected void validate() {
		if (!nameText.getText().trim().isEmpty() && !portText.getText().trim().isEmpty()
				&& !hostText.getText().trim().isEmpty() && !jiraText.getText().trim().isEmpty()) {
			setPageComplete(true);
		}
	}

	public String getName() {
		return nameText.getText().trim();
	}

	public IProject getParent() {
		return parentProject;
	}

	public String getPort() {
		return portText.getText().trim();
	}

	public String getHost() {
		return hostText.getText().trim();
	}

	public String getJira() {
		return jiraText.getText().trim();
	}

}
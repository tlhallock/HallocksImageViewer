package iv.main;


import iv.main.history.HistoryTextField;

import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author trever
 */
public class MainApplicationFrame extends javax.swing.JFrame implements KeyListener {

	int currentIndex;
	String lastWorkspaceLocation;
	
	ImageSet entries;
	Preferences preferences;

	ImagePanel theImagePanel;
	ImageEntry currentEntry;

	Logger logger;
	ArrayTraversalPolicy policy;
	ButtonEnabler locEnabler;
	ButtonEnabler saveEnabler;

	public MainApplicationFrame()
	{
		initComponents();
		preferences = new Preferences();
		entries = new ImageSet();

		locEnabler = new ButtonEnabler(changeFilenameButton);
		locEnabler.put("loc", jTextField2);
		saveEnabler = new ButtonEnabler(saveCurrentButton);
		saveEnabler.put("description", jTextArea1);
		saveEnabler.put("location", jTextField1);
		saveEnabler.put("people", peopleField);

		logger = new PanelLogger(logLabel, jProgressBar1);

		policy = new ArrayTraversalPolicy();
		policy.add(jTextField1);
		policy.add(jTextArea1);
		policy.add(peopleField);
		policy.add(saveCurrentButton);
		policy.addAndSetDefault(nextButton);
//		policy.add(previousButton);
		setFocusTraversalPolicy(policy);

		saveCurrentButton.addKeyListener(new PerformOnEnterListener(saveCurrentButton));
		previousButton.addKeyListener(new PerformOnEnterListener(previousButton));
		nextButton.addKeyListener(new PerformOnEnterListener(nextButton));
		
		jTextArea1.addFocusListener(new EverythingSelector(jTextArea1));
		jTextField1.addFocusListener(new EverythingSelector(jTextField1));

		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("pressed TAB")));
		jTextArea1.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke.getKeyStroke("shift pressed TAB")));
		jTextArea1.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, strokes);

		addKeyListener(this);
		
		locationField.setText("");
		
		resetView();
	}

	void setWorkspace(Workspace parse, String file)
	{
		stopperLock.lock();
		try
		{
			if (stopper != null)
				stopper.requestStop();
			openWorkspaceLocationButton.setEnabled(false);
			if (file != null)
				this.lastWorkspaceLocation = file;

			// jTextField1.setText(parse.location.path.toString());
			// recurseOption.setSelected(parse.location.recurse);
			parse.verifyImages(logger);

			preferences = parse.preferences;
			entries = parse.images;
			
			((HistoryTextField) jTextField1).clear();
			((HistoryTextField) jTextField1).addAll(parse.images.collectAllLocations());
			((HistoryTextField) jTextField1).addAll(parse.locationHistory);

			setCurrentIndex(0);
		}
		finally
		{
			stopperLock.unlock();
			openWorkspaceLocationButton.setEnabled(true);
		}
	}

	private Workspace createWorkspace()
	{
		return new Workspace(
			entries,
			preferences, 
			((HistoryTextField) jTextField1).getHistory());
	}

	private void resetView()
	{
		if (entries.size() > 0)
		{
			setCurrentIndex(0);
			return;
		}

		currentEntry = null;
		
		locEnabler.clear();
		saveEnabler.clear();

		jLabel9.setText("0 of 0");
		jLabel8.setText("");
		jLabel4.setText("");
		
		previousButton.setEnabled(false);
		unusedButton.setEnabled(false);
		nextButton.setEnabled(false);
		saveCurrentButton.setEnabled(false);
		fastBackwardButton.setEnabled(false);
		lastButton.setEnabled(false);
		fastForwardButton.setEnabled(false);
		firstButton.setEnabled(false);
		
		jTextField1.setText("");
		jTextField1.setEditable(false);
		jTextField1.setEnabled(false);
		
		changeFilenameButton.setEnabled(false);
		jTextField2.setText("");
		jTextField2.setEditable(false);
		jTextField2.setEnabled(false);
		
		jTextArea1.setText("");
		jTextArea1.setEditable(false);
		jTextArea1.setEnabled(false);
		
		jLabel11.setText("");
		jLabel13.setText("");

		peopleField.setText("");
		peopleField.setEditable(false);
		peopleField.setEnabled(false);

		theImagePanel.setText("No image");

		logger.log("Viewing nothing");
		logger.setProgress(0, 0);
	}

	private void open()
	{
		new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground()
			{
				readAllImages();
				return null;
			}
		}.execute();
	}

	public void setCurrentImage(ImageEntry entry, int position, int maximum)
	{
		if (entry == null)
		{
			resetView();
			return;
		}

		currentEntry = entry;
		try
		{
			BufferedImage read = entries.getImage(entry);
			theImagePanel.setImage(read);
		}
		catch (IOException ex)
		{
			theImagePanel.setText("Failed to load image");
			ex.printStackTrace();
		}

		String cd = entry.getDescription();
		saveEnabler.currentValues.put("description", cd);
		String cl = entry.getLocation();
		saveEnabler.currentValues.put("location", cl);
		String pp = entry.getPeople();
		saveEnabler.currentValues.put("people", pp);
		String il = entry.getImageFilename();
		locEnabler.currentValues.put("loc", il);
		
		
		jTextField2.setText(il);
		jTextArea1.setText(cd);
		jTextField1.setText(cl);
		
		peopleField.setEnabled(true);
		peopleField.setEditable(true);
		peopleField.setText(entry.getPeople());
		
		jTextField2.setEditable(true);
		jTextField2.setEnabled(true);
		jLabel4.setText("coming soon.");
		jLabel8.setText(entry.getImageTime());
		jLabel9.setText((position + 1) + " of " + maximum);
		jTextArea1.setEditable(true);
		jTextArea1.setEnabled(true);
		jTextField1.setEditable(true);
		jTextField1.setEnabled(true);
		
		boolean notAtBegin = position > 0;
		boolean notAtEnd = position < maximum - 1;
		
		previousButton.setEnabled(notAtBegin);
		fastBackwardButton.setEnabled(notAtBegin);
		firstButton.setEnabled(notAtBegin);
		
		nextButton.setEnabled(notAtEnd);
		fastForwardButton.setEnabled(notAtEnd);
		lastButton.setEnabled(notAtEnd);
		
		jLabel11.setText(entry.getChecksum());
		jLabel13.setText(entry.getLastModifed());

		unusedButton.setEnabled(true);

		// if (policy.getDefaultComponent() != null)
		// policy.getDefaultComponent().requestFocus();

		logger.log("Loaded image " + entry.getImageFilename());
		
		System.out.println("convert " + entry.getImageFilename() + " -rotate 90 " + entry.getImageFilename());
	}

	private void setCurrentIndex(int newIndex)
	{
		currentIndex = Math.max(0, Math.min(newIndex, entries.size() - 1));
		if (currentIndex >= entries.size())
		{
			setCurrentImage(null, 0, 0);
			return;
		}
		setCurrentImage(entries.get(currentIndex), currentIndex, entries.size());
	}

	private void save()
	{
		String description = jTextArea1.getText();
		String location = jTextField1.getText();
		String people = peopleField.getText();
		currentEntry.setDescription(description);
		currentEntry.setLocation(location);
		currentEntry.setPeople(people);

		saveEnabler.currentValues.put("description", description);
		saveEnabler.currentValues.put("location", location);

		saveCurrentButton.setEnabled(false);
		
		if (lastWorkspaceLocation == null)
		{
			saveAWorkspace();
			return;
		}
		
		try
		{
			Workspace.save(createWorkspace(), new File(lastWorkspaceLocation));
			logger.log("Workspace saved to " + lastWorkspaceLocation);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.log("Unable to write to file");
		}
	}

	private void next()
	{
		setCurrentIndex(currentIndex + 1);
	}

	private void previous()
	{
		setCurrentIndex(currentIndex - 1);
	}

	private void first()
	{
		setCurrentIndex(0);
	}

	private void back10()
	{
		setCurrentIndex(currentIndex - 10);
	}

	private void skip10()
	{
		setCurrentIndex(currentIndex + 10);
	}

	private void last()
	{
		setCurrentIndex(entries.size() - 1);
	}

	private JPanel createImagePanel()
	{
		if (theImagePanel == null)
			theImagePanel = new ImagePanel();
		return theImagePanel;
	}

	private void exportToPdf()
	{
		JFileChooser choose = new JFileChooser();
		choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		choose.setMultiSelectionEnabled(false);
		if (choose.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		exportToPdf(choose.getSelectedFile());
	}
	void exportToPdf(final File p)
	{
		new SwingWorker() {
			@Override
			protected Object doInBackground() throws Exception
			{
				try
				{
					Workspace.exportToPdf(createWorkspace(), p, logger);
					logger.log("wrote tex file to " + p);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					logger.log("Unable to write " + p);
				}
				return null;
			}}.execute();
	}

	private void saveAWorkspace()
	{
		Workspace workspace = createWorkspace();
		JFileChooser choose = new JFileChooser();
		choose.setMultiSelectionEnabled(false);
		if (choose.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		try
		{
			Workspace.save(workspace, choose.getSelectedFile());
			lastWorkspaceLocation = choose.getSelectedFile().toString();
			logger.log("Saved workspace to " + choose.getSelectedFile());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.log("Unable to write " + choose.getSelectedFile());
			return;
		}
	}

	private void createNew()
	{
		setWorkspace(new Workspace(), null);
	}
	
	private void openAWorkspace()
	{
		JFileChooser choose = new JFileChooser();
		// if (lastOpen != null)
		// choose.setCurrentDirectory(new File(lastOpen));
		choose.setMultiSelectionEnabled(false);
		if (choose.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		Workspace parse;
		try
		{
			parse = Workspace.parse(choose.getSelectedFile(), logger);
			logger.log("Loaded workspace from " + choose.getSelectedFile());
			lastWorkspaceLocation = choose.getSelectedFile().getPath();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			logger.log("Unable to read " + choose.getSelectedFile());
			return;
		}
		setWorkspace(parse, null);
	}
	
	
	
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_O:
			if (e.isControlDown())
				openAWorkspace();
			break;
		case KeyEvent.VK_S:
			if (e.isControlDown())
				saveAWorkspace();
			break;
		case KeyEvent.VK_N:
			if (e.isControlDown())
				next();
			break;
		case KeyEvent.VK_P:
			if (e.isControlDown())
				previous();
			break;
		case KeyEvent.VK_E:
			if (e.isControlDown())
				exportToPdf();
			break;
		case KeyEvent.VK_ESCAPE:
			dispose();
			break;
		}
	}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        nextButton = new javax.swing.JButton();
        saveCurrentButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        unusedButton = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        changeFilenameButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lastButton = new javax.swing.JButton();
        fastForwardButton = new javax.swing.JButton();
        firstButton = new javax.swing.JButton();
        fastBackwardButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new HistoryTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        peopleField = new javax.swing.JTextField();
        imagePanel = createImagePanel();
        jLabel6 = new javax.swing.JLabel();
        locationField = new javax.swing.JTextField();
        recurseOption = new javax.swing.JCheckBox();
        openWorkspaceLocationButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        logLabel = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(300);

        jLabel2.setText("Image Filename:");

        jLabel3.setText("Description Filename:");

        jLabel4.setText("jLabel4");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        nextButton.setText("Next");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        saveCurrentButton.setText("Save");
        saveCurrentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCurrentButtonActionPerformed(evt);
            }
        });

        previousButton.setText("Previous");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });

        jLabel7.setText("Image date:");

        jLabel8.setText("jLabel8");

        unusedButton.setText("Remove");
        unusedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unusedButtonActionPerformed(evt);
            }
        });

        jTextField2.setText("jTextField2");

        changeFilenameButton.setText("Save");

        jLabel5.setText("Current Position:");

        jLabel9.setText("jLabel9");

        lastButton.setText("Last");
        lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastButtonActionPerformed(evt);
            }
        });

        fastForwardButton.setText("Skip 10");
        fastForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastForwardButtonActionPerformed(evt);
            }
        });

        firstButton.setText("First");
        firstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstButtonActionPerformed(evt);
            }
        });

        fastBackwardButton.setText("Prev 10");
        fastBackwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fastBackwardButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Currrent Location:");

        jTextField1.setText("jTextField1");

        jLabel10.setText("Checksum:");

        jLabel11.setText("jLabel11");

        jLabel12.setText("Last modified:");

        jLabel13.setText("jLabel13");

        jLabel14.setText("People:");

        peopleField.setText("jTextField3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(firstButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fastBackwardButton)
                        .addGap(2, 2, 2)
                        .addComponent(previousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 569, Short.MAX_VALUE)
                        .addComponent(unusedButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(saveCurrentButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(nextButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fastForwardButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lastButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel14))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jTextField2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(changeFilenameButton))
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(peopleField))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeFilenameButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(peopleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextButton)
                    .addComponent(saveCurrentButton)
                    .addComponent(previousButton)
                    .addComponent(unusedButton)
                    .addComponent(lastButton)
                    .addComponent(fastForwardButton)
                    .addComponent(firstButton)
                    .addComponent(fastBackwardButton))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        javax.swing.GroupLayout imagePanelLayout = new javax.swing.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 738, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(imagePanel);

        jLabel6.setText("Folder:");

        locationField.setText("jTextField1");
        locationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationFieldActionPerformed(evt);
            }
        });

        recurseOption.setText("recurse");

        openWorkspaceLocationButton.setText("Open");
        openWorkspaceLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openWorkspaceLocationButtonActionPerformed(evt);
            }
        });

        browseButton.setText("Browse...");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        logLabel.setText("jLabel5");

        jMenu1.setText("File");

        jMenuItem1.setText("Save workspace");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Open workspace");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText("Export to pdf");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText("Create New");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem3.setText("Preferences");
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1566, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(18, 18, 18)
                        .addComponent(locationField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(browseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(openWorkspaceLocationButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(recurseOption))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(locationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(recurseOption)
                    .addComponent(openWorkspaceLocationButton)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(logLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void saveCurrentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCurrentButtonActionPerformed
        save();
    }//GEN-LAST:event_saveCurrentButtonActionPerformed

	private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
        next();
    }//GEN-LAST:event_nextButtonActionPerformed


	private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed
        previous();
    }//GEN-LAST:event_previousButtonActionPerformed


	private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setMultiSelectionEnabled(false);
        if (jFileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;
        try {
            locationField.setText(jFileChooser.getSelectedFile().getCanonicalPath());
        } catch (IOException ex) {
            logger.log("Unable to open " + jFileChooser.getSelectedFile());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void openWorkspaceLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openWorkspaceLocationButtonActionPerformed
        open();
    }//GEN-LAST:event_openWorkspaceLocationButtonActionPerformed

    private void locationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationFieldActionPerformed
       open();
    }//GEN-LAST:event_locationFieldActionPerformed

    private void lastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastButtonActionPerformed
        last();
    }//GEN-LAST:event_lastButtonActionPerformed


	private void fastForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastForwardButtonActionPerformed
       skip10();
    }//GEN-LAST:event_fastForwardButtonActionPerformed


	private void fastBackwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fastBackwardButtonActionPerformed
        back10();
    }//GEN-LAST:event_fastBackwardButtonActionPerformed


	private void firstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstButtonActionPerformed
        first();
    }//GEN-LAST:event_firstButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
		saveAWorkspace();
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed


	private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
		openAWorkspace();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void unusedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unusedButtonActionPerformed
        if (currentEntry != null)
			entries.remove(currentEntry);
        setCurrentIndex(currentIndex);
		if (lastWorkspaceLocation != null) {
			try {
				Workspace.save(createWorkspace(), new File(lastWorkspaceLocation));
				logger.log("Saved workspace to " + lastWorkspaceLocation);
			} catch (IOException e) {
				e.printStackTrace();
				logger.log("Unable to write " + lastWorkspaceLocation);
				return;
			}
		}
    }//GEN-LAST:event_unusedButtonActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        exportToPdf();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        createNew();
    }//GEN-LAST:event_jMenuItem5ActionPerformed


	/**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainApplicationFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//	public void run() {
//                new MainApplicationFrame().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton changeFilenameButton;
    private javax.swing.JButton fastBackwardButton;
    private javax.swing.JButton fastForwardButton;
    private javax.swing.JButton firstButton;
    private javax.swing.JPanel imagePanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JButton lastButton;
    private javax.swing.JTextField locationField;
    private javax.swing.JLabel logLabel;
    private javax.swing.JButton nextButton;
    private javax.swing.JButton openWorkspaceLocationButton;
    private javax.swing.JTextField peopleField;
    private javax.swing.JButton previousButton;
    private javax.swing.JCheckBox recurseOption;
    private javax.swing.JButton saveCurrentButton;
    private javax.swing.JButton unusedButton;
    // End of variables declaration//GEN-END:variables
    
    
    
    
    
    
    
    
    
    
    
    
    
	private static class ButtonEnabler implements DocumentListener
	{
		private HashMap<String, String> currentValues = new HashMap<>();
		private HashMap<String, JTextComponent> fields = new HashMap<>();
		private JButton button;

		ButtonEnabler(JButton button)
		{
			this.button = button;
		}

		public void clear()
		{
			currentValues.clear();
		}

		public void put(String string, JTextComponent field)
		{
			fields.put(string, field);
			field.getDocument().addDocumentListener(this);
		}

		@Override
		public void changedUpdate(DocumentEvent arg0)
		{
			changed();
		}

		@Override
		public void insertUpdate(DocumentEvent arg0)
		{
			changed();
		}

		@Override
		public void removeUpdate(DocumentEvent arg0)
		{
			changed();
		}

		private boolean shouldBeEnabled()
		{
			for (Entry<String, JTextComponent> comp : fields.entrySet())
			{
				String val = currentValues.get(comp.getKey());
				if (val == null)
				{
					return false;
				}
				if (comp.getValue().getText().equals(val))
					continue;
				return true;
			}
			return false;
		}

		private void changed()
		{
			button.setEnabled(shouldBeEnabled());
		}
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	private final Timer killTimer = new Timer();
	private Lock stopperLock = new ReentrantLock();
	private SearchStopper stopper;

	public final class SearchStopper extends TimerTask
	{
		boolean stopped;
		boolean pleaseStop;
		Thread t = Thread.currentThread();
		private boolean changedButton;

		private void start()
		{
			stopper = this;
			openWorkspaceLocationButton.setText("Cancel");
			changedButton = true;
		}

		public boolean shouldStop()
		{
			return pleaseStop;
		}

		private void stop()
		{
			stopperLock.lock();
			try
			{
				if (stopped || !changedButton)
					return;
				stopper = null;
				stopped = true;
				logger.setProgress(0, 1);
				openWorkspaceLocationButton.setText("Open");
			}
			finally
			{
				stopperLock.unlock();
			}
		}

		public void requestStop()
		{
			pleaseStop = true;
			killTimer.schedule(this, 1000);
		}

		public void stopViolently()
		{
			t.interrupt();
		}

		@Override
		public void run()
		{
			stopperLock.lock();
			try
			{
				if (!stopped)
					stopViolently();
			}
			finally
			{
				stopperLock.unlock();
			}
		}
	}

	private void readAllImages()
	{
		String oldName = Thread.currentThread().getName();

		stopperLock.lock();
		try
		{
			if (stopper != null)
			{
				if (!openWorkspaceLocationButton.getText().equals("Cancel"))
					throw new RuntimeException();
				stopper.requestStop();
				return;
			}

			stopper = new SearchStopper();
			try
			{
				stopper.start();
				Thread.currentThread().setName("Searching thread");
				stopperLock.unlock();

				try
				{
					ImageFinder.dumpAllImages(entries,
							Paths.get(locationField.getText()),
							this.recurseOption.isSelected(),
							preferences,
							stopper,
							logger);
				}
				catch (IOException | InterruptedException e)
				{
					System.out.println("Interrupted.");
				}

				stopperLock.lock();

				((HistoryTextField) jTextField1).addAll(entries.collectAllLocations());
				setCurrentIndex(0);
			}
			finally
			{
				Thread.currentThread().setName(oldName);
				stopper.stop();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			stopperLock.unlock();
		}
	}
}

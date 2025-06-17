package com.clock.view;

import com.clock.controller.CountdownController;
import com.clock.model.CountdownTimer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

public class CountdownGUI extends JFrame {
    private CountdownController controller;
    private JPanel mainPanel;
    private Timer updateTimer;
    private JList<CountdownTimer> timerList;
    private DefaultListModel<CountdownTimer> listModel;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    // Colors for modern theme
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color LIGHT_GRAY = new Color(248, 249, 250);
    private static final Color DARK_GRAY = new Color(52, 58, 64);

    public CountdownGUI(CountdownController controller) {
        this.controller = controller;
        this.listModel = new DefaultListModel<>();

        setTitle("🕐 Countdown Timer Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(true);

        // Set modern look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Fallback to default look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Add window listener to save data when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.saveTimers();
            }
        });

        initializeComponents();
        setupLayout();
        startUpdateTimer();
        loadExistingTimers();
    }

    private void loadExistingTimers() {
        for (CountdownTimer timer : controller.getAllTimers()) {
            listModel.addElement(timer);
        }
        updateStatusBar();
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(LIGHT_GRAY);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();

        // Create timer list with improved styling
        timerList = new JList<>(listModel);
        timerList.setCellRenderer(new ModernTimerListRenderer());
        timerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        timerList.setBackground(Color.WHITE);
        timerList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        timerList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(timerList);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                "Active Timers",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font(Font.SANS_SERIF, Font.BOLD, 14),
                PRIMARY_COLOR
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Create modern control panel
        JPanel controlPanel = createModernControlPanel();

        // Create status bar
        JPanel statusPanel = createStatusPanel();

        // Add components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_GRAY);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Countdown Timer Manager", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(DARK_GRAY);

        JLabel subtitleLabel = new JLabel("Manage and track your countdown timers", JLabel.CENTER);
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(LIGHT_GRAY);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createModernControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBackground(LIGHT_GRAY);
        controlPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        // Create styled buttons
        JButton createButton = createStyledButton("➕ New Timer", PRIMARY_COLOR);
        JButton startButton = createStyledButton("▶️ Start", SUCCESS_COLOR);
        JButton stopButton = createStyledButton("⏸️ Stop", WARNING_COLOR);
        JButton resetButton = createStyledButton("🔄 Reset", new Color(108, 117, 125));
        JButton removeButton = createStyledButton("🗑️ Remove", DANGER_COLOR);
        JButton historyButton = createStyledButton("📊 History", new Color(111, 66, 193));

        // Add buttons with spacing
        controlPanel.add(createButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(startButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(stopButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(resetButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(removeButton);
        controlPanel.add(Box.createVerticalStrut(10));
        controlPanel.add(historyButton);
        controlPanel.add(Box.createVerticalGlue());

        // Add action listeners
        createButton.addActionListener(e -> createNewTimer());
        startButton.addActionListener(e -> controlSelectedTimer(TimerAction.START));
        stopButton.addActionListener(e -> controlSelectedTimer(TimerAction.STOP));
        resetButton.addActionListener(e -> controlSelectedTimer(TimerAction.RESET));
        removeButton.addActionListener(e -> removeSelectedTimer());
        historyButton.addActionListener(e -> showCompletionHistory());

        return controlPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(140, 40));
        button.setMaximumSize(new Dimension(140, 40));
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(DARK_GRAY);
        statusPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setBackground(Color.WHITE);
        progressBar.setForeground(PRIMARY_COLOR);

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);

        return statusPanel;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timerList.repaint();
                    updateStatusBar();
                });
            }
        }, 0, 100); // Update every 100ms for smoother animation
    }

    private void updateStatusBar() {
        int totalTimers = listModel.getSize();
        int runningTimers = 0;
        int completedTimers = 0;

        for (int i = 0; i < totalTimers; i++) {
            CountdownTimer timer = listModel.getElementAt(i);
            if (timer.isRunning()) runningTimers++;
            if (timer.isCompleted()) completedTimers++;
        }

        statusLabel.setText(String.format("Total: %d | Running: %d | Completed: %d",
                totalTimers, runningTimers, completedTimers));

        // Update progress bar for selected timer
        CountdownTimer selected = timerList.getSelectedValue();
        if (selected != null && !selected.isCompleted()) {
            long total = selected.getTargetDuration().toMillis();
            long remaining = selected.getRemainingTime().toMillis();
            int progress = (int) ((total - remaining) * 100 / total);
            progressBar.setValue(progress);
            progressBar.setString(String.format("%d%%", progress));
        } else {
            progressBar.setValue(0);
            progressBar.setString("");
        }
    }

    private void createNewTimer() {
        JDialog dialog = new JDialog(this, "Create New Timer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        // Timer name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(new JLabel("Timer Name:"), gbc);

        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(nameField, gbc);

        // Duration inputs
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(15, 0, 5, 0);
        contentPanel.add(new JLabel("Duration:"), gbc);

        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        JSpinner minutesSpinner = new JSpinner(new SpinnerNumberModel(5, 0, 59, 1)); // Default 5 minutes
        JSpinner secondsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        // Style spinners
        styleSpinner(hoursSpinner);
        styleSpinner(minutesSpinner);
        styleSpinner(secondsSpinner);

        durationPanel.add(hoursSpinner);
        durationPanel.add(new JLabel(" h "));
        durationPanel.add(minutesSpinner);
        durationPanel.add(new JLabel(" m "));
        durationPanel.add(secondsSpinner);
        durationPanel.add(new JLabel(" s"));

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(durationPanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = createStyledButton("Create", SUCCESS_COLOR);
        JButton cancelButton = createStyledButton("Cancel", new Color(108, 117, 125));

        okButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a timer name!",
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int hours = (Integer) hoursSpinner.getValue();
            int minutes = (Integer) minutesSpinner.getValue();
            int seconds = (Integer) secondsSpinner.getValue();

            Duration duration = Duration.ofHours(hours)
                    .plusMinutes(minutes)
                    .plusSeconds(seconds);

            if (duration.isZero()) {
                JOptionPane.showMessageDialog(dialog, "Duration cannot be zero!",
                        "Invalid Duration", JOptionPane.ERROR_MESSAGE);
                return;
            }

            CountdownTimer timer = controller.createTimer(name, duration);
            listModel.addElement(timer);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void styleSpinner(JSpinner spinner) {
        spinner.setPreferredSize(new Dimension(60, 30));
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    }

    private void controlSelectedTimer(TimerAction action) {
        CountdownTimer selected = timerList.getSelectedValue();
        if (selected == null) {
            showStyledMessage("Please select a timer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (action) {
            case START:
                controller.startTimer(selected);
                break;
            case STOP:
                controller.stopTimer(selected);
                break;
            case RESET:
                controller.resetTimer(selected);
                break;
        }
    }

    private void removeSelectedTimer() {
        CountdownTimer selected = timerList.getSelectedValue();
        if (selected != null) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to remove '" + selected.getName() + "'?",
                    "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                controller.removeTimer(selected);
                listModel.removeElement(selected);
            }
        }
    }

    private void showCompletionHistory() {
        CountdownTimer selected = timerList.getSelectedValue();
        if (selected == null) {
            showStyledMessage("Please select a timer first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CompletionHistoryDialog dialog = new CompletionHistoryDialog(this, selected);
        dialog.setVisible(true);
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private enum TimerAction {
        START, STOP, RESET
    }

    private class ModernTimerListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(10, 15, 10, 15));

            if (value instanceof CountdownTimer) {
                CountdownTimer timer = (CountdownTimer) value;

                // Timer name
                JLabel nameLabel = new JLabel(timer.getName());
                nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

                // Timer time
                JLabel timeLabel = new JLabel(formatDuration(timer.getRemainingTime()));
                timeLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));

                // Status indicator
                JLabel statusLabel = new JLabel();
                statusLabel.setOpaque(true);
                statusLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
                statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));

                if (timer.isCompleted()) {
                    statusLabel.setText("COMPLETED");
                    statusLabel.setBackground(SUCCESS_COLOR);
                    statusLabel.setForeground(Color.WHITE);
                    timeLabel.setForeground(SUCCESS_COLOR);
                } else if (timer.isRunning()) {
                    statusLabel.setText("RUNNING");
                    statusLabel.setBackground(PRIMARY_COLOR);
                    statusLabel.setForeground(Color.WHITE);
                    timeLabel.setForeground(PRIMARY_COLOR);
                } else {
                    statusLabel.setText("STOPPED");
                    statusLabel.setBackground(new Color(108, 117, 125));
                    statusLabel.setForeground(Color.WHITE);
                    timeLabel.setForeground(Color.GRAY);
                }

                // Layout
                JPanel topPanel = new JPanel(new BorderLayout());
                topPanel.add(nameLabel, BorderLayout.WEST);
                topPanel.add(statusLabel, BorderLayout.EAST);

                panel.add(topPanel, BorderLayout.NORTH);
                panel.add(timeLabel, BorderLayout.CENTER);

                // Selection styling
                if (isSelected) {
                    panel.setBackground(new Color(230, 240, 250));
                    panel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                            new EmptyBorder(8, 13, 8, 13)
                    ));
                } else {
                    panel.setBackground(Color.WHITE);
                }

                topPanel.setBackground(panel.getBackground());
            }

            return panel;
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
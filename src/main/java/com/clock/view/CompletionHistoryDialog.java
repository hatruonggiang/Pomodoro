package com.clock.view;

import com.clock.model.CountdownTimer;
import com.clock.model.TimerCompletion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class CompletionHistoryDialog extends JDialog {
    private final CountdownTimer timer;
    private JTable historyTable = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JLabel statsLabel = new JLabel();

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(64, 128, 255);
    private static final Color SECONDARY_COLOR = new Color(240, 248, 255);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color HEADER_COLOR = new Color(52, 73, 94);

    public CompletionHistoryDialog(Frame owner, CountdownTimer timer) {
        super(owner, "Timer Completion History", true);
        this.timer = timer;

        initializeComponents();
        setupLayout();
        styleComponents();
        updateHistory();

        // Dialog properties
        setSize(600, 450);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Create table model with columns
        String[] columnNames = {"#", "Completion Time", "Duration"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        statsLabel = new JLabel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Add padding to the main dialog
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Completion History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(HEADER_COLOR);

        // Timer name
        JLabel timerNameLabel = new JLabel("Timer: " + timer.getName());
        timerNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timerNameLabel.setForeground(TEXT_COLOR);

        // Stats label
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(PRIMARY_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(timerNameLabel, BorderLayout.CENTER);
        titlePanel.add(statsLabel, BorderLayout.SOUTH);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Add icon
        JLabel iconLabel = new JLabel("⏱️");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        headerPanel.add(iconLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Setup table
        setupTable();

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void setupTable() {
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        historyTable.setRowHeight(35);
        historyTable.setShowGrid(false);
        historyTable.setSelectionBackground(new Color(230, 240, 255));
        historyTable.setSelectionForeground(TEXT_COLOR);

        // Header styling
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        historyTable.getTableHeader().setBackground(HEADER_COLOR);
        historyTable.getTableHeader().setForeground(Color.WHITE);
        historyTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        // Custom cell renderer for alternating row colors
        historyTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(getBackground());

        // Clear history button
        JButton clearButton = createStyledButton("Clear History", "🗑️");
        clearButton.setBackground(new Color(231, 76, 60));
        clearButton.addActionListener(e -> clearHistory());

        // Refresh button
        JButton refreshButton = createStyledButton("Refresh", "🔄");
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.addActionListener(e -> updateHistory());

        // Close button
        JButton closeButton = createStyledButton("Close", "✖️");
        closeButton.setBackground(new Color(149, 165, 166));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        return buttonPanel;
    }

    private JButton createStyledButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
        });

        return button;
    }

    private void styleComponents() {
        // Set dialog background
        getContentPane().setBackground(Color.WHITE);
    }

    private void updateHistory() {
        List<TimerCompletion> history = timer.getCompletionHistory();

        // Clear existing data
        tableModel.setRowCount(0);

        if (history.isEmpty()) {
            // Show empty state
            JLabel emptyLabel = new JLabel("No completion history available yet");
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(149, 165, 166));
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);

            statsLabel.setText("Total completions: 0");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

        // Add data to table
        for (int i = 0; i < history.size(); i++) {
            TimerCompletion completion = history.get(i);
            Object[] row = {
                    i + 1,
                    formatter.format(completion.getCompletionTime().atZone(ZoneId.systemDefault())),
                    formatDuration(completion.getDuration().toMillis())
            };
            tableModel.addRow(row);
        }

        // Update stats
        updateStats(history);
    }

    private void updateStats(List<TimerCompletion> history) {
        int totalCompletions = history.size();
        String statsText = String.format("Total completions: %d", totalCompletions);

        if (totalCompletions > 0) {
            // Calculate average duration if available
            long totalSeconds = history.stream()
                    .mapToLong(c -> c.getDuration().toMillis())
                    .sum();
            long avgSeconds = totalSeconds / totalCompletions;
            statsText += String.format(" | Average duration: %s", formatDuration(avgSeconds));
        }

        statsLabel.setText(statsText);
    }

    private String formatDuration(long seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else if (seconds < 3600) {
            return String.format("%dm %ds", seconds / 60, seconds % 60);
        } else {
            return String.format("%dh %dm %ds",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60);
        }
    }

    private void clearHistory() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all completion history?\nThis action cannot be undone.",
                "Clear History",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            timer.getCompletionHistory().clear(); // Nhưng nên tránh, vì vi phạm tính đóng gói (encapsulation)
            updateHistory();
            JOptionPane.showMessageDialog(
                    this,
                    "Completion history has been cleared.",
                    "History Cleared",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // Custom table cell renderer for alternating row colors
    private class AlternatingRowRenderer extends JLabel implements TableCellRenderer {
        public AlternatingRowRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            setText(value != null ? value.toString() : "");
            setFont(table.getFont());

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(new Color(248, 249, 250));
                }
                setForeground(TEXT_COLOR);
            }

            // Center align the first column (numbers)
            if (column == 0) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            setBorder(new EmptyBorder(5, 10, 5, 10));

            return this;
        }
    }
}
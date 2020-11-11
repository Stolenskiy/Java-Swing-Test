package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class ScreenManager {
	private JFrame introFrame;
	private JFrame sortFrame;
	private List<JButton> btnList = new ArrayList<>();
	private boolean sortByUp = false;
	private Timer timer;

	class Animation implements ActionListener {
		private JComponent jComponent;
		private Long currentTime;
		private Random random;
		private int startX;
		private int startY;

		public Animation(JComponent jComponent) {
			this.jComponent = jComponent;
			startX = jComponent.getX();
			startY = jComponent.getY();
			random = new Random();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (currentTime == null) {
				currentTime = System.currentTimeMillis();
			}
			int x = jComponent.getX();
			int y = jComponent.getY();

			jComponent.setLocation(x + random.nextInt(3) - 1, y + random.nextInt(3) - 1);

			if (System.currentTimeMillis() - currentTime >= 150) {
				jComponent.setLocation(this.startX, this.startY);
				currentTime = null;
				timer.stop();
			}
		}
	}

	private void enterBtnPress(JTextField textField, JLabel label) {
		if (textField.getText().length() == 0) {
			timer.start();
			label.setText("Please enter a number less than 1000 and more than 0!");
			label.setBounds(85, 100, 430, 25);
			label.setForeground(Color.RED);
			textField.setBorder(new LineBorder(Color.RED, 1));
		} else {
			introFrame.setVisible(false);
			initSortScreen(Integer.parseInt(textField.getText()));
		}
	}
	public void initIntroScreen() {
		introFrame = new JFrame("Intro screen");
		JLabel label = new JLabel("How many numbers to display?");
		JTextField textField = new JTextField(4);
		JButton btn = new JButton("Enter");
		JPanel panel = new JPanel();
		panel.setLayout(null);

		label.setBounds(175, 100, 250, 25);
		label.setFont(new Font("Serif", Font.PLAIN, 20));
		panel.add(label);

		textField.setFont(label.getFont());
		textField.setBounds(240, 140, 120, 25);
		textField.setBorder(new LineBorder(new Color(66, 114, 196), 1));
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() < 48 || e.getKeyChar() > 57) {
					e.consume();
				} else {
					textField.setBorder(new LineBorder(new Color(66, 114, 196), 1));
					if (textField.getText().length() > 0) {
						if (Integer.parseInt(textField.getText() + e.getKeyChar()) > 1000) {
							e.consume();
						}
					} else if (e.getKeyChar() == '0') {
						e.consume();
					}
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 10) {
					enterBtnPress(textField, label);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		});
		panel.add(textField);

		btn.setBounds(240, 180, 120, 25);
		Animation animation = new Animation(textField);
		timer = new Timer(1000 / 60, animation);
		btn.addActionListener(e -> enterBtnPress(textField, label));
		setButtonStyle(btn, new Color(66, 114, 196));
		panel.add(btn);

		introFrame.setSize(600, 400);
		introFrame.setBackground(Color.WHITE);
		introFrame.add(panel);
		introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		introFrame.setLocationRelativeTo(null);
		introFrame.setVisible(true);
	}

	private void locateNumBtn(List<JButton> btnList,  JPanel panel) {
		int x = 0;
		int y = 0;
		int stepY = 0;
		for (int i = 0; i < btnList.size(); i++) {
			JButton btn = btnList.get(i);
			GridBagConstraints gbc =
					new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridx = x;
			gbc.gridy = y;
			panel.add(btn, gbc);
			y ++;
			if ((i + 1) % 10 == 0) {
				x ++;
				y = stepY;
			}
		}
	}

	private void randValForNumBtns(List<JButton> btnList) {
		int rV;
		Random random = new Random();
		for (int i = 0; i < btnList.size(); i++) {
			JButton btn = btnList.get(i);
			rV = random.nextInt(1000) + 1;
			btn.setText(String.valueOf(rV));
		}
		btnList.get(random.nextInt(btnList.size())).setText(String.valueOf(random.nextInt(30) + 1));
	}

	public void initSortScreen(int countBtn) {
		sortFrame = new JFrame("Intro screen");
		JButton sortBtn = new JButton("Sort");
		JButton resetBtn = new JButton("Reset");

		JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new GridBagLayout());

		JPanel navigationPanel = new JPanel();
		navigationPanel.setLayout(new GridLayout(2, 1, 20, 10));

		sortBtn.setSize(120, 25);
		resetBtn.setSize(120, 25);

		navigationPanel.add(sortBtn);
		navigationPanel.add(resetBtn);

		setButtonStyle(sortBtn, new Color(0, 176, 80));
		setButtonStyle(resetBtn, new Color(0, 176, 80));

		resetBtn.addActionListener(e -> {
			sortFrame.dispose();
			initIntroScreen();
		});
		sortBtn.addActionListener(e -> {
			btnList.sort(Comparator.comparing(btn -> sortByUp ? Integer.valueOf(btn.getText()) : -Integer.valueOf(btn.getText())));
			locateNumBtn(btnList, btnPanel);
			sortByUp = !sortByUp;
			sortFrame.invalidate();
			sortFrame.revalidate();
			sortFrame.repaint();
		});

		btnList = new ArrayList<>();
		for (int i = 0; i < countBtn; i++) {
			JButton btn = new JButton();
			btnList.add(btn);
			btn.addActionListener(e -> {
				if (Integer.valueOf(btn.getText()) <= 30) {
					randValForNumBtns(btnList);
					locateNumBtn(btnList, btnPanel);
					sortByUp = false;
				} else {
					JOptionPane.showMessageDialog(sortFrame,"Please select a value smaller or equal to 30.");
				}
			});
			setButtonStyle(btn, new Color(66, 114, 196));
		}

		randValForNumBtns(btnList);
		locateNumBtn(btnList, btnPanel);

		JScrollPane scrollPane = new JScrollPane(btnPanel,   ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setPreferredSize(new Dimension(600, 600));

		sortFrame.setSize(800, 435);
		sortFrame.setBackground(Color.WHITE);

		JPanel panel = new JPanel();
		panel.add(navigationPanel, BorderLayout.NORTH);

		sortFrame.add(scrollPane);
		sortFrame.add(panel, BorderLayout.EAST);
		sortFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sortFrame.setLocationRelativeTo(null);
		sortFrame.setVisible(true);
	}

	private void setButtonStyle(JButton btn, Color backgRoundColor) {
		btn.setBackground(backgRoundColor);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Serif", Font.PLAIN, 20));
	}

	public static void main(String[] args) {
		new ScreenManager().initIntroScreen();
	}
}

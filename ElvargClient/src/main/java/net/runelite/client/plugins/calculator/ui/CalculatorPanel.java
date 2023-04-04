/*
 * Copyright (c) 2020, Spedwards <https://github.com/Spedwards>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.calculator.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import net.runelite.client.plugins.calculator.CalculatorPlugin;
import net.runelite.client.util.ImageUtil;
import org.apache.commons.lang3.StringUtils;

public class CalculatorPanel extends JPanel
{
	private static final ImageIcon PLUS_MINUS_ICON;
	private static final Insets INSETS_LEFT_BORDER = new Insets(1, 0, 1, 1);
	private static final Insets INSETS_RIGHT_BORDER = new Insets(1, 1, 1, 0);
	private static final Insets INSETS = new Insets(1, 1, 1, 1);

	static
	{
		final BufferedImage plusMinusIcon = ImageUtil.resizeImage(ImageUtil.getResourceStreamFromClass(CalculatorPlugin.class, "plus_minus_icon.png"), 25, 25);
		PLUS_MINUS_ICON = new ImageIcon(plusMinusIcon);
	}

	private final CalculatorPluginPanel panel;
	private final DisplayField displayField;
	private final GridBagConstraints c;

	protected CalculatorPanel(CalculatorPluginPanel panel)
	{
		super();

		this.panel = panel;
		this.displayField = panel.getDisplayField();

		setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		CalculatorButton plusMinus = new CalculatorButton(PLUS_MINUS_ICON);

		addButton("+");
		addButton("7");
		addButton("8");
		addButton("9");
		
		addButton("-");
		addButton("4");
		addButton("5");
		addButton("6");
		
		addButton("*");
		addButton("1");
		addButton("2");
		addButton("3");
		
		addButton("/");
		addButton("0");
		addComp(plusMinus);
		addButton("=");

		addButton("C");
		c.gridwidth = 3;
		addButton("Clear History");

		plusMinus.addActionListener(e ->
		{
			if (displayField.isFinished())
			{
				displayField.reset();
				if (displayField.getPreviousResult() != null)
				{
					displayField.setNum1(displayField.getPreviousResult());
				}
				else
				{
					displayField.setNum1(0);
				}
				displayField.setNum1(displayField.getNum1() * -1);
				displayField.setFinished(false);
			}
			else
			{
				if (displayField.getCalculatorAction() == null)
				{
					displayField.setNum1(displayField.getNum1() * -1);
				}
				else
				{
					Integer num2 = displayField.getNum2();
					if (num2 == null)
					{
						num2 = 0;
					}
					displayField.setNum2(num2 * -1);
				}
			}
			displayField.update();
		});
	}

	private void addButton(String key)
	{
		CalculatorButton btn = new CalculatorButton(key);
		btn.addActionListener(e ->
		{
			String text = btn.getText();
			if (text.equals("="))
			{
				displayField.calculateResult();
				if (displayField.getResult() == null)
				{
					// Divide by 0 error occured
					return;
				}
				// Add new calculation to history before the displayField is updated
				panel.getHistoryPanel().addHistoryItem(displayField.getText() + " =", displayField.getResult().toString());
			}
			else if (text.equals("C"))
			{
				displayField.clear();
				return;
			}
			else if (text.equals("Clear History"))
			{
				panel.getHistoryPanel().clearHistory();
				return;
			}
			else if (StringUtils.isNumeric(text))
			{
				int num = Integer.parseInt(text);
				// Previous calculation has finised. Start again
				if (displayField.isFinished())
				{
					displayField.reset();
					displayField.setNum1(num);
					displayField.setFinished(false);
				}
				else
				{
					// If there is no action saved, assume we're working with num1
					if (displayField.getCalculatorAction() == null)
					{
						Integer num1 = displayField.getNum1();
						if (num1 == 0)
						{
							if (displayField.num1IsNegativeZero())
							{
								num *= -1;
							}
							displayField.setNum1(num);
						}
						else
						{
							if (num1 < 0)
							{
								displayField.setNum1(num1 * 10 - num);
							}
							else
							{
								displayField.setNum1(num1 * 10 + num);
							}
						}
					}
					else
					{
						Integer num2 = displayField.getNum2();
						if (num2 == null || num2 == 0)
						{
							if (displayField.num2IsNegativeZero())
							{
								num *= -1;
							}
							displayField.setNum2(num);
						}
						else
						{
							if (num2 < 0)
							{
								displayField.setNum2(num2 * 10 - num);
							}
							else
							{
								displayField.setNum2(num2 * 10 + num);
							}
						}
					}
				}
			}
			else
			{
				// If the calculation is finished and there's a previous result to work from,
				// set num1 as the previous result and continue
				if (displayField.isFinished() && displayField.getPreviousResult() != null)
				{
					displayField.reset();
					displayField.setNum1(displayField.getPreviousResult());
					displayField.setFinished(false);
					displayField.update();
				}
				if (displayField.getNum1() != null)
				{
					switch (text)
					{
						case "+":
							displayField.setCalculatorAction(DisplayField.Action.ADDITION);
							break;
						case "-":
							displayField.setCalculatorAction(DisplayField.Action.SUBTRACTION);
							break;
						case "*":
							displayField.setCalculatorAction(DisplayField.Action.MULTIPLICATION);
							break;
						case "/":
							displayField.setCalculatorAction(DisplayField.Action.DIVISION);
							break;
					}
				}
				displayField.setNum2(null);
			}
			displayField.update();
		});
		addComp(btn);
	}

	private void addComp(Component component)
	{
		switch (c.gridx)
		{
			case 0:
				c.insets = INSETS_LEFT_BORDER;
				break;
			case 3:
				c.insets = INSETS_RIGHT_BORDER;
				break;
			default:
				c.insets = INSETS;
		}
		if (c.gridwidth == 3)
		{
			c.insets = INSETS_RIGHT_BORDER;
		}
		add(component, c);
		c.gridx = ++c.gridx % 4;
		c.gridy = c.gridx == 0 ? ++c.gridy : c.gridy;
	}
}

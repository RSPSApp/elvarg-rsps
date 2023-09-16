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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import net.runelite.client.plugins.calculator.CalculatorPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

public class HistoryItemPanel extends JPanel
{
	private static final ImageIcon CLIPBOARD_ICON;

	static
	{
		final BufferedImage clipboardIcon = ImageUtil.resizeImage(ImageUtil.getResourceStreamFromClass(CalculatorPlugin.class, "clipboard_icon.png"), 20, 20);
		CLIPBOARD_ICON = new ImageIcon(clipboardIcon);
	}

	public HistoryItemPanel(String line1, String line2)
	{
		super();

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setBorder(new MatteBorder(0, 5, 0, 0, ColorScheme.BRAND_ORANGE));

		JPanel text = new JPanel(new BorderLayout());
		text.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		JLabel lineOneLabel = new JLabel(line1);
		lineOneLabel.setHorizontalAlignment(JLabel.CENTER);
		JLabel lineTwoLabel = new JLabel(line2);
		lineTwoLabel.setHorizontalAlignment(JLabel.CENTER);
		text.add(lineOneLabel, BorderLayout.NORTH);
		text.add(lineTwoLabel, BorderLayout.SOUTH);

		add(text, BorderLayout.CENTER);

		JButton copy = new JButton(CLIPBOARD_ICON);
		copy.setPreferredSize(new Dimension(40, 40));
		copy.setToolTipText("Copy result to clipboard");
		copy.addActionListener(e ->
		{
			StringSelection selection = new StringSelection(line2);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
		});

		add(copy, BorderLayout.EAST);
	}
}

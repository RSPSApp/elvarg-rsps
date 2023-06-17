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

import javax.swing.JTextField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayField extends JTextField
{
	@AllArgsConstructor
	@Getter
	protected enum Action
	{
		ADDITION("+"), SUBTRACTION("-"), MULTIPLICATION("*"), DIVISION("/");

		private final String character;
	}

	private Integer num1;
	private Integer num2;
	private Action calculatorAction;
	private Integer result;
	private Integer previousResult;
	private String text;
	private boolean finished = true;

	protected void calculateResult()
	{
		if (num1 == null || num2 == null || calculatorAction == null)
		{
			return;
		}
		switch (calculatorAction)
		{
			case ADDITION:
				result = num1 + num2;
				break;
			case SUBTRACTION:
				result = num1 - num2;
				break;
			case MULTIPLICATION:
				result = num1 * num2;
				break;
			case DIVISION:
				if (num2 == 0)
				{
					reset();
					super.setText("Error: Cannot divide by 0!");
					finished = true;
					break;
				}
				result = num1 / num2;
				break;
		}
		previousResult = result;
		finished = true;
	}

	protected void reset()
	{
		num1 = null;
		num2 = null;
		calculatorAction = null;
		result = null;
		text = null;
		update();
	}

	protected void clear()
	{
		reset();
		finished = true;
		previousResult = null;
	}

	protected void update()
	{
		if (num1 == null)
		{
			this.text = "";
		}
		else if (calculatorAction == null)
		{
			this.text = num1.toString();
		}
		else if (num2 == null)
		{
			this.text = String.join(" ", num1.toString(), calculatorAction.getCharacter());
		}
		else if (result == null)
		{
			this.text = String.join(" ", num1.toString(), calculatorAction.getCharacter(), num2.toString());
		}
		else
		{
			this.text = String.join(" ",
				num1.toString(), calculatorAction.getCharacter(), num2.toString(), "=", result.toString());
		}
		super.setText(this.text);
		repaint();
	}

	protected boolean num1IsNegativeZero()
	{
		return num1 == 0 && num1.toString().length() == 2;
	}

	protected boolean num2IsNegativeZero()
	{
		if (num2 == null)
		{
			return false;
		}
		return num2 == 0 && num2.toString().length() == 2;
	}
}

package net.runelite.rs.api;

import net.runelite.mapping.Import;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface RSReflectionCheck
{
	@Import("methods")
	Method[] getMethods();

	@Import("fields")
	Field[] getFields();

	@Import("arguments")
	byte[][][] getArgs();
}

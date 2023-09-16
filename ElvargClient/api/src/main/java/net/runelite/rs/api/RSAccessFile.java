package net.runelite.rs.api;

import net.runelite.mapping.Import;

import java.io.RandomAccessFile;

public interface RSAccessFile
{
	@Import("file")
	RandomAccessFile getFile();

	@Import("offset")
	long getPosition();

	@Import("maxSize")
	long getLength();
}

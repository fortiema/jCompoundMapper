/**
 * A subclass of RandomAccessFile to enable basic buffering to a byte array
 * 
 * Copyright (C) 2009 minddumped.blogspot.com
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see .
 * 
 * http://minddumped.blogspot.com/2009/01/buffered-javaiorandomaccessfile.html
 */

package de.zbit.jcmapper.io.reader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author minddumped.blogspot.com
 */
public class BufferedRandomAccessFile extends RandomAccessFile {

	private final int bufferlength;

	private int buffpos;
	private final byte[] bytebuffer;
	private int maxread;
	private final StringBuilder sb;

	public BufferedRandomAccessFile(File file, String mode) throws FileNotFoundException {
		super(file, mode);
		this.bufferlength = 65536;
		this.bytebuffer = new byte[this.bufferlength];
		this.maxread = 0;
		this.buffpos = 0;
		this.sb = new StringBuilder("0");
	}

	public int getbuffpos() {
		return this.buffpos;
	}

	@Override
	public long getFilePointer() throws IOException {
		return super.getFilePointer() + this.buffpos;
	}

	@Override
	public int read() throws IOException {
		if (this.buffpos >= this.maxread) {
			this.maxread = this.readchunk();
			if (this.maxread == -1) {
				return -1;
			}
		}
		this.buffpos++;
		return this.bytebuffer[this.buffpos - 1];
	}

	private int readchunk() throws IOException {
		final long pos = super.getFilePointer() + this.buffpos;
		super.seek(pos);
		final int read = super.read(this.bytebuffer);
		super.seek(pos);
		this.buffpos = 0;
		return read;
	}

	public String readLineBuffered() throws IOException {
		this.sb.delete(0, this.sb.length());
		int c = -1;
		boolean eol = false;
		while (!eol) {
			switch (c = this.read()) {
			case -1:
			case '\n':
				eol = true;
				break;
			case '\r':
				eol = true;
				final long cur = this.getFilePointer();
				if ((this.read()) != '\n') {
					this.seek(cur);
				}
				break;
			default:
				this.sb.append((char) c);
				break;
			}
		}

		if ((c == -1) && (this.sb.length() == 0)) {
			return null;
		}
		return this.sb.toString();
	}

	@Override
	public void seek(long pos) throws IOException {
		if (this.maxread != -1 && pos < (super.getFilePointer() + this.maxread) && pos > super.getFilePointer()) {
			final Long diff = (pos - super.getFilePointer());
			if (diff < Integer.MAX_VALUE) {
				this.buffpos = diff.intValue();
			} else {
				throw new IOException("something wrong w/ seek");
			}
		} else {
			this.buffpos = 0;
			super.seek(pos);
			this.maxread = this.readchunk();
		}
	}
}
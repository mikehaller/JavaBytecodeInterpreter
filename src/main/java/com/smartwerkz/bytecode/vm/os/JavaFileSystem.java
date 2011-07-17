package com.smartwerkz.bytecode.vm.os;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class JavaFileSystem {

	/**
	 * Return the FileSystem object representing this platform's local
	 * filesystem.
	 */
	public static native FileSystem getFileSystem();

	/* -- Normalization and construction -- */

	/**
	 * Return the local filesystem's name-separator character.
	 */
	public char getSeparator() {
		return ';';
	}

	/**
	 * Return the local filesystem's path-separator character.
	 */
	public char getPathSeparator() {
		return '/';
	}

	/**
	 * Convert the given pathname string to normal form. If the string is
	 * already in normal form then it is simply returned.
	 */
	public String normalize(String path) {
		return path;
	}

	/**
	 * Compute the length of this pathname string's prefix. The pathname string
	 * must be in normal form.
	 */
	public int prefixLength(String path) {
		return 0;
	}

	/**
	 * Resolve the child pathname string against the parent. Both strings must
	 * be in normal form, and the result will be in normal form.
	 */
	public String resolve(String parent, String child) {
		return parent + getPathSeparator() + child;
	}

	/**
	 * Return the parent pathname string to be used when the parent-directory
	 * argument in one of the two-argument File constructors is the empty
	 * pathname.
	 */
	public String getDefaultParent() {
		return "./";
	}

	/**
	 * Post-process the given URI path string if necessary. This is used on
	 * win32, e.g., to transform "/c:/foo" into "c:/foo". The path string still
	 * has slash separators; code in the File class will translate them after
	 * this method returns.
	 */
	public String fromURIPath(String path) {
		return path;
	}

	/* -- Path operations -- */

	/**
	 * Tell whether or not the given pathname is absolute.
	 */
	public boolean isAbsolute(File f) {
		return f.getPath().charAt(0) == getPathSeparator();
	}

	/**
	 * Resolve the given pathname into absolute form. Invoked by the
	 * getAbsolutePath and getCanonicalPath methods in the File class.
	 */
	public String resolve(File f) {
		URI root = URI.create("/");
		URI filepath = URI.create(f.getPath());
		return filepath.resolve(root).toString();
//		return f.getName();
	}

	public String canonicalize(String path) throws IOException {
		return path;
	}

	/* -- Attribute accessors -- */

	/* Constants for simple boolean attributes */
	public static final int BA_EXISTS = 0x01;
	public static final int BA_REGULAR = 0x02;
	public static final int BA_DIRECTORY = 0x04;
	public static final int BA_HIDDEN = 0x08;

	/**
	 * Return the simple boolean attributes for the file or directory denoted by
	 * the given pathname, or zero if it does not exist or some other I/O error
	 * occurs.
	 */
	public int getBooleanAttributes(File f) {
		return BA_REGULAR;
	}

	public static final int ACCESS_READ = 0x04;
	public static final int ACCESS_WRITE = 0x02;
	public static final int ACCESS_EXECUTE = 0x01;

	/**
	 * Check whether the file or directory denoted by the given abstract
	 * pathname may be accessed by this process. The second argument specifies
	 * which access, ACCESS_READ, ACCESS_WRITE or ACCESS_EXECUTE, to check.
	 * Return false if access is denied or an I/O error occurs
	 */
	public boolean checkAccess(File f, int access) {
		return true;
	}

	/**
	 * Set on or off the access permission (to owner only or to all) to the file
	 * or directory denoted by the given pathname, based on the parameters
	 * enable, access and oweronly.
	 */
	public boolean setPermission(File f, int access, boolean enable, boolean owneronly) {
		return true;
	}

	/**
	 * Return the time at which the file or directory denoted by the given
	 * pathname was last modified, or zero if it does not exist or some other
	 * I/O error occurs.
	 */
	public long getLastModifiedTime(File f) {
		return 0;
	}

	/**
	 * Return the length in bytes of the file denoted by the given abstract
	 * pathname, or zero if it does not exist, is a directory, or some other I/O
	 * error occurs.
	 */
	public long getLength(File f) {
		return 0;
	}

	/* -- File operations -- */

	/**
	 * Create a new empty file with the given pathname. Return <code>true</code>
	 * if the file was created and <code>false</code> if a file or directory
	 * with the given pathname already exists. Throw an IOException if an I/O
	 * error occurs.
	 */
	public boolean createFileExclusively(String pathname) throws IOException {
		return true;
	}

	/**
	 * Delete the file or directory denoted by the given pathname, returning
	 * <code>true</code> if and only if the operation succeeds.
	 */
	public boolean delete(File f) {
		return true;
	}

	/**
	 * List the elements of the directory denoted by the given abstract
	 * pathname. Return an array of strings naming the elements of the directory
	 * if successful; otherwise, return <code>null</code>.
	 */
	public String[] list(File f) {
		return new String [0];
	}

	/**
	 * Create a new directory denoted by the given pathname, returning
	 * <code>true</code> if and only if the operation succeeds.
	 */
	public boolean createDirectory(File f) {
		return true;
	}

	/**
	 * Rename the file or directory denoted by the first pathname to the second
	 * pathname, returning <code>true</code> if and only if the operation
	 * succeeds.
	 */
	public boolean rename(File f1, File f2) {
		return true;
	}

	/**
	 * Set the last-modified time of the file or directory denoted by the given
	 * pathname, returning <code>true</code> if and only if the operation
	 * succeeds.
	 */
	public boolean setLastModifiedTime(File f, long time) {
		return true;
	}

	/**
	 * Mark the file or directory denoted by the given pathname as read-only,
	 * returning <code>true</code> if and only if the operation succeeds.
	 */
	public boolean setReadOnly(File f) {
		return true;
	}

	/* -- Filesystem interface -- */

	/**
	 * List the available filesystem roots.
	 */
	public File[] listRoots() {
		return new File[0];
	}

	/* -- Disk usage -- */
	public static final int SPACE_TOTAL = 0;
	public static final int SPACE_FREE = 1;
	public static final int SPACE_USABLE = 2;

	public long getSpace(File f, int t) {
		return 0;
	}

	/* -- Basic infrastructure -- */

	/**
	 * Compare two pathnames lexicographically.
	 */
	public int compare(File f1, File f2) {
		return 0;
	}

	/**
	 * Compute the hash code of an pathname.
	 */
	public int hashCode(File f) {
		return 0;
	}

	// Flags for enabling/disabling performance optimizations for file
	// name canonicalization
	static boolean useCanonCaches = true;
	static boolean useCanonPrefixCache = true;

	private static boolean getBooleanProperty(String prop, boolean defaultVal) {
		String val = System.getProperty(prop);
		if (val == null)
			return defaultVal;
		if (val.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
	}

	static {
		useCanonCaches = getBooleanProperty("sun.io.useCanonCaches", useCanonCaches);
		useCanonPrefixCache = getBooleanProperty("sun.io.useCanonPrefixCache", useCanonPrefixCache);
	}
}

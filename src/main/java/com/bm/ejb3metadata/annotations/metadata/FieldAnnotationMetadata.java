package com.bm.ejb3metadata.annotations.metadata;

import com.bm.ejb3metadata.annotations.JField;

/**
 * This class represents the annotation metadata of a field.
 * 
 * @author Daniel Wiese
 */
public class FieldAnnotationMetadata extends CommonAnnotationMetadata {

	/**
	 * Method on which we got metadata.
	 */
	private JField jField = null;

	/**
	 * Parent metadata.
	 */
	private ClassAnnotationMetadata classAnnotationMetadata = null;

	/**
	 * This field is a field from a super class ?<br>
	 */
	private boolean inherited = false;

	/**
	 * Constructor.
	 * 
	 * @param jField
	 *            the field on which we will set/add metadata
	 * @param classAnnotationMetadata
	 *            the parent metadata.
	 */
	public FieldAnnotationMetadata(final JField jField,
			final ClassAnnotationMetadata classAnnotationMetadata) {
		this.jField = jField;
		this.classAnnotationMetadata = classAnnotationMetadata;
	}

	/**
	 * @return name of the field
	 */
	public String getFieldName() {
		return this.jField.getName();
	}

	/**
	 * @return JMethod object
	 */
	public JField getJField() {
		return this.jField;
	}

	/**
	 * @return string representation
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String titleIndent = " ";
		String indent = "   ";
		// classname
		sb.append(titleIndent);
		sb.append(this.getClass().getName().substring(
				this.getClass().getPackage().getName().length() + 1));
		sb.append("[\n");

		// Add super class toString()
		sb.append(super.toString());

		// Field
		sb.append(indent);
		sb.append("jField=");
		sb.append(jField);
		sb.append("\n");

		// inherited
		if (inherited) {
			sb.append(indent);
			sb.append("inherited=");
			sb.append(inherited);
			sb.append("\n");
		}

		sb.append(titleIndent);
		sb.append("]\n");
		return sb.toString();
	}

	/**
	 * @return true if this method is inherited from a super class
	 */
	public boolean isInherited() {
		return inherited;
	}

	/**
	 * Sets the inheritance of this method.
	 * 
	 * @param inherited
	 *            true if method is from a super class
	 */
	public void setInherited(final boolean inherited) {
		this.inherited = inherited;
	}

	/**
	 * @return parent metadata (class)
	 */
	public ClassAnnotationMetadata getClassAnnotationMetadata() {
		return classAnnotationMetadata;
	}

}

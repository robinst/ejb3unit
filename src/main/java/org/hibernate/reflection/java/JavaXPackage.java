package org.hibernate.reflection.java;

import org.hibernate.reflection.XPackage;

/**
 * @author Paolo Perrotta
 * @author Davide Marchignoli
 */
class JavaXPackage extends JavaXAnnotatedElement implements XPackage {

	public JavaXPackage( Package pkg, JavaXFactory factory ) {
		super( pkg, factory );
	}

	public String getName() {
		return ((Package) toAnnotatedElement()).getName();
	}
}

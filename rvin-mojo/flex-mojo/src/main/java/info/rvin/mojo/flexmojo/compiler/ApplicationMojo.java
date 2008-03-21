/**
 * Flexmojos is a set of maven goals to allow maven users to compile, optimize and test Flex SWF, Flex SWC, Air SWF and Air SWC.
 * Copyright (C) 2008-2012  Marvin Froeder <marvin@flexmojos.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package info.rvin.mojo.flexmojo.compiler;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import flex2.tools.oem.Application;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which touches a timestamp file.
 * 
 * @goal compile-swf
 * @requiresDependencyResolution
 * @phase compile
 */
public class ApplicationMojo extends AbstractFlexCompilerMojo<Application> {

	/**
	 * The file to be compiled
	 * 
	 * @parameter
	 */
	private File sourceFile;

	@Override
	public void setUp() throws MojoExecutionException, MojoFailureException {
		if (sourceFile == null) {
			File[] files = new File(build.getSourceDirectory())
					.listFiles(new FileFilter() {
						public boolean accept(File pathname) {
							return pathname.isFile();
						}
					});

			if (files.length == 1) {
				sourceFile = files[0];
			}
			if (files.length > 1) {
				for (File file : files) {
					if (file.getName().equalsIgnoreCase("main.mxml")
							|| file.getName().equalsIgnoreCase("main.as")) {
						sourceFile = file;
					}
				}
			}
		}
		
		if(sourceFile == null) {
			throw new MojoExecutionException("Source file not found and not expecified!");
		}

		// need to initialize builder before go super
		try {
			builder = new Application(sourceFile);
		} catch (FileNotFoundException e) {
			throw new MojoFailureException("Unable to find " + sourceFile);
		}
		super.setUp();

		builder.setOutput(outputFile);

	}
}

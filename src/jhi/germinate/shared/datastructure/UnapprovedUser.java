/*
 *  Copyright 2017 Sebastian Raubach and Paul Shaw from the
 *  Information and Computational Sciences Group at JHI Dundee
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.shared.datastructure;

import java.io.*;

/**
 * {@link UnapprovedUser} user is a DTO holding information about an unapproved user (a newly registered but not yet approved user).
 *
 * @author Sebastian Raubach
 */
public class UnapprovedUser implements Serializable
{
	private static final long serialVersionUID = -6350229035824380532L;

	public boolean toRegister;
	public String  userUsername;
	public String  userPassword;
	public String  userFullName;
	public String  userEmailAddress;
	public Long    institutionId;
	public String  institutionName;
	public String  institutionAcronym;
	public String  institutionAddress;

	public UnapprovedUser()
	{

	}
}

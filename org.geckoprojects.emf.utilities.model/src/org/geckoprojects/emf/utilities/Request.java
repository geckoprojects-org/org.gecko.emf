/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 * 
 */
package org.geckoprojects.emf.utilities;

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Request</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * General purpose request object
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getId <em>Id</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getObjectId <em>Object Id</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getFrom <em>From</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getTo <em>To</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getPage <em>Page</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getPageSize <em>Page Size</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#isReturnResultSize <em>Return Result Size</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getSorting <em>Sorting</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getFiltering <em>Filtering</em>}</li>
 *   <li>{@link org.geckoprojects.emf.utilities.Request#getProjection <em>Projection</em>}</li>
 * </ul>
 *
 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest()
 * @model
 * @generated
 */
public interface Request extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_Id()
	 * @model
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Object Id</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * One or many primary key filter values
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Object Id</em>' attribute list.
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_ObjectId()
	 * @model
	 * @generated
	 */
	EList<String> getObjectId();

	/**
	 * Returns the value of the '<em><b>From</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The from value for a time range query
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>From</em>' attribute.
	 * @see #setFrom(Date)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_From()
	 * @model
	 * @generated
	 */
	Date getFrom();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#getFrom <em>From</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>From</em>' attribute.
	 * @see #getFrom()
	 * @generated
	 */
	void setFrom(Date value);

	/**
	 * Returns the value of the '<em><b>To</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The to value for a time range query
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>To</em>' attribute.
	 * @see #setTo(Date)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_To()
	 * @model
	 * @generated
	 */
	Date getTo();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#getTo <em>To</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>To</em>' attribute.
	 * @see #getTo()
	 * @generated
	 */
	void setTo(Date value);

	/**
	 * Returns the value of the '<em><b>Page</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Paging: the page offset, should be 1-based index
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Page</em>' attribute.
	 * @see #setPage(int)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_Page()
	 * @model
	 * @generated
	 */
	int getPage();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#getPage <em>Page</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page</em>' attribute.
	 * @see #getPage()
	 * @generated
	 */
	void setPage(int value);

	/**
	 * Returns the value of the '<em><b>Page Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Paging: Entries size per page
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Page Size</em>' attribute.
	 * @see #setPageSize(int)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_PageSize()
	 * @model
	 * @generated
	 */
	int getPageSize();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#getPageSize <em>Page Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Page Size</em>' attribute.
	 * @see #getPageSize()
	 * @generated
	 */
	void setPageSize(int value);

	/**
	 * Returns the value of the '<em><b>Return Result Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Parameter to force the response to return the result size value for the query, if paging is set
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Return Result Size</em>' attribute.
	 * @see #setReturnResultSize(boolean)
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_ReturnResultSize()
	 * @model
	 * @generated
	 */
	boolean isReturnResultSize();

	/**
	 * Sets the value of the '{@link org.geckoprojects.emf.utilities.Request#isReturnResultSize <em>Return Result Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Return Result Size</em>' attribute.
	 * @see #isReturnResultSize()
	 * @generated
	 */
	void setReturnResultSize(boolean value);

	/**
	 * Returns the value of the '<em><b>Sorting</b></em>' containment reference list.
	 * The list contents are of type {@link org.geckoprojects.emf.utilities.Sort}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Sort definition
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Sorting</em>' containment reference list.
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_Sorting()
	 * @model containment="true" keys="index"
	 * @generated
	 */
	EList<Sort> getSorting();

	/**
	 * Returns the value of the '<em><b>Filtering</b></em>' containment reference list.
	 * The list contents are of type {@link org.geckoprojects.emf.utilities.Filter}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Additional filter definition
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Filtering</em>' containment reference list.
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_Filtering()
	 * @model containment="true" keys="index"
	 * @generated
	 */
	EList<Filter> getFiltering();

	/**
	 * Returns the value of the '<em><b>Projection</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The fields to project to
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Projection</em>' attribute list.
	 * @see org.geckoprojects.emf.utilities.UtilPackage#getRequest_Projection()
	 * @model
	 * @generated
	 */
	EList<String> getProjection();

} // Request

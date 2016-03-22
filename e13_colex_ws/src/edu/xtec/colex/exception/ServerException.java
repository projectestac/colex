/*
 * File    : ServerException.java
 * Created : 18-oct-2005 17:56
 * By      : ogalzorriz
 *
 * Col·lex - Web-based educational application for design collections
 * of records, store information, make queries and share them.
 *
 * Copyright (C) 2006 - 2008 Oriol Garcia-Alzorriz & Departament
 * d'Educacio de la Generalitat de Catalunya
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details (see the LICENSE file).
 */

package edu.xtec.colex.exception;

/**
 * Class used to encapsulate all the internal exceptions. For example, when a SOAPExecption is catched it is thrown as a ServerException
 * @author ogalzorriz
 * @version 1.0
 */
public class ServerException extends Exception{
    
    /**
     * Creates a new ServerException from another Exception
     * @param e the Exception to encapsulate
     */
    public ServerException(Exception e) 
    {
        super(e.getClass().getName()+": "+ e.getMessage());
        this.setStackTrace(e.getStackTrace());
    }
}


/*
 * Copyright (c) 2002-2011, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.fillingdirectory.utils;

import fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl;
import fr.paris.lutece.plugins.directory.business.EntryTypeFile;
import fr.paris.lutece.plugins.directory.business.EntryTypeImg;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.service.upload.DirectoryAsynchronousUploadHandler;
import fr.paris.lutece.plugins.directory.utils.DirectoryErrorException;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.blobstore.BlobStoreClientException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


public class TaskFillingDirectoryUtils
{
    /**
     * Return the parameters
     * @param request
     * @param strParameterName
     * @return List<String> return the parameters by type Multiple Choise
     */
    public static List<String> getParameterValuesTypeMultipleChoice( HttpServletRequest request, String strParameterName )
    {
        List<String> lstValue = new ArrayList<String>(  );
        String[] strTabIdField = request.getParameterValues( strParameterName );

        if ( strTabIdField != null )
        {
            for ( String strIdField : strTabIdField )
            {
                lstValue.add( strIdField );
            }
        }

        return lstValue;
    }

    /**
     * Return the parameters
     * @param request
     * @param strParameterName
     * @return List<String> return the parameters
     */
    public static List<String> getParameterValue( HttpServletRequest request, String strParameterName )
    {
        List<String> lstValue = new ArrayList<String>(  );
        String strValueEntry = request.getParameter( strParameterName );
        lstValue.add( strValueEntry );

        return lstValue;
    }

    /**
     * getRecordFieldData by type File
     * @param entry
     * @param record
     * @param request
     * @param bTestDirectoryError
     * @param listRecordField
     * @param locale
     * @param strParameterName
     * @throws DirectoryErrorException
     * @deprecated should use standard IEntry.getRecordFieldData
     * @since 1.0.8
     * @see {@link EntryTypeFile} and {@link EntryTypeImg}
     */
    public static void getRecordFieldDataTypeFile( IEntry entry, Record record, HttpServletRequest request,
        boolean bTestDirectoryError, List<RecordField> listRecordField, Locale locale, String strParameterName )
        throws DirectoryErrorException
    {
        File fileSource = DirectoryUtils.getFileData( strParameterName, request );
        List<RegularExpression> listRegularExpression = entry.getFields(  ).get( 0 ).getRegularExpressionList(  );
        RecordField recordField = new RecordField(  );
        recordField.setEntry( entry );

        if ( entry.isMandatory(  ) && ( fileSource == null ) )
        {
            throw new DirectoryErrorException( entry.getTitle(  ) );
        }

        if ( ( fileSource != null ) && ( listRegularExpression != null ) && ( listRegularExpression.size(  ) != 0 ) &&
                RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !RegularExpressionService.getInstance(  ).isMatches( fileSource.getMimeType(  ), regularExpression ) )
                {
                    throw new DirectoryErrorException( entry.getTitle(  ), regularExpression.getErrorMessage(  ) );
                }
            }
        }

        recordField.setFile( fileSource );
        listRecordField.add( recordField );
    }

    /**
     * Delete the temporary files generated from validating the task from entry type download url.
     * @param entry the entry
     * @param record the record
     * @param plugin the plugin
     */
    public static void doDeleteTempFile( IEntry entry, List<RecordField> listRecordFields, Plugin plugin )
    {
        if ( entry instanceof EntryTypeDownloadUrl && ( listRecordFields != null ) && !listRecordFields.isEmpty(  ) )
        {
            DirectoryAsynchronousUploadHandler handler = DirectoryAsynchronousUploadHandler.getHandler(  );
            List<Field> listFields = entry.getFields(  );

            if ( listFields == null )
            {
                listFields = FieldHome.getFieldListByIdEntry( entry.getIdEntry(  ), plugin );
            }

            Field fieldWSRestUrl = DirectoryUtils.findFieldByTitleInTheList( EntryTypeDownloadUrl.CONSTANT_WS_REST_URL,
                    listFields );
            String strWSRestUrl = fieldWSRestUrl.getValue(  );

            for ( RecordField recordField : listRecordFields )
            {
                if ( ( recordField.getEntry(  ) != null ) &&
                        ( recordField.getEntry(  ).getIdEntry(  ) == entry.getIdEntry(  ) ) )
                {
                    try
                    {
                        handler.doRemoveFile( recordField, entry, strWSRestUrl );
                    }
                    catch ( BlobStoreClientException e )
                    {
                        AppLogService.error( e );
                    }
                }
            }
        }
    }
}
